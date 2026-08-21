package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun LocationSelectionDialog(
    currentAddress: Address,
    savedAddresses: List<Address>,
    onSelectAddress: (Address) -> Unit,
    onAddNewAddress: (Address) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }

    // Egyptian Address Form Fields
    var label by remember { mutableStateOf("البيت الجديد") }
    var governorate by remember { mutableStateOf("القاهرة") }
    var city by remember { mutableStateOf("مدينة نصر") }
    var area by remember { mutableStateOf("مكرم عبيد") }
    var street by remember { mutableStateOf("شارع مكرم عبيد الرئيسي") }
    var buildingNumber by remember { mutableStateOf("عمارة 18") }
    var floor by remember { mutableStateOf("الدور 3") }
    var apartment by remember { mutableStateOf("شقة 302") }
    var landmark by remember { mutableStateOf("بجوار سيتي ستارز") }
    var instructions by remember { mutableStateOf("رن الجرس وسيبه") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showAddForm) "إضافة عنوان جديد في مصر 📍" else "اختر عنوان التوصيل 📍",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!showAddForm) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(savedAddresses) { addr ->
                            val isSelected = addr.id == currentAddress.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        onSelectAddress(addr)
                                        onDismiss()
                                    }
                                    .border(
                                        1.dp,
                                        if (isSelected) MinyooOrangePrimary else Color.Transparent,
                                        RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) MinyooOrangePrimary else MinyooSlateLight
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = addr.label,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = addr.fullAddressText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MinyooSlateLight
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MinyooOrangePrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_new_address_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إضافة عنوان جديد بالتفاصيل", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Add Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = label,
                            onValueChange = { label = it },
                            label = { Text("اسم العنوان (مثلاً: البيت، الشغل)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = governorate,
                                onValueChange = { governorate = it },
                                label = { Text("المحافظة") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("المدينة / الحي") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = street,
                            onValueChange = { street = it },
                            label = { Text("اسم الشارع") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = buildingNumber,
                                onValueChange = { buildingNumber = it },
                                label = { Text("رقم العمارة") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = floor,
                                onValueChange = { floor = it },
                                label = { Text("الدور") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = apartment,
                                onValueChange = { apartment = it },
                                label = { Text("الشقة") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            label = { Text("علامة مميزة (جنب صيدلية أو محل)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("تعليمات خاصة بالمندوب") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { showAddForm = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("رجوع")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val newAddr = Address(
                                        id = "addr_${UUID.randomUUID()}",
                                        label = label,
                                        governorate = governorate,
                                        city = city,
                                        area = "$city - $street",
                                        street = street,
                                        buildingNumber = buildingNumber,
                                        floor = floor,
                                        apartment = apartment,
                                        landmark = landmark,
                                        deliveryInstructions = instructions
                                    )
                                    onAddNewAddress(newAddr)
                                    onSelectAddress(newAddr)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_address_btn")
                            ) {
                                Text("حفظ العنوان", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit,
    onLogout: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تبديل واجهة النظام 🔄",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Text(
                    text = "منصة لقمة تدعم واجهات وأدوار متكاملة في الوقت الفعلي:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MinyooSlateLight,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                UserRole.entries.forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onRoleSelected(role)
                                onDismiss()
                            }
                            .border(
                                1.dp,
                                if (isSelected) MinyooOrangePrimary else Color.Transparent,
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("role_option_${role.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (role) {
                                    UserRole.CUSTOMER -> "🛍️"
                                    UserRole.RESTAURANT_OWNER -> "👨‍🍳"
                                    UserRole.COURIER -> "🛵"
                                    UserRole.ADMIN -> "📊"
                                },
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.titleAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (role) {
                                        UserRole.CUSTOMER -> "تصفح المطاعم، السلة، وتتبع الطلب والتوصيل"
                                        UserRole.RESTAURANT_OWNER -> "استقبال وتجهيز الطلبات وإدارة المنيو والأسعار"
                                        UserRole.COURIER -> "استلام وتوصيل الطلبات وإدارة الأرباح"
                                        UserRole.ADMIN -> "مراقبة الإحصائيات، الكوبونات وإدارة النظام"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MinyooSlateLight
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MinyooOrangePrimary
                                )
                            }
                        }
                    }
                }

                if (onLogout != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_logout_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسجيل الخروج والعودة لشاشة الدخول", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCustomizationDialog(
    product: Product,
    restaurant: Restaurant,
    onAddToCart: (quantity: Int, modifiers: List<SelectedModifier>, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }
    val selectedModifiers = remember { mutableStateMapOf<String, SelectedModifier>() }

    // Auto-select first option for required groups
    LaunchedEffect(product) {
        product.modifierGroups.forEach { group ->
            if (group.isRequired && group.options.isNotEmpty()) {
                val first = group.options.first()
                selectedModifiers[group.id] = SelectedModifier(
                    groupId = group.id,
                    groupTitle = group.title,
                    optionId = first.id,
                    optionName = first.name,
                    price = first.priceModifier
                )
            }
        }
    }

    val unitTotal = product.price + selectedModifiers.values.sumOf { it.price }
    val grandTotal = unitTotal * quantity

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinyooSlateLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "السعر الأساسي: ${product.price.toInt()} جنيه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooOrangePrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Modifier Groups
                    product.modifierGroups.forEach { group ->
                        Text(
                            text = "${group.title} ${if (group.isRequired) "(إجباري)" else "(اختياري)"}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (group.isRequired) MinyooOrangeDark else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        group.options.forEach { option ->
                            val isSingleSelect = group.maxSelection == 1
                            val isSelected = if (isSingleSelect) {
                                selectedModifiers[group.id]?.optionId == option.id
                            } else {
                                selectedModifiers.containsKey("${group.id}_${option.id}")
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MinyooOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (isSingleSelect) {
                                            selectedModifiers[group.id] = SelectedModifier(
                                                groupId = group.id,
                                                groupTitle = group.title,
                                                optionId = option.id,
                                                optionName = option.name,
                                                price = option.priceModifier
                                            )
                                        } else {
                                            val key = "${group.id}_${option.id}"
                                            if (isSelected) {
                                                selectedModifiers.remove(key)
                                            } else {
                                                selectedModifiers[key] = SelectedModifier(
                                                    groupId = group.id,
                                                    groupTitle = group.title,
                                                    optionId = option.id,
                                                    optionName = option.name,
                                                    price = option.priceModifier
                                                )
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isSingleSelect) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = null,
                                                colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                            )
                                        } else {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = null,
                                                colors = CheckboxDefaults.colors(checkedColor = MinyooOrangePrimary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }

                                    if (option.priceModifier > 0) {
                                        Text(
                                            text = "+${option.priceModifier.toInt()} ج",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooOrangePrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Special Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات خاصة بالمطعم (بدون شطة، صوص زيادة...)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sticky Bottom Action Row
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, MinyooBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "تقليل")
                            }
                            Text(
                                text = "$quantity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "زيادة")
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Add to Cart Button
                        Button(
                            onClick = {
                                onAddToCart(quantity, selectedModifiers.values.toList(), notes)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("confirm_add_to_cart_btn")
                        ) {
                            Text(
                                text = "أضف للسلة (${grandTotal.toInt()} ج)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
