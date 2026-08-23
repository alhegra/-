package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ai.AiMealSuggestion
import com.example.data.ai.MinyooAiAssistant
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiFoodAssistantScreen(
    restaurants: List<Restaurant>,
    products: List<Product>,
    onAddMealBundleToCart: (AiMealSuggestion) -> Unit,
    onRestaurantClick: (Restaurant) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val aiAssistant = remember { MinyooAiAssistant() }

    var userPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<AiMealSuggestion>>(emptyList()) }

    val quickPrompts = listOf(
        "عايز أكل يكفيني أنا وصاحبي بحدود 400 جنيه ومش عايز حاجة حارة 🍗",
        "أحلى تحلية وترند في مصر لشخصين 🍨",
        "غدا سريع ورخيص لشخص واحد بحدود 100 جنيه 🍲",
        "عروض برجر كومبو مشبعة مع بطاطس وصوصات 🍔",
        "مشويات ملوكي وكباب وكفتة لعزومة 🥩"
    )

    // Initial load
    LaunchedEffect(Unit) {
        if (suggestions.isEmpty()) {
            isLoading = true
            suggestions = aiAssistant.getRecommendations("اقتراحات مميزة ومتنوعة", restaurants, products)
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCCBC))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "✨", fontSize = 26.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "مساعد لقمة الذكي (AI) 🤖",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MinyooCharcoal
                        )
                        Text(
                            text = "مدعوم بنماذج Gemini لترشيح أفضل وجبات مصرية لميزانيتك",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooCharcoal.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            // Prompt input Box
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "إيه طلبك بالظبط؟ اكتب بالمصري زي ما تحب 👇",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = userPrompt,
                            onValueChange = { userPrompt = it },
                            placeholder = { Text("مثال: عايز وجبة مشبعة بـ 250ج بدون بصل...") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_prompt_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (userPrompt.isNotBlank()) {
                                    coroutineScope.launch {
                                        isLoading = true
                                        suggestions = aiAssistant.getRecommendations(userPrompt, restaurants, products)
                                        isLoading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("ai_generate_suggestions_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اقترحلي وجبات مناسبة 🪄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Prompts Chips
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "أو اختر من الأسئلة الأكثر شيوعاً:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MinyooSlateLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickPrompts) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MinyooOrangeContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    userPrompt = prompt
                                    coroutineScope.launch {
                                        isLoading = true
                                        suggestions = aiAssistant.getRecommendations(prompt, restaurants, products)
                                        isLoading = false
                                    }
                                }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangeDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Suggestions Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الترشيحات المقترحة لك 🎯",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MinyooOrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Suggestions List
            items(suggestions) { suggestion ->
                val rest = restaurants.find { it.id == suggestion.restaurantId } ?: restaurants.first()

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = suggestion.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "من مطعم: ${suggestion.restaurantName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MinyooOrangeDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MinyooWarmYellowLight
                            ) {
                                Text(
                                    text = "حوالي ${suggestion.estimatedTotal.toInt()} ج",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = suggestion.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // AI Reason Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = suggestion.reasonWhy,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooSlateLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Included Dishes Preview
                        suggestion.suggestedProducts.forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = prod.imageUrl,
                                    contentDescription = prod.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = prod.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${prod.price.toInt()} ج",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooOrangePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions Row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { onRestaurantClick(rest) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("زيارة المطعم")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onAddMealBundleToCart(suggestion) },
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("add_bundle_to_cart_btn")
                            ) {
                                Text("أضف الوجبة للسلة 🛍️", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
