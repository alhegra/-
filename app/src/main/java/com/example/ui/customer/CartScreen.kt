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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.repository.SeedData
import com.example.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    appliedCoupon: Coupon?,
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onApplyCoupon: (String) -> Pair<Boolean, String>,
    onRemoveCoupon: () -> Unit,
    onClearCart: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onExploreRestaurantsClick: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    val serviceFee = 5.0
    val grandTotal = (subtotal + deliveryFee + serviceFee - discount).coerceAtLeast(0.0)

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🛒", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "لسه مفيش طلبات في السلة 😋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "اكتشف أحلى المطاعم والأكلات في مصر وابدأ طلبك الآن",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinyooSlateLight
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onExploreRestaurantsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(50.dp)
                        .testTag("explore_food_empty_cart_btn")
                ) {
                    Text("ابدأ أول طلب 🍔", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val restaurantName = cartItems.first().restaurantName

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
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سلة المشتريات 🛍️",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الطلب من: $restaurantName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MinyooSlateLight
                    )
                }

                TextButton(onClick = onClearCart) {
                    Text("تفريغ السلة", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            // Cart Items
            items(cartItems) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.product.imageUrl,
                            contentDescription = item.product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (item.selectedModifiers.isNotEmpty()) {
                                Text(
                                    text = item.selectedModifiers.joinToString(" + ") { "${it.optionName} (${it.price.toInt()}ج)" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooSlateLight
                                )
                            }

                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = "ملاحظة: ${item.notes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooOrangeDark
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${item.totalPrice.toInt()} جنيه",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangePrimary
                            )
                        }

                        // Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, MinyooBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (item.quantity > 1) {
                                        onUpdateQuantity(item.cartItemId, -1)
                                    } else {
                                        onRemoveItem(item.cartItemId)
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                    contentDescription = "تقليل",
                                    tint = if (item.quantity == 1) Color(0xFFEF4444) else MinyooCharcoal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onUpdateQuantity(item.cartItemId, 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "زيادة",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Coupon Code Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "كود الخصم والكوبونات 🎟️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (appliedCoupon != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MinyooGreenLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "الكوبون المطبق: ${appliedCoupon.code} ✅",
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooGreenDark,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "خصم ${discount.toInt()} جنيه",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MinyooGreenDark
                                        )
                                    }
                                    IconButton(onClick = onRemoveCoupon) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "حذف الكوبون",
                                            tint = MinyooGreenDark
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("أدخل الكود (مثال: MINYOO50)") },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_input_field")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val (success, msg) = onApplyCoupon(couponInput)
                                        couponMessage = msg
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("apply_coupon_btn")
                                ) {
                                    Text("تطبيق", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = couponMessage!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MinyooOrangeDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Suggested Coupon Quick Chips
                            Text(
                                text = "كوبونات متاحة لك:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MinyooSlateLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(SeedData.sampleCoupons) { c ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MinyooOrangeContainer,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                couponInput = c.code
                                                val (_, msg) = onApplyCoupon(c.code)
                                                couponMessage = msg
                                            }
                                    ) {
                                        Text(
                                            text = "${c.code} (${c.description.take(18)}...)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooOrangeDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Summary Breakdown Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تفاصيل الحساب والفاتورة 🧾",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المجموع الفرعي للأطباق", color = MinyooSlateLight)
                            Text("${subtotal.toInt()} ج", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رسوم التوصيل", color = MinyooSlateLight)
                            Text(
                                if (deliveryFee == 0.0) "مجاني" else "${deliveryFee.toInt()} ج",
                                color = if (deliveryFee == 0.0) MinyooGreenDark else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رسوم الخدمة والتغليف", color = MinyooSlateLight)
                            Text("${serviceFee.toInt()} ج", fontWeight = FontWeight.SemiBold)
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

                        Divider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MinyooBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "المجموع الإجمالي",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${grandTotal.toInt()} جنيه مصري",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangePrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Checkout Button Sticky Footer
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("الإجمالي المستحق", style = MaterialTheme.typography.labelSmall, color = MinyooSlateLight)
                    Text(
                        "${grandTotal.toInt()} ج",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooOrangePrimary
                    )
                }

                Button(
                    onClick = onProceedToCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .testTag("proceed_to_checkout_btn")
                ) {
                    Text(
                        "متابعة إتمام الطلب 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
