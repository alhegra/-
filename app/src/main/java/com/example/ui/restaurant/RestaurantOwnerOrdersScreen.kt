package com.example.ui.restaurant

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Restaurant
import com.example.ui.theme.*

/**
 * شاشة منفصلة وبسيطة لصاحب المطعم لمتابعة الطلبات الجديدة فقط وتغيير حالتها بزر واحد
 * تعرض: (اسم الزبون، الأصناف، الإجمالي، رقم التليفون) + زرار واحد يغير حالة الطلب مرحلة بمرحلة
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantOwnerOrdersScreen(
    restaurant: Restaurant,
    allRestaurants: List<Restaurant> = emptyList(),
    orders: List<Order>,
    onAdvanceOrderStatus: (String) -> Unit,
    onRoleSwitcherClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onAddSampleOrder: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedRestaurantId by remember { mutableStateOf(restaurant.id) }
    var filterOnlyNewOrders by remember { mutableStateOf(true) }

    val currentRestaurant = remember(selectedRestaurantId, allRestaurants, restaurant) {
        allRestaurants.find { it.id == selectedRestaurantId } ?: restaurant
    }

    // Filter orders for this restaurant or all
    val relevantOrders = remember(orders, selectedRestaurantId, currentRestaurant, filterOnlyNewOrders) {
        val restFiltered = if (selectedRestaurantId == "ALL") {
            orders
        } else {
            orders.filter {
                it.restaurantId == selectedRestaurantId ||
                it.restaurantName.trim() == currentRestaurant.name.trim() ||
                (orders.isNotEmpty() && currentRestaurant.id.startsWith("rest_custom") && it.restaurantId == currentRestaurant.id)
            }
        }

        if (filterOnlyNewOrders) {
            restFiltered.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
        } else {
            restFiltered
        }
    }

    val activeCount = remember(relevantOrders) {
        relevantOrders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }

    // Visual notification state when a new order arrives
    var previousPlacedCount by remember { mutableStateOf<Int?>(null) }
    val currentPlacedCount = remember(relevantOrders) {
        relevantOrders.count { it.status == OrderStatus.PLACED }
    }

    LaunchedEffect(currentPlacedCount) {
        if (previousPlacedCount != null && currentPlacedCount > (previousPlacedCount ?: 0)) {
            try {
                val toneGen = android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 80)
                toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 300)
                kotlinx.coroutines.delay(350)
                toneGen.release()
            } catch (_: Throwable) {}
        }
        previousPlacedCount = currentPlacedCount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Top Restaurant Header
        Surface(
            color = Color(0xFF0F172A),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBackClick != null) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "رجوع",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = MinyooOrangePrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "👨‍🍳", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "شاشة صاحب المطعم",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MinyooGreen
                                ) {
                                    Text(
                                        text = "مباشر 🔴",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${currentRestaurant.name} • فرع ${currentRestaurant.area}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Role Switcher Button
                    Button(
                        onClick = onRoleSwitcherClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("الواجهة 🔄", color = Color(0xFFF1F5F9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats & Quick Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Active Orders Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeCount > 0) Color(0xFFEA580C) else Color(0xFF334155)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الطلبات الجديدة قيد التجهيز:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$activeCount طلب",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Toggle All vs New
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (filterOnlyNewOrders) MinyooOrangePrimary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { filterOnlyNewOrders = true }
                        ) {
                            Text(
                                text = "الجديدة فقط ✨",
                                color = if (filterOnlyNewOrders) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!filterOnlyNewOrders) MinyooOrangePrimary else Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { filterOnlyNewOrders = false }
                        ) {
                            Text(
                                text = "الكل",
                                color = if (!filterOnlyNewOrders) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Orders Content
        if (relevantOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🛎️", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "لا توجد طلبات جديدة معلقة حالياً",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "أي طلب يقوم به العميل سيصل فوراً إلى شاشة المطعم هنا مع بيانات العميل والأصناف.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight,
                            textAlign = TextAlign.Center
                        )

                        if (onAddSampleOrder != null) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onAddSampleOrder,
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إرسال طلب تجريبي للمطبخ 🍳", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                items(relevantOrders, key = { it.id }) { order ->
                    RestaurantOrderItemCard(
                        order = order,
                        onAdvanceStatus = { onAdvanceOrderStatus(order.id) },
                        onCallCustomer = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}"))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

/**
 * كارت الطلب المخصص لصاحب المطعم:
 * يبرز: (اسم الزبون، الأصناف، الإجمالي، رقم التليفون)
 * + زرار واحد يغير حالة الطلب مرحلة بمرحلة
 */
@Composable
fun RestaurantOrderItemCard(
    order: Order,
    onAdvanceStatus: () -> Unit,
    onCallCustomer: () -> Unit
) {
    val isDelivered = order.status == OrderStatus.DELIVERED
    val isCancelled = order.status == OrderStatus.CANCELLED

    // Header color based on stage
    val statusHeaderColor = when (order.status) {
        OrderStatus.PLACED -> Color(0xFFEF4444) // Urgent red/orange (New incoming)
        OrderStatus.CONFIRMED -> Color(0xFFF59E0B) // Amber
        OrderStatus.PREPARING -> Color(0xFF3B82F6) // Blue
        OrderStatus.COURIER_ASSIGNED, OrderStatus.PICKED_UP -> Color(0xFF8B5CF6) // Purple
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF0284C7) // Sky
        OrderStatus.DELIVERED -> MinyooGreenDark // Green
        OrderStatus.CANCELLED -> Color(0xFF64748B)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                if (order.status == OrderStatus.PLACED) Color(0xFFFCA5A5) else Color(0xFFE2E8F0),
                RoundedCornerShape(18.dp)
            )
            .testTag("restaurant_order_card_${order.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Order Number & Live Status Header Bar
            Surface(
                color = statusHeaderColor.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "طلب ${order.orderNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = statusHeaderColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (order.status == OrderStatus.PLACED) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = "جديد 🔔",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusHeaderColor
                    ) {
                        Text(
                            text = order.status.titleAr,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // 2. Customer Name & Phone Number (Required: اسم الزبون + رقم التليفون)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MinyooOrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الزبون: ${order.customerName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MinyooGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = order.customerPhone,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                        }
                    }

                    // Call Customer Button
                    OutlinedButton(
                        onClick = onCallCustomer,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MinyooGreenDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MinyooGreenDark),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال بالزبون",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Items List (Required: الأصناف)
                Text(
                    text = "الأصناف المطلوبة 🍽️",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                order.items.forEachIndexed { index, item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Quantity circle
                                    Surface(
                                        shape = CircleShape,
                                        color = MinyooOrangePrimary,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${item.quantity}",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.product.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                Text(
                                    text = "${item.totalPrice.toInt()} ج",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp
                                )
                            }

                            // Modifiers / options if any
                            if (item.selectedModifiers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "  • خيارات: " + item.selectedModifiers.joinToString("، ") { it.optionName },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }

                            // Chef notes if any
                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "  ⚠️ ملاحظة للشيف: ${item.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooOrangeDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                if (order.deliveryNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📝 ملاحظة التوصيل: ${order.deliveryNotes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // 4. Total Price (Required: الإجمالي) & Payment Method
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "المبلغ الإجمالي:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${order.total.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MinyooOrangePrimary
                            )
                            Text(
                                text = " ج.م",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangePrimary,
                                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                            )
                        }
                    }

                    // Payment method tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (order.paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || order.paymobTransactionId != null) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (order.paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || order.paymobTransactionId != null) "💳 مدفوع فيزا (Paymob)" else "💵 كاش عند الاستلام",
                                color = if (order.paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || order.paymobTransactionId != null) Color(0xFF166534) else Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Visual Step Timeline Bar (From Placed -> Delivered)
                OrderStageProgressBar(currentStatus = order.status)

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Single Action Button to Advance Order Status (Required: زرار واحد يغير حالة الطلب مرحلة بمرحلة)
                SingleStatusAdvanceButton(
                    orderStatus = order.status,
                    onAdvance = onAdvanceStatus
                )
            }
        }
    }
}

/**
 * شريط بياني يوضح مراحل الأوردر الـ 6 لصاحب المطعم
 */
@Composable
fun OrderStageProgressBar(currentStatus: OrderStatus) {
    val steps = listOf(
        OrderStatus.PLACED to "استلام",
        OrderStatus.CONFIRMED to "تأكيد",
        OrderStatus.PREPARING to "تحضير",
        OrderStatus.COURIER_ASSIGNED to "مندوب",
        OrderStatus.PICKED_UP to "استلام",
        OrderStatus.OUT_FOR_DELIVERY to "توصيل",
        OrderStatus.DELIVERED to "تسليم"
    )

    val currentIndex = currentStatus.stepIndex

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (status, label) ->
            val isPassed = currentIndex >= status.stepIndex
            val isCurrent = currentStatus == status

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = when {
                        isCurrent -> MinyooOrangePrimary
                        isPassed -> MinyooGreenDark
                        else -> Color(0xFFCBD5E1)
                    },
                    modifier = Modifier.size(if (isCurrent) 18.dp else 12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isPassed && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MinyooOrangePrimary else if (isPassed) MinyooGreenDark else Color(0xFF94A3B8)
                )
            }
        }
    }
}

/**
 * الزر الموحد لتغيير حالة الطلب مرحلة بمرحلة من تم الاستلام إلى تم التسليم
 */
@Composable
fun SingleStatusAdvanceButton(
    orderStatus: OrderStatus,
    onAdvance: () -> Unit
) {
    when (orderStatus) {
        OrderStatus.PLACED -> {
            Button(
                onClick = onAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("rest_advance_btn_PLACED")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1️⃣ تم الاستلام (قبول وتأكيد الطلب) ✅",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        OrderStatus.CONFIRMED -> {
            Button(
                onClick = onAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("rest_advance_btn_CONFIRMED")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🍳", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2️⃣ جاري التحضير في المطبخ 🔥",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        OrderStatus.PREPARING -> {
            Button(
                onClick = onAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("rest_advance_btn_PREPARING")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.TwoWheeler, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3️⃣ جاهز للتوصيل (تسليم الكابتن) 🛵",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        OrderStatus.COURIER_ASSIGNED, OrderStatus.PICKED_UP -> {
            Button(
                onClick = onAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("rest_advance_btn_PICKED_UP")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4️⃣ في الطريق إلى العميل 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        OrderStatus.OUT_FOR_DELIVERY -> {
            Button(
                onClick = onAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = MinyooGreenDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("rest_advance_btn_OUT_FOR_DELIVERY")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "5️⃣ تأكيد التسليم للزبون بنجاح 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        OrderStatus.DELIVERED -> {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, MinyooGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MinyooGreenDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تم تسليم الطلب للزبون بنجاح ✨ (مكتمل)",
                        color = MinyooGreenDark,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        OrderStatus.CANCELLED -> {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "تم إلغاء هذا الطلب ❌",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
