package com.example.ui.customer

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun CheckoutScreen(
    currentAddress: Address,
    cartItems: List<CartItem>,
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    customerName: String = "أحمد مصطفى",
    customerPhone: String = "01098765432",
    onBackClick: () -> Unit,
    onChangeAddressClick: () -> Unit,
    onConfirmOrder: (paymentMethod: PaymentMethod, notes: String, paymobResult: PaymobPaymentResult?) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH_ON_DELIVERY) }
    var deliveryInstructions by remember { mutableStateOf(currentAddress.deliveryInstructions) }
    var selectedTip by remember { mutableStateOf(10.0) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var showPaymobHostedCheckout by remember { mutableStateOf(false) }
    var paymobErrorMessage by remember { mutableStateOf<String?>(null) }

    val serviceFee = 5.0
    val grandTotal = (subtotal + deliveryFee + serviceFee + selectedTip - discount).coerceAtLeast(0.0)

    val quickInstructionOptions = listOf(
        "رن الجرس واترك الطلب أمام الباب 🔔",
        "اتصل بي فور الوصول عند مدخل العمارة 📞",
        "اترك الطلب مع أمن العمارة / البواب 🏢",
        "تسليم باليد مباشرة 🤝"
    )

    // Paymob Fullscreen Hosted Checkout Screen
    if (showPaymobHostedCheckout) {
        PaymobHostedCheckoutScreen(
            amount = grandTotal,
            cartItems = cartItems,
            customerName = customerName,
            customerPhone = customerPhone,
            onPaymentSuccess = { result ->
                showPaymobHostedCheckout = false
                isPlacingOrder = true
                onConfirmOrder(PaymentMethod.ONLINE_CARD_PAYMOB, deliveryInstructions, result)
            },
            onPaymentFailed = { err ->
                showPaymobHostedCheckout = false
                paymobErrorMessage = "تعذر إتمام الدفع عبر Paymob: $err. يمكنك المحاولة مرة أخرى أو اختيار الدفع عند الاستلام."
            },
            onCancel = {
                showPaymobHostedCheckout = false
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward, // RTL back
                        contentDescription = "رجوع"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إتمام وتأكيد الطلب 🛵",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Paymob Failure Error Banner if any
            if (paymobErrorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تنبيه في عملية الدفع",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = paymobErrorMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                            IconButton(onClick = { paymobErrorMessage = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 1. Delivery Address Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MinyooOrangePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "عنوان التوصيل في مصر",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(onClick = onChangeAddressClick) {
                                Text("تغيير", color = MinyooOrangePrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = currentAddress.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooOrangeDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentAddress.fullAddressText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (currentAddress.landmark.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "العلامة المميزة: ${currentAddress.landmark}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Delivery Instructions
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
                            text = "تعليمات خاصة لمندوب التوصيل 🛵",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        quickInstructionOptions.forEach { option ->
                            val isSelected = deliveryInstructions == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { deliveryInstructions = option }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Payment Method Choice (Primary Options: COD vs Paymob Online Card)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "طريقة الدفع 💳",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MinyooGreenLight
                            ) {
                                Text(
                                    text = "بوابة دفع آمنة 100%",
                                    color = MinyooGreenDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 1: Cash on Delivery (الدفع عند الاستلام)
                        val isCod = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCod) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY }
                                .border(
                                    1.5.dp,
                                    if (isCod) MinyooOrangePrimary else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .testTag("payment_method_COD")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💵", fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "الدفع عند الاستلام (كاش)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCod) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ادفع نقداً لمندوب التوصيل عند استلام الطلب على باب البيت",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight
                                    )
                                }
                                RadioButton(
                                    selected = isCod,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                )
                            }
                        }

                        // Option 2: Paymob Hosted Checkout (الدفع أونلاين بالفيزا)
                        val isOnlineCard = selectedPaymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || selectedPaymentMethod == PaymentMethod.CREDIT_CARD
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isOnlineCard) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethod = PaymentMethod.ONLINE_CARD_PAYMOB }
                                .border(
                                    1.5.dp,
                                    if (isOnlineCard) MinyooOrangePrimary else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .testTag("payment_method_PAYMOB")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💳", fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "الدفع أونلاين بالفيزا / ماستركارد",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOnlineCard) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF0A1E3F)
                                        ) {
                                            Text(
                                                text = "Paymob 🇪🇬",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "صفحة دفع آمنة (Paymob Hosted Checkout) - لا يتم تخزين أي بيانات كارت في التطبيق",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Supported card badges
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFE2E8F0)) {
                                            Text("Visa", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1F71), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                        Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFE2E8F0)) {
                                            Text("Mastercard", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEB001B), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                        Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFFE2E8F0)) {
                                            Text("ميزة 🇪🇬", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006837), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                RadioButton(
                                    selected = isOnlineCard,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                )
                            }
                        }

                        // Option 3: Smart Wallets (Vodafone Cash / InstaPay)
                        val isWallet = selectedPaymentMethod == PaymentMethod.VODAFONE_CASH
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isWallet) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethod = PaymentMethod.VODAFONE_CASH }
                                .border(
                                    1.5.dp,
                                    if (isWallet) MinyooOrangePrimary else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .testTag("payment_method_WALLET")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📱", fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "فودافون كاش / إنستاباي InstaPay",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWallet) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "تحويل سريع عبر المحافظ الإلكترونية المصرية أو InstaPay",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight
                                    )
                                }
                                RadioButton(
                                    selected = isWallet,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Courier Tip (إكرامية كابتن التوصيل)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "إكرامية كابتن التوصيل (اختياري) 🛵",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedTip > 0) {
                                Text(
                                    text = "+${selectedTip.toInt()} ج",
                                    color = MinyooOrangePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "100% من الإكرامية تذهب مباشرة للكابتن تقديراً لمجهوده في حر مصر وزحمتها",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(0.0, 5.0, 10.0, 20.0, 30.0)) { tip ->
                                val isSelected = selectedTip == tip
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MinyooOrangePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedTip = tip }
                                ) {
                                    Text(
                                        text = if (tip == 0.0) "بدون" else "${tip.toInt()} ج",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Final Calculation Breakdown
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
                            text = "ملخص الفاتورة النهائية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المجموع الفرعي", color = MinyooSlateLight)
                            Text("${subtotal.toInt()} ج")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رسوم التوصيل", color = MinyooSlateLight)
                            Text(if (deliveryFee == 0.0) "مجاني" else "${deliveryFee.toInt()} ج")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رسوم الخدمة", color = MinyooSlateLight)
                            Text("${serviceFee.toInt()} ج")
                        }
                        if (selectedTip > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("إكرامية الكابتن", color = MinyooSlateLight)
                                Text("${selectedTip.toInt()} ج")
                            }
                        }
                        if (discount > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("خصم الكوبون", color = MinyooGreenDark, fontWeight = FontWeight.Bold)
                                Text("-${discount.toInt()} ج", color = MinyooGreenDark, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MinyooBorder)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المبلغ الإجمالي للدفع", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text(
                                "${grandTotal.toInt()} جنيه",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MinyooOrangePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Confirm Order Bottom Button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                val isPaymobSelected = selectedPaymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB || selectedPaymentMethod == PaymentMethod.CREDIT_CARD

                Button(
                    onClick = {
                        paymobErrorMessage = null
                        if (isPaymobSelected) {
                            // Open Paymob Hosted Checkout
                            showPaymobHostedCheckout = true
                        } else {
                            // Direct Order Placement for Cash on Delivery / Wallet
                            isPlacingOrder = true
                            onConfirmOrder(selectedPaymentMethod, deliveryInstructions, null)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaymobSelected) Color(0xFF0A1E3F) else MinyooOrangePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isPlacingOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_and_place_order_btn")
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isPaymobSelected) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "المتابعة لدفع Paymob 🔒 (${grandTotal.toInt()} ج)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "تأكيد الطلب الآن 🚀 (${grandTotal.toInt()} ج)",
                                    style = MaterialTheme.typography.titleMedium,
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

