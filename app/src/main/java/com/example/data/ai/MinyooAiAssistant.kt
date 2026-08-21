package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.data.repository.SeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiMealSuggestion(
    val title: String,
    val description: String,
    val restaurantId: String,
    val restaurantName: String,
    val suggestedProducts: List<Product>,
    val estimatedTotal: Double,
    val reasonWhy: String
)

class MinyooAiAssistant {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getRecommendations(
        userPrompt: String,
        restaurants: List<Restaurant>,
        products: List<Product>
    ): List<AiMealSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiResponse = callGeminiApi(apiKey, userPrompt, restaurants, products)
                if (aiResponse.isNotEmpty()) {
                    return@withContext aiResponse
                }
            } catch (e: Exception) {
                // Fallback to smart rule-based matching below
            }
        }

        // Local smart NLP matcher tuned for Egyptian food requests
        return@withContext generateLocalSmartRecommendations(userPrompt, restaurants, products)
    }

    private fun callGeminiApi(
        apiKey: String,
        userPrompt: String,
        restaurants: List<Restaurant>,
        products: List<Product>
    ): List<AiMealSuggestion> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val systemPrompt = """
            أنت مساعد ذكي لتطبيق MINYOO لطلب الطعام في مصر. 
            المستخدم سيطلب منك اقتراح أكل بمواصفات معينة (ميزانية، عدد أشخاص، نوع أكل، حار أو بارد، حلو أو حادق).
            قائمة المطاعم المتاحة: ${restaurants.joinToString { "${it.id}: ${it.name} (${it.cuisines.joinToString()})" }}
            قائمة الأكلات المتاحة: ${products.joinToString { "${it.id}: ${it.name} من مطعم ${it.restaurantId} بسعر ${it.price} ج" }}
            
            أجب بصيغة JSON فقط:
            [
              {
                "title": "اسم الوجبة المقترحة",
                "description": "وصف جذاب باللهجة المصرية",
                "restaurantId": "id المطعم",
                "productIds": ["p_101", "p_102"],
                "reasonWhy": "ليه ده مناسب لطلبك"
              }
            ]
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\n\nطلب المستخدم: $userPrompt"))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return emptyList()

        val body = response.body?.string() ?: return emptyList()
        val rootObj = JSONObject(body)
        val text = rootObj.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        val jsonArray = JSONArray(text)
        val suggestions = mutableListOf<AiMealSuggestion>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val restId = item.optString("restaurantId")
            val rest = restaurants.find { it.id == restId } ?: restaurants.first()
            val prodIds = item.optJSONArray("productIds") ?: JSONArray()
            val matchedProds = mutableListOf<Product>()
            for (j in 0 until prodIds.length()) {
                val pId = prodIds.getString(j)
                products.find { it.id == pId }?.let { matchedProds.add(it) }
            }
            if (matchedProds.isEmpty()) {
                matchedProds.addAll(products.filter { it.restaurantId == rest.id }.take(2))
            }

            val total = matchedProds.sumOf { it.price } + rest.deliveryFee + 5.0
            suggestions.add(
                AiMealSuggestion(
                    title = item.optString("title", "وجبة مينيو المقترحة"),
                    description = item.optString("description", "تشكيلة مختارة تناسب طلبك"),
                    restaurantId = rest.id,
                    restaurantName = rest.name,
                    suggestedProducts = matchedProds,
                    estimatedTotal = total,
                    reasonWhy = item.optString("reasonWhy", "تناسب الميزانية والذوق المطلوب تماماً!")
                )
            )
        }
        return suggestions
    }

    private fun generateLocalSmartRecommendations(
        userPrompt: String,
        restaurants: List<Restaurant>,
        products: List<Product>
    ): List<AiMealSuggestion> {
        val query = userPrompt.lowercase()
        val suggestions = mutableListOf<AiMealSuggestion>()

        // 1. Budget extraction (e.g. 400, 100, 200, 500)
        val budgetMatch = Regex("""\b(\d{2,4})\b""").find(query)?.value?.toDoubleOrNull() ?: 350.0
        val isNotSpicy = query.contains("مش حار") || query.contains("مش سبايسي") || query.contains("عادي") || query.contains("بارد")
        val isSweet = query.contains("حلو") || query.contains("حلويات") || query.contains("نوتيلا") || query.contains("قشطوطة") || query.contains("سويت")
        val isTwoPersons = query.contains("صاحبي") || query.contains("اثنين") || query.contains("شخصين") || query.contains("٢") || query.contains("معايا حد")

        if (isSweet) {
            val blaban = restaurants.find { it.id == "rest_4" } ?: restaurants.first()
            val sweetProds = products.filter { it.restaurantId == "rest_4" }
            suggestions.add(
                AiMealSuggestion(
                    title = "كومبو السعادة والتحلية من بلبن 🍨",
                    description = "قشطوطة لوتس ونوتيلا مع كشري حلو بالمانجو والبيستاشيو.",
                    restaurantId = blaban.id,
                    restaurantName = blaban.name,
                    suggestedProducts = sweetProds.take(2),
                    estimatedTotal = sweetProds.take(2).sumOf { it.price } + blaban.deliveryFee + 5.0,
                    reasonWhy = "أحلى تحلية وترند في مصر تكفي شخصين بمزاج عالي وأقل من ميزانيتك!"
                )
            )
        }

        if (query.contains("برجر") || query.contains("ساندوتش") || !isSweet) {
            val burgerRest = restaurants.find { it.id == "rest_2" } ?: restaurants[1]
            val burgerProds = products.filter { it.restaurantId == "rest_2" }
            suggestions.add(
                AiMealSuggestion(
                    title = if (isTwoPersons) "عرض برجر الصحاب المشبع 🍔" else "وجبة شيروكي برجر السريعة",
                    description = "ساندوتش شيروكي بيكون بقري + تشيزي لودد فرايز بمذاق رهيب.",
                    restaurantId = burgerRest.id,
                    restaurantName = burgerRest.name,
                    suggestedProducts = burgerProds.take(2),
                    estimatedTotal = burgerProds.take(2).sumOf { it.price } + burgerRest.deliveryFee + 5.0,
                    reasonWhy = "برجر بقري مشوي 100% غني وممتع، مشطوب من أي سبايسي حار يناسب طلبك."
                )
            )
        }

        if (query.contains("مشاوي") || query.contains("كباب") || query.contains("لحمة") || budgetMatch >= 300) {
            val kababgy = restaurants.find { it.id == "rest_3" } ?: restaurants[2]
            val kababProds = products.filter { it.restaurantId == "rest_3" }
            suggestions.add(
                AiMealSuggestion(
                    title = "صينية قصر الكبابجي الملكية لشخصين 🍗",
                    description = "نصف كيلو كباب وكفتة وريش ضاني مشوية عالفحم مع أرز وطحينة وعيش سخن.",
                    restaurantId = kababgy.id,
                    restaurantName = kababgy.name,
                    suggestedProducts = listOfNotNull(kababProds.firstOrNull()),
                    estimatedTotal = (kababProds.firstOrNull()?.price ?: 390.0) + kababgy.deliveryFee + 5.0,
                    reasonWhy = "أكلة ملوكي تشبعكم ومظبوطة بالضبط على حدود ميزانية الـ ${budgetMatch.toInt()} جنيه!"
                )
            )
        }

        if (query.contains("كشري") || query.contains("مصري") || query.contains("سريع") || suggestions.size < 2) {
            val koshary = restaurants.find { it.id == "rest_1" } ?: restaurants[0]
            val kosharyProds = products.filter { it.restaurantId == "rest_1" }
            suggestions.add(
                AiMealSuggestion(
                    title = "وليمة كشري أبو طارق سوبر لوكس 🍲",
                    description = "2 علبة كشري سوبر لوكس + طاجن مكرونة باللحمة البلدي + أرز باللبن.",
                    restaurantId = koshary.id,
                    restaurantName = koshary.name,
                    suggestedProducts = kosharyProds.take(3),
                    estimatedTotal = kosharyProds.take(3).sumOf { it.price } + koshary.deliveryFee + 5.0,
                    reasonWhy = "الأكلة الشعبية رقم 1 في مصر، توفير عالي، كميات مشبعة جداً وأسرع وقت توصيل (25 دقيقة)!"
                )
            )
        }

        return suggestions.take(3)
    }
}
