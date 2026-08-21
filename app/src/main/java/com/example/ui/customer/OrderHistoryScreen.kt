package com.example.ui.customer

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun OrderHistoryScreen(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit,
    onReorderClick: (Order) -> Unit,
    onExploreClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Active, 1: Past

    val activeOrders = remember(orders) {
        orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }
    val pastOrders = remember(orders) {
        orders.filter { it.status == OrderStatus.DELIVERED || it.status == OrderStatus.CANCELLED }
    }

    val displayedOrders = if (selectedTab == 0) activeOrders else pastOrders

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
                    text = "طلباتي 🛵",
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
                                "الطلبات الحالية (${activeOrders.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "الطلبات السابقة (${pastOrders.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (displayedOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (selectedTab == 0) "🛵" else "📋", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (selectedTab == 0) "مفيش طلبات قيد التوصيل حالياً" else "لسه مفيش طلبات سابقة مكتملة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اطلب الآن أشهى الأكلات واستمتع بأسرع دليفري في مصر",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinyooSlateLight
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("تصفح المطاعم 🍔", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
            ) {
                items(displayedOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onOrderClick(order) }
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
                            .testTag("order_item_${order.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = order.restaurantName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "رقم الطلب: ${order.orderNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MinyooSlateLight
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when (order.status) {
                                        OrderStatus.DELIVERED -> MinyooGreenLight
                                        OrderStatus.CANCELLED -> Color(0xFFFEE2E2)
                                        else -> MinyooOrangeContainer
                                    }
                                ) {
                                    Text(
                                        text = order.status.titleAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            OrderStatus.DELIVERED -> MinyooGreenDark
                                            OrderStatus.CANCELLED -> Color(0xFFDC2626)
                                            else -> MinyooOrangeDark
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = order.items.joinToString(" + ") { "${it.quantity}x ${it.product.name}" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinyooSlateLight,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${order.total.toInt()} جنيه",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooOrangePrimary
                                )

                                Row {
                                    if (order.status == OrderStatus.DELIVERED) {
                                        Button(
                                            onClick = { onReorderClick(order) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangeContainer),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                "إعادة الطلب",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MinyooOrangeDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Button(
                                        onClick = { onOrderClick(order) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            if (order.status == OrderStatus.DELIVERED) "التفاصيل" else "تتبع الطلب 🚀",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
