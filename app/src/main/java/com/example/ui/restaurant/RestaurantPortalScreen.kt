package com.example.ui.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.ui.theme.*

@Composable
fun RestaurantPortalScreen(
    restaurant: Restaurant,
    orders: List<Order>,
    products: List<Product>,
    onAdvanceOrderStatus: (String) -> Unit,
    onToggleProductAvailability: (String) -> Unit,
    onRoleSwitcherClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Live Orders, 1: Menu Management

    val restOrders = remember(orders, restaurant) {
        orders.filter { it.restaurantId == restaurant.id }
    }
    val activeOrders = remember(restOrders) {
        restOrders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }
    val restProducts = remember(products, restaurant) {
        products.filter { it.restaurantId == restaurant.id }
    }

    val todaySales = remember(restOrders) {
        restOrders.sumOf { it.total }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Portal Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍🍳", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بوابة إدارة المطعم",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${restaurant.name} • فرع ${restaurant.area}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }

                    Button(
                        onClick = onRoleSwitcherClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangeContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تبديل الواجهة 🔄", color = MinyooOrangeDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0F2FE), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مبيعات اليوم", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0369A1))
                        Text("${todaySales.toInt()} ج", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الطلبات الحالية", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0369A1))
                        Text("${activeOrders.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("أصناف المنيو", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0369A1))
                        Text("${restProducts.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MinyooOrangePrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("طلبات المطبخ (${activeOrders.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("إدارة أصناف المنيو (${restProducts.size})", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Live Orders
            if (activeOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛎️", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "لا توجد طلبات معلقة للمطعم حالياً",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "أي طلب جديد من العملاء سيظهر هنا فوراً للتجهيز",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinyooSlateLight
                        )
                    }
                }
            } else {
                val context = LocalContext.current
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
                ) {
                    items(activeOrders, key = { it.id }) { order ->
                        RestaurantOrderItemCard(
                            order = order,
                            onAdvanceStatus = { onAdvanceOrderStatus(order.id) },
                            onCallCustomer = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${order.customerPhone}"))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        } else {
            // Menu Management
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
            ) {
                items(restProducts) { prod ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${prod.price.toInt()} جنيه • ${prod.categoryId}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooOrangePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (prod.isAvailable) "متاح للطلب" else "غير متوفر",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (prod.isAvailable) MinyooGreenDark else Color(0xFFEF4444)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = prod.isAvailable,
                                    onCheckedChange = { onToggleProductAvailability(prod.id) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MinyooGreen)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
