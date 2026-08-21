package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.repository.SeedData
import com.example.ui.theme.*

@Composable
fun AdminPortalScreen(
    orders: List<Order>,
    onAdvanceOrderStatus: (String) -> Unit,
    onCancelOrder: (String) -> Unit,
    onRoleSwitcherClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Orders, 1: Coupons, 2: Analytics

    val totalGMV = remember(orders) { orders.sumOf { it.total } }
    val deliveredCount = remember(orders) { orders.count { it.status == OrderStatus.DELIVERED } }
    val activeCount = remember(orders) { orders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Header
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
                            Text(text = "📊", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "لوحة التحكم والإدارة المركزية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "نظام عمليات MINYOO - جمهورية مصر العربية 🇪🇬",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }

                    Button(
                        onClick = onRoleSwitcherClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCFCE7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تبديل الواجهة 🔄", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Dashboard Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MinyooOrangeContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("إجمالي المبيعات (GMV)", style = MaterialTheme.typography.labelSmall, color = MinyooOrangeDark)
                            Text("${totalGMV.toInt()} ج", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MinyooOrangeDark)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("طلبات مكتملة", style = MaterialTheme.typography.labelSmall, color = Color(0xFF15803D))
                            Text("$deliveredCount طلب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("طلبات نشطة", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0369A1))
                            Text("$activeCount طلب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                        }
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
                        text = { Text("إدارة الطلبات (${orders.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("كوبونات الخصم", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
            ) {
                items(orders) { order ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${order.orderNumber} • ${order.restaurantName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "العميل: ${order.customerName} | العنوان: ${order.deliveryAddress.area}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
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
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "المبلغ: ${order.total.toInt()} ج • الدفع: ${order.paymentMethod.titleAr} (${order.paymentStatus})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED) {
                                    Button(
                                        onClick = { onAdvanceOrderStatus(order.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("تقديم الحالة ⏩", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = { onCancelOrder(order.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("إلغاء الطلب ❌", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Coupons list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
            ) {
                items(SeedData.sampleCoupons) { coupon ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "كود الخصم: ${coupon.code}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooOrangeDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = coupon.description, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "الحد الأدنى للطلب: ${coupon.minOrder.toInt()} ج • أقصى خصم: ${coupon.maxDiscount.toInt()} ج",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooSlateLight
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MinyooGreenLight
                            ) {
                                Text(
                                    text = "نشط ومفعل ✅",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
