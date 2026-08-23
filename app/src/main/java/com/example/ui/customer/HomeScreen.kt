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
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.data.repository.SeedData
import com.example.ui.components.BasicTextFieldWithPlaceholder
import com.example.ui.components.CategoryChipItem
import com.example.ui.components.CategoryGridItem
import com.example.ui.components.ProductCardItem
import com.example.ui.components.RestaurantCard
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    restaurants: List<Restaurant>,
    products: List<Product>,
    favoriteIds: Set<String>,
    lastOrder: Order?,
    onRestaurantClick: (Restaurant) -> Unit,
    onProductClick: (Product, Restaurant) -> Unit,
    onQuickAddProduct: (Product, Restaurant) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onAiAssistantClick: () -> Unit,
    onReorderClick: (Order) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "OFFERS", "FAST", "TOP_RATED"

    val filteredRestaurants = remember(restaurants, searchQuery, selectedCategory, selectedFilter) {
        var list = restaurants

        if (selectedCategory != "الكل") {
            list = list.filter { it.cuisines.contains(selectedCategory) }
        }

        if (searchQuery.isNotBlank()) {
            val query = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(query) ||
                        it.nameEn.lowercase().contains(query) ||
                        it.cuisines.any { c -> c.lowercase().contains(query) }
            }
        }

        when (selectedFilter) {
            "OFFERS" -> list.filter { !it.discountBadge.isNullOrEmpty() }
            "FAST" -> list.sortedBy { it.deliveryTimeMinutes }
            "TOP_RATED" -> list.sortedByDescending { it.rating }
            else -> list
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // High Density Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MinyooSearchBg)
                    .clickable { /* focus handled */ }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = MinyooSlateMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextFieldWithPlaceholder(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "بتدور على مطعم أو أكلة؟",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_bar")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "مسح",
                                tint = MinyooSlateMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4-Column Quick Categories Grid (High Density)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            val top4Categories = listOf(
                "برجر" to "🍔",
                "بيتزا" to "🍕",
                "كشري ومصري" to "🥘",
                "شاورما وفراخ" to "🍗"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                top4Categories.forEach { (catName, emoji) ->
                    CategoryGridItem(
                        title = catName,
                        emoji = emoji,
                        isSelected = selectedCategory == catName,
                        onClick = {
                            selectedCategory = if (selectedCategory == catName) "الكل" else catName
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // High Density Exclusive Promo Banner
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("exclusive_promo_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF5400), Color(0xFFFF8A00))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.75f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White
                        ) {
                            Text(
                                text = "عرض حصري",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MinyooOrangePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "خصم ٣٠٪ على أول ٥ طلبات!",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 22.sp
                        )

                        Text(
                            text = "كود: MINYOO2024",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Decorative Emoji & Glow in Background
                    Text(
                        text = "🥘",
                        fontSize = 52.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 8.dp)
                    )
                }
            }
        }

        // Smart AI Food Assistant Card Trigger
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MinyooSurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onAiAssistantClick() }
                    .border(1.dp, MinyooBorder, RoundedCornerShape(20.dp))
                    .testTag("ai_assistant_home_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MinyooOrangeContainer)
                            .border(1.dp, MinyooOrangeLight, CircleShape)
                    ) {
                        Text(text = "✨", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مساعد لقمة الذكي (AI) 🤖",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinyooCharcoal
                        )
                        Text(
                            text = "محتار تطلب إيه؟ اقترحلك أكلة تناسب ميزانيتك بضغطة واحدة",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MinyooSlateLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowBack, // RTL arrow forward
                        contentDescription = null,
                        tint = MinyooOrangePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // All Categories Horizontal Scroll
        item {
            Spacer(modifier = Modifier.height(14.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SeedData.categories) { catName ->
                    CategoryChipItem(
                        title = catName,
                        icon = getCategoryIcon(catName),
                        isSelected = selectedCategory == catName,
                        onClick = {
                            selectedCategory = if (selectedCategory == catName) "الكل" else catName
                        }
                    )
                }
            }
        }

        // Reorder Section (if last order exists)
        if (lastOrder != null && lastOrder.items.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MinyooSurfaceLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔁", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "اطلب تاني من ${lastOrder.restaurantName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = { onReorderClick(lastOrder) },
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("reorder_btn")
                            ) {
                                Text("إعادة الطلب", style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = lastOrder.items.joinToString(" + ") { "${it.quantity}x ${it.product.name}" },
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MinyooSlateLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Restaurant Ranking & Filter Chips (High Density)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مطاعم قريبة منك",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = MinyooCharcoal
                )
                Text(
                    text = "عرض الكل (${filteredRestaurants.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinyooOrangePrimary,
                    modifier = Modifier.clickable {
                        selectedCategory = "الكل"
                        selectedFilter = "ALL"
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Sort / Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("الكل", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "OFFERS",
                        onClick = { selectedFilter = "OFFERS" },
                        label = { Text("عروض وخصومات 🔥", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "FAST",
                        onClick = { selectedFilter = "FAST" },
                        label = { Text("أسرع توصيل ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "TOP_RATED",
                        onClick = { selectedFilter = "TOP_RATED" },
                        label = { Text("الأعلى تقييماً ⭐", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Restaurants List
        if (filteredRestaurants.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔍", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "مفيش مطاعم مطابقة للبحث",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "جرب تبحث باسم تاني أو غير القسم المختار",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinyooSlateLight
                    )
                }
            }
        } else {
            items(filteredRestaurants) { rest ->
                val isFav = favoriteIds.contains(rest.id)
                RestaurantCard(
                    restaurant = rest,
                    isFavorite = isFav,
                    onFavoriteToggle = { onFavoriteToggle(rest.id, true) },
                    onClick = { onRestaurantClick(rest) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
