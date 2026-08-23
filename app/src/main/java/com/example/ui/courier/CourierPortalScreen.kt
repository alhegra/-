package com.example.ui.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun CourierPortalScreen(
    orders: List<Order>,
    onAdvanceOrderStatus: (String) -> Unit,
    onRoleSwitcherClick: () -> Unit
) {
    var isOnline by remember { mutableStateOf(true) }

    val activeDelivery = remember(orders) {
        orders.firstOrNull { it.status == OrderStatus.PREPARING || it.status == OrderStatus.COURIER_ASSIGNED || it.status == OrderStatus.PICKED_UP || it.status == OrderStatus.OUT_FOR_DELIVERY }
    }

    val completedDeliveriesCount = remember(orders) {
        orders.count { it.status == OrderStatus.DELIVERED }
    }
    val todayEarnings = remember(completedDeliveriesCount) {
        completedDeliveriesCount * 30.0 + 25.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Courier Header
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
                            Icon(
                                imageVector = Icons.Default.DeliveryDining,
                                contentDescription = null,
                                tint = MinyooOrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تطبيق كابتن التوصيل",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "كابتن إبراهيم حسن • موتوسيكل هوندا",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }

                    Button(
                        onClick = onRoleSwitcherClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E8FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تبديل الواجهة 🔄", color = Color(0xFF6B21A8), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Online/Offline Status & Earnings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isOnline) MinyooGreenLight else Color(0xFFFEE2E2), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isOnline) "🟢 متصل وجاهز لاستلام طلبات" else "🔴 غير متاح حالياً",
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) MinyooGreenDark else Color(0xFFDC2626),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "أرباح اليوم: ${todayEarnings.toInt()} ج (${completedDeliveriesCount} طلبات تم تسليمها)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOnline) MinyooGreenDark else Color(0xFFDC2626)
                        )
                    }

                    Switch(
                        checked = isOnline,
                        onCheckedChange = { isOnline = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MinyooGreen)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Active Assigned Trip
            if (activeDelivery != null) {
                item {
                    Text(
                        text = "الطلب المسند إليك حالياً 📦",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, MinyooOrangePrimary, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "طلب ${activeDelivery.orderNumber}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MinyooOrangeContainer
                                ) {
                                    Text(
                                        text = activeDelivery.status.titleAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MinyooOrangeDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Step 1: Restaurant Pickup
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "1. الاستلام من المطعم 🏬",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MinyooOrangeDark
                                    )
                                    Text(text = activeDelivery.restaurantName, fontWeight = FontWeight.Bold)
                                    Text(text = "المنطقة: ${activeDelivery.restaurantArea}", style = MaterialTheme.typography.bodySmall, color = MinyooSlateLight)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Step 2: Customer Delivery
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "2. التوصيل للعميل 📍",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MinyooGreenDark
                                    )
                                    Text(text = "${activeDelivery.customerName} (${activeDelivery.customerPhone})", fontWeight = FontWeight.Bold)
                                    Text(text = activeDelivery.deliveryAddress.fullAddressText, style = MaterialTheme.typography.bodySmall)
                                    if (activeDelivery.deliveryNotes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "⚠️ تعليمات العميل: ${activeDelivery.deliveryNotes}", style = MaterialTheme.typography.labelSmall, color = MinyooOrangeDark)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Stepper for Courier
                            Button(
                                onClick = { onAdvanceOrderStatus(activeDelivery.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("courier_action_btn")
                            ) {
                                Text(
                                    text = when (activeDelivery.status) {
                                        OrderStatus.PREPARING -> "تم الوصول للمطعم واستلام الأكل"
                                        OrderStatus.COURIER_ASSIGNED -> "استلام الطلب من الشيف"
                                        OrderStatus.PICKED_UP -> "بدء التحرك لعنوان العميل"
                                        OrderStatus.OUT_FOR_DELIVERY -> "تم التسليم للعميل بنجاح واستلام المبلغ"
                                        else -> "تحديث حالة التوصيل"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = null,
                                tint = MinyooOrangePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا يوجد طلب قيد التوصيل الآن",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "أنت في قائمة الانتظار، سيتم إسناد أقرب طلب في منطقتك فوراً",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinyooSlateLight
                            )
                        }
                    }
                }
            }
        }
    }
}
