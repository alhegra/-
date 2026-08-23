package com.example.ui.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.ui.components.OrderStatusTimelineView
import com.example.ui.components.StarRatingInput
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    order: Order,
    onBackClick: () -> Unit,
    onSimulateNextStep: () -> Unit,
    onCancelOrder: () -> Unit,
    onSupportClick: () -> Unit,
    onSubmitReview: (Int, String) -> Unit = { _, _ -> }
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "تتبع الطلب ${order.orderNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = order.restaurantName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }
                }

                TextButton(onClick = onSupportClick) {
                    Icon(imageVector = Icons.Outlined.HeadsetMic, contentDescription = null, tint = MinyooOrangePrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("المساعدة", color = MinyooOrangePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ETA & Status Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (order.status == OrderStatus.DELIVERED) MinyooGreenLight else MinyooOrangeContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusIcon = when (order.status) {
                            OrderStatus.PLACED -> Icons.Filled.Schedule
                            OrderStatus.CONFIRMED -> Icons.Filled.Restaurant
                            OrderStatus.PREPARING -> Icons.Filled.Restaurant
                            OrderStatus.COURIER_ASSIGNED -> Icons.Filled.DeliveryDining
                            OrderStatus.PICKED_UP -> Icons.Filled.ShoppingBag
                            OrderStatus.OUT_FOR_DELIVERY -> Icons.Filled.LocalShipping
                            OrderStatus.DELIVERED -> Icons.Filled.CheckCircle
                            OrderStatus.CANCELLED -> Icons.Filled.Cancel
                        }
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = if (order.status == OrderStatus.DELIVERED) MinyooGreenDark else MinyooOrangePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.status.titleAr,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (order.status == OrderStatus.DELIVERED) MinyooGreenDark else MinyooOrangeDark
                            )
                            Text(
                                text = if (order.status == OrderStatus.DELIVERED)
                                    "ألف هنا وشفا! وصل الطلب إلى عنوانك"
                                else
                                    "الوقت المقدر للوصول: حوالي ${order.estimatedMinutes} دقيقة",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (order.status == OrderStatus.DELIVERED) MinyooGreenDark else MinyooCharcoal
                            )
                        }
                    }
                }
            }

            // Realtime Interactive Stepper Timeline
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OrderStatusTimelineView(currentStatus = order.status)
            }

            // Driver Card (when assigned)
            if (order.status != OrderStatus.PLACED && order.status != OrderStatus.CANCELLED) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MinyooOrangeContainer,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        tint = MinyooOrangePrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.courierName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${order.courierVehicle} • ⭐ 4.9",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooSlateLight
                                )
                            }

                            // Call & Chat buttons
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MinyooGreenLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "اتصال بالكابتن",
                                    tint = MinyooGreenDark
                                )
                            }
                        }
                    }
                }
            }

            // Receipt & Ordered Items Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تفاصيل الطلب والأصناف 🧾",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.quantity}x ${item.product.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (item.selectedModifiers.isNotEmpty()) {
                                        Text(
                                            text = item.selectedModifiers.joinToString(" + ") { it.optionName },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MinyooSlateLight
                                        )
                                    }
                                }
                                Text(
                                    text = "${item.totalPrice.toInt()} ج",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MinyooBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("طريقة الدفع:", color = MinyooSlateLight)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(order.paymentMethod.titleAr, fontWeight = FontWeight.Bold)
                                if (order.paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || order.paymobTransactionId != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MinyooGreenLight
                                    ) {
                                        Text(
                                            text = "مدفوع بالفيزا 🔒",
                                            color = MinyooGreenDark,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (order.paymobTransactionId != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("رقم عملية Paymob:", color = MinyooSlateLight, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    order.paymobTransactionId ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0A1E3F)
                                )
                            }
                        }

                        if (order.maskedCardNumber != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("البطاقة المستخدمة:", color = MinyooSlateLight, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    order.maskedCardNumber ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المبلغ الإجمالي:", fontWeight = FontWeight.Bold)
                            Text(
                                "${order.total.toInt()} جنيه",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangePrimary
                            )
                        }
                    }
                }
            }

            // Live Simulator Action Button (For interactive testing of the full state machine)
            if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "محاكاة دورة حياة الطلب الحية:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = onSimulateNextStep,
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_next_order_status_btn")
                            ) {
                                Text("تقديم حالة الطلب للمرحلة التالية", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Delivery Rating Card (if delivered)
            if (order.status == OrderStatus.DELIVERED) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MinyooGreenLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "كيف كانت تجربتك معنا؟",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MinyooGreenDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (reviewSubmitted) {
                                Text(
                                    text = "شكراً لمشاركتك رأيك معنا! تم حفظ تقييمك بنجاح",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MinyooGreenDark,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                StarRatingInput(
                                    rating = rating,
                                    onRatingChanged = { rating = it },
                                    starSize = 32.dp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    placeholder = { Text("اكتب تعليقك على جودة الأكل وسرعة الكابتن...") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        reviewSubmitted = true
                                        onSubmitReview(rating, reviewComment)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MinyooGreenDark),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("submit_review_btn")
                                ) {
                                    Text("إرسال التقييم", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Cancel Order Option (Only available before preparing)
            if (order.status == OrderStatus.PLACED) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onCancelOrder,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cancel_order_btn")
                    ) {
                        Text("إلغاء الطلب", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
