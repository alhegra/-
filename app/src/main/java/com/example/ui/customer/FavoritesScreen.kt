package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.ui.components.ProductCardItem
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.*

@Composable
fun FavoritesScreen(
    restaurants: List<Restaurant>,
    products: List<Product>,
    favoriteIds: Set<String>,
    onRestaurantClick: (Restaurant) -> Unit,
    onProductClick: (Product, Restaurant) -> Unit,
    onQuickAddProduct: (Product, Restaurant) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onExploreClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Restaurants, 1: Dishes

    val favoriteRestaurants = remember(restaurants, favoriteIds) {
        restaurants.filter { favoriteIds.contains(it.id) }
    }
    val favoriteProducts = remember(products, favoriteIds) {
        products.filter { favoriteIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "المفضلة ❤️",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MinyooOrangePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "المطاعم المفضلة (${favoriteRestaurants.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "الأكلات والأطباق (${favoriteProducts.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            if (favoriteRestaurants.isEmpty()) {
                EmptyFavoritesView(message = "مفيش مطاعم في المفضلة لسه", onExploreClick = onExploreClick)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
                ) {
                    items(favoriteRestaurants) { rest ->
                        RestaurantCard(
                            restaurant = rest,
                            isFavorite = true,
                            onFavoriteToggle = { onFavoriteToggle(rest.id, true) },
                            onClick = { onRestaurantClick(rest) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        } else {
            if (favoriteProducts.isEmpty()) {
                EmptyFavoritesView(message = "مفيش أكلات في المفضلة لسه", onExploreClick = onExploreClick)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
                ) {
                    items(favoriteProducts) { prod ->
                        val rest = restaurants.find { it.id == prod.restaurantId } ?: restaurants.first()
                        ProductCardItem(
                            product = prod,
                            onClick = { onProductClick(prod, rest) },
                            onQuickAdd = { onQuickAddProduct(prod, rest) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesView(message: String, onExploreClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "❤️", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "اضغط على علامة القلب عند تصفح أي مطعم أو أكلة لحفظها هنا",
                style = MaterialTheme.typography.bodyMedium,
                color = MinyooSlateLight
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onExploreClick,
                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("تصفح واكتشف 🍔", fontWeight = FontWeight.Bold)
            }
        }
    }
}
