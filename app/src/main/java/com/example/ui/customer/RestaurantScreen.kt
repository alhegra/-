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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.data.model.Review
import com.example.ui.components.ProductCardItem
import com.example.ui.theme.*

@Composable
fun RestaurantScreen(
    restaurant: Restaurant,
    products: List<Product>,
    cartItems: List<CartItem>,
    isFavorite: Boolean,
    restaurantReviews: List<Review> = emptyList(),
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onProductClick: (Product) -> Unit,
    onQuickAddProduct: (Product) -> Unit,
    onViewCartClick: () -> Unit
) {
    val restProducts = remember(products, restaurant) {
        products.filter { it.restaurantId == restaurant.id }
    }

    val categories = remember(restProducts) {
        listOf("الكل") + restProducts.map { it.categoryId }.distinct()
    }

    var selectedCategory by remember { mutableStateOf("الكل") }

    val displayedProducts = remember(restProducts, selectedCategory) {
        if (selectedCategory == "الكل") restProducts
        else restProducts.filter { it.categoryId == selectedCategory }
    }

    val currentCartCount = remember(cartItems, restaurant) {
        cartItems.filter { it.restaurantId == restaurant.id }.sumOf { it.quantity }
    }
    val currentCartTotal = remember(cartItems, restaurant) {
        cartItems.filter { it.restaurantId == restaurant.id }.sumOf { it.totalPrice }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Image with Back & Fav Buttons
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    AsyncImage(
                        model = restaurant.coverUrl,
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x99000000), Color.Transparent, Color(0xB3000000))
                                )
                            )
                    )

                    // Top Action Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .testTag("restaurant_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward, // RTL back
                                contentDescription = "رجوع",
                                tint = MinyooCharcoal
                            )
                        }

                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .testTag("restaurant_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "المفضلة",
                                tint = if (isFavorite) Color(0xFFEF4444) else MinyooCharcoal
                            )
                        }
                    }

                    // Restaurant Logo Floating on Bottom
                    AsyncImage(
                        model = restaurant.logoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp, bottom = 12.dp)
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                    )
                }
            }

            // Restaurant Info Section
            item {
                Card(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-12).dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = restaurant.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MinyooGreenLight
                            ) {
                                Text(
                                    text = if (restaurant.isOpen) "مفتوح الآن ✅" else "مغلق حالياً",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = restaurant.cuisines.joinToString(" • ") + " | " + restaurant.area,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinyooSlateLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Row (Rating, Time, Delivery Fee, Min Order)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = MinyooWarmYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${restaurant.rating}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Text(
                                    text = "${restaurant.reviewCount} تقييم",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooSlateLight
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(1.dp),
                                color = MinyooBorder
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${restaurant.deliveryTimeMinutes} دقيقة",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "وقت التوصيل",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooSlateLight
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(1.dp),
                                color = MinyooBorder
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (restaurant.deliveryFee == 0.0) "مجاني" else "${restaurant.deliveryFee.toInt()} ج",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (restaurant.deliveryFee == 0.0) MinyooGreenDark else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "رسوم التوصيل",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooSlateLight
                                )
                            }
                        }
                    }
                }
            }

            // Menu Category Tabs
            item {
                Text(
                    text = "قائمة الطعام والوجبات 📋",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MinyooOrangePrimary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .border(1.dp, if (isSelected) MinyooOrangePrimary else MinyooBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Products List
            items(displayedProducts) { product ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    ProductCardItem(
                        product = product,
                        onClick = { onProductClick(product) },
                        onQuickAdd = { onQuickAddProduct(product) }
                    )
                }
            }

            // Customer Reviews Section Header & List
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "آراء العملاء والتقييمات ⭐",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinyooCharcoal
                        )
                        Text(
                            text = "${restaurant.reviewCount} تقييم إجمالي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (restaurantReviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد تقييمات بعد. كن أول من يقيّم هذا المطعم!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinyooSlateLight
                        )
                    }
                }
            } else {
                items(restaurantReviews) { review ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = review.customerName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MinyooCharcoal
                                )
                                Text(
                                    text = review.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooSlateLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < review.rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = MinyooWarmYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${review.rating}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooCharcoal
                                )
                            }
                            if (review.comment.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = review.comment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MinyooCharcoal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Cart Sticky Banner (if items exist for this restaurant)
        if (currentCartCount > 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MinyooCharcoal,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onViewCartClick() }
                    .testTag("floating_cart_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MinyooOrangePrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$currentCartCount",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "عرض السلة ومتابعة الطلب",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "من ${restaurant.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MinyooSlateLight
                            )
                        }
                    }

                    Text(
                        text = "${currentCartTotal.toInt()} ج",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooWarmYellow
                    )
                }
            }
        }
    }
}
