package com.example.ui.customer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CartItem
import com.example.data.model.PaymobPaymentResult
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Paymob Brand Colors & Theme
private val PaymobNavy = Color(0xFF0A1E3F)
private val PaymobDarkBlue = Color(0xFF102A43)
private val PaymobCyan = Color(0xFF00A3FF)
private val PaymobGreen = Color(0xFF00C48C)
private val PaymobLightBg = Color(0xFFF4F7FB)
private val PaymobCardSurface = Color(0xFFFFFFFF)
private val PaymobBorderColor = Color(0xFFD9E2EC)

enum class CardScheme(val displayName: String, val badgeColor: Color, val iconText: String) {
    VISA("Visa", Color(0xFF1A1F71), "VISA"),
    MASTERCARD("Mastercard", Color(0xFFEB001B), "MC"),
    MEEZA("ميزة Meeza 🇪🇬", Color(0xFF006837), "MEEZA"),
    UNKNOWN("بطاقة بنكية", Color(0xFF627D98), "CARD")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymobHostedCheckoutScreen(
    amount: Double,
    cartItems: List<CartItem>,
    customerName: String,
    customerPhone: String,
    onPaymentSuccess: (PaymobPaymentResult) -> Unit,
    onPaymentFailed: (String) -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Card Input States (Hosted Frame Simulation - No storage)
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf(customerName) }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showCvv by remember { mutableStateOf(false) }

    // Transaction & Flow States
    var isProcessing by remember { mutableStateOf(false) }
    var processingStageText by remember { mutableStateOf("") }
    var show3DSecureModal by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var otpCountdown by remember { mutableStateOf(120) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var simulateFailureMode by remember { mutableStateOf(false) }

    val orderReference = remember { "PMOB-${(100000..999999).random()}" }

    // Detect card scheme
    val cardScheme = remember(cardNumber) {
        val cleanNumber = cardNumber.replace(" ", "")
        when {
            cleanNumber.startsWith("4") -> CardScheme.VISA
            cleanNumber.startsWith("51") || cleanNumber.startsWith("52") ||
                    cleanNumber.startsWith("53") || cleanNumber.startsWith("54") ||
                    cleanNumber.startsWith("55") || cleanNumber.startsWith("2") -> CardScheme.MASTERCARD
            cleanNumber.startsWith("5078") || cleanNumber.startsWith("5890") ||
                    cleanNumber.startsWith("9870") -> CardScheme.MEEZA
            else -> CardScheme.UNKNOWN
        }
    }

    // Format Card Number with Spaces (XXXX XXXX XXXX XXXX)
    fun formatCardNumber(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }.take(16)
        return digitsOnly.chunked(4).joinToString(" ")
    }

    // Format Expiry Date (MM/YY)
    fun formatExpiryDate(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }.take(4)
        return if (digitsOnly.length >= 3) {
            "${digitsOnly.substring(0, 2)}/${digitsOnly.substring(2)}"
        } else {
            digitsOnly
        }
    }

    // Validate inputs
    val isFormValid = remember(cardNumber, cardHolderName, expiryDate, cvv) {
        val cleanNum = cardNumber.replace(" ", "")
        cleanNum.length in 15..16 &&
                cardHolderName.trim().length >= 3 &&
                expiryDate.length == 5 &&
                cvv.length in 3..4
    }

    // Handle Pay Button Click
    fun startPaymobCheckout() {
        focusManager.clearFocus()
        errorMessage = null
        isProcessing = true
        processingStageText = "جاري إنشاء اتصال آمن ومشفر مع بوابة Paymob..."

        coroutineScope.launch {
            delay(1200)
            processingStageText = "جاري مطابقة بيانات البطاقة مع البنك المركزي المصري 🇪🇬..."
            delay(1000)
            isProcessing = false
            // Launch 3D Secure Verification
            show3DSecureModal = true
        }
    }

    // Handle 3D Secure Completion
    fun complete3DSecureVerification() {
        show3DSecureModal = false
        isProcessing = true
        processingStageText = "جاري تأكيد كود الحماية والخصم عبر Paymob..."

        coroutineScope.launch {
            delay(1500)
            isProcessing = false

            if (simulateFailureMode || (otpInput.isNotEmpty() && otpInput != "123456" && otpInput != "849201")) {
                errorMessage = "تم رفض المعاملة من البنك المصدر للبطاقة: كود التحقق OTP غير صحيح أو رصيد غير كافٍ."
                onPaymentFailed(errorMessage ?: "فشل الدفع عبر Paymob")
            } else {
                val cleanCard = cardNumber.replace(" ", "")
                val masked = if (cleanCard.length >= 4) {
                    "**** **** **** " + cleanCard.takeLast(4)
                } else {
                    "**** **** **** 4242"
                }
                val nowFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                val result = PaymobPaymentResult(
                    isSuccess = true,
                    transactionId = orderReference,
                    authCode = "AUTH_${(1000..9999).random()}",
                    maskedPan = masked,
                    cardScheme = cardScheme.displayName,
                    errorMessage = null,
                    amount = amount,
                    paymentDate = nowFormatted
                )
                onPaymentSuccess(result)
            }
        }
    }

    // 3D Secure OTP Countdown timer
    LaunchedEffect(show3DSecureModal) {
        if (show3DSecureModal) {
            otpCountdown = 120
            while (otpCountdown > 0 && show3DSecureModal) {
                delay(1000)
                otpCountdown--
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaymobLightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Paymob Top Hosted Navigation Header
            Surface(
                color = PaymobNavy,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إلغاء والعودة",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Paymob",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PaymobCyan
                                ) {
                                    Text(
                                        text = "SECURE PAY 🔒",
                                        color = PaymobNavy,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "بوابة دفع إلكتروني معتمدة من البنك المركزي المصري 🇪🇬",
                                color = Color(0xFFBAC7D5),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // SSL Certificate Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF102A43), RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PaymobGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "256-bit SSL",
                            color = PaymobGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scrollable Content Frame
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Merchant & Amount Header Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaymobCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PaymobBorderColor, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "التاجر: initial (Profile PK: 1218391)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PaymobDarkBlue
                                )
                                Text(
                                    text = "رقم المعاملة المرجعي: $orderReference",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF627D98)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MinyooOrangeContainer
                            ) {
                                Text(
                                    text = "طلب أكل 🛵",
                                    color = MinyooOrangeDark,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = PaymobBorderColor)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المبلغ المطلوب سداده:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF334E68)
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${amount.toInt()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = PaymobNavy
                                )
                                Text(
                                    text = " ج.م (EGP)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PaymobCyan,
                                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error Message if any
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFDC2626),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Hosted Payment Card Frame (Paymob Interface)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PaymobCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PaymobBorderColor, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "بيانات البطاقة البنكية 💳",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PaymobNavy
                            )

                            // Accepted Cards Badges
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF0F4F8),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9E2EC))
                                ) {
                                    Text(
                                        "VISA",
                                        color = Color(0xFF1A1F71),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF0F4F8),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9E2EC))
                                ) {
                                    Text(
                                        "Mastercard",
                                        color = Color(0xFFEB001B),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF0F4F8),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9E2EC))
                                ) {
                                    Text(
                                        "ميزة 🇪🇬",
                                        color = Color(0xFF006837),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card Number Field
                        Text(
                            text = "رقم البطاقة (Card Number)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334E68)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { input ->
                                cardNumber = formatCardNumber(input)
                            },
                            placeholder = { Text("4xxx xxxx xxxx xxxx", color = Color(0xFF9FB3C8)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = if (cardScheme != CardScheme.UNKNOWN) cardScheme.badgeColor else Color(0xFF829AB1)
                                )
                            },
                            trailingIcon = {
                                if (cardScheme != CardScheme.UNKNOWN) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = cardScheme.badgeColor.copy(alpha = 0.15f),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = cardScheme.displayName,
                                            color = cardScheme.badgeColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paymob_card_number_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaymobCyan,
                                unfocusedBorderColor = PaymobBorderColor,
                                focusedContainerColor = Color(0xFFFAFCFF),
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cardholder Name
                        Text(
                            text = "اسم صاحب البطاقة (Cardholder Name)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334E68)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = cardHolderName,
                            onValueChange = { cardHolderName = it },
                            placeholder = { Text("Ahmed Mostafa", color = Color(0xFF9FB3C8)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF829AB1)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paymob_cardholder_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaymobCyan,
                                unfocusedBorderColor = PaymobBorderColor,
                                focusedContainerColor = Color(0xFFFAFCFF),
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Expiry & CVV in One Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Expiry Date
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تاريخ الانتهاء (MM/YY)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334E68)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { expiryDate = formatExpiryDate(it) },
                                    placeholder = { Text("12/28", color = Color(0xFF9FB3C8)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Color(0xFF829AB1)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("paymob_card_expiry_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PaymobCyan,
                                        unfocusedBorderColor = PaymobBorderColor,
                                        focusedContainerColor = Color(0xFFFAFCFF),
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }

                            // CVV
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "رمز الأمان (CVV)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334E68)
                                    )
                                    Text(
                                        text = "3 أرقام بالخلف",
                                        fontSize = 10.sp,
                                        color = Color(0xFF829AB1)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) cvv = it },
                                    placeholder = { Text("•••", color = Color(0xFF9FB3C8)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF829AB1)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { showCvv = !showCvv }) {
                                            Icon(
                                                imageVector = if (showCvv) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFF829AB1),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    visualTransformation = if (showCvv) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { if (isFormValid) startPaymobCheckout() }
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("paymob_card_cvv_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PaymobCyan,
                                        unfocusedBorderColor = PaymobBorderColor,
                                        focusedContainerColor = Color(0xFFFAFCFF),
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sandbox / Demo Card Quick Presets (For fast evaluation)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F4F8),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚡ تعبئة سريعة لبطاقات تجريبية (Paymob Demo Cards):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaymobNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            cardNumber = "4111 2222 3333 4444"
                                            cardHolderName = "Ahmed Mostafa"
                                            expiryDate = "12/28"
                                            cvv = "123"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("فيزا", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            cardNumber = "5555 4444 3333 2222"
                                            cardHolderName = "Ahmed Mostafa"
                                            expiryDate = "08/29"
                                            cvv = "567"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("ماستركارد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            cardNumber = "5078 0300 1234 5678"
                                            cardHolderName = "Ahmed Mostafa"
                                            expiryDate = "05/30"
                                            cvv = "999"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("ميزة 🇪🇬", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security & Non-Storage Compliance Box (Explicit Requirement)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFE3F8FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB3ECFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF007A99),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "حماية مشددة وفق معايير PCI-DSS 🛡️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF004D61)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تتم عملية الدفع مباشرة على سيرفرات Paymob المؤمنة والمشفرة بتصريح من البنك المركزي المصري. لا يقوم تطبيق مينيو باستلام أو تخزين أية أرقام كروت أو أرقام سرية.",
                                fontSize = 11.sp,
                                color = Color(0xFF004D61),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Confirm Button Frame
            Surface(
                color = PaymobNavy,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { startPaymobCheckout() },
                        enabled = isFormValid && !isProcessing,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaymobGreen,
                            disabledContainerColor = Color(0xFF486581)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("paymob_submit_payment_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PaymobNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "دفع ${amount.toInt()} ج.م بأمان عبر Paymob 💳",
                                color = PaymobNavy,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "إلغاء والعودة لاختيار الدفع عند الاستلام",
                            color = Color(0xFFBAC7D5),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Fullscreen Loading Overlay during Handshake / Bank Communication
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PaymobNavy),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PaymobCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Paymob Hosted Gateway 🔒",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = processingStageText,
                            color = Color(0xFFBAC7D5),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // 3D Secure Bank Verification Modal (Egyptian Bank 3DS2)
        if (show3DSecureModal) {
            Dialog(
                onDismissRequest = { /* Prevent accidental dismissal */ },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3DS Bank Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "3D Secure 2.0 🛡️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PaymobNavy
                                )
                                Text(
                                    text = "التحقق الأمني من البنك المصدر",
                                    fontSize = 11.sp,
                                    color = Color(0xFF627D98)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF0F4F8)
                            ) {
                                Text(
                                    text = cardScheme.displayName,
                                    color = cardScheme.badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = PaymobBorderColor)

                        // Description
                        Text(
                            text = "تم إرسال رمز التحقق السري (OTP) في رسالة نصية SMS إلى هاتفك المحمول المسجل لدى البنك والمنتهي بـ ****4321 لتأكيد خصم مبلغ ${amount.toInt()} ج.م لصالح MINYOO EGYPT.",
                            fontSize = 12.sp,
                            color = Color(0xFF334E68),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // OTP Input Field
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otpInput = it },
                            placeholder = { Text("أدخل 6 أرقام (مثال: 123456)", color = Color(0xFF9FB3C8)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paymob_3ds_otp_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaymobCyan,
                                unfocusedBorderColor = PaymobBorderColor
                            ),
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                textAlign = TextAlign.Center,
                                letterSpacing = 4.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Auto-fill button for testing
                        TextButton(
                            onClick = { otpInput = "123456" }
                        ) {
                            Text("✨ تعبئة كود تجريبي تلقائي (123456)", fontSize = 11.sp, color = PaymobCyan)
                        }

                        // Countdown
                        Text(
                            text = "صلاحية الرمز: $otpCountdown ثانية",
                            fontSize = 11.sp,
                            color = Color(0xFF829AB1)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Confirm & Cancel Buttons
                        Button(
                            onClick = { complete3DSecureVerification() },
                            enabled = otpInput.length >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = PaymobNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("paymob_confirm_otp_btn")
                        ) {
                            Text("تأكيد وخصم المبلغ 🔐", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                show3DSecureModal = false
                                errorMessage = "تم إلغاء عملية التحقق الأمني من طرف العميل."
                                onPaymentFailed("تم إلغاء عملية الدفع 3DS")
                            }
                        ) {
                            Text("إلغاء المعاملة", color = Color(0xFFEF4444), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
