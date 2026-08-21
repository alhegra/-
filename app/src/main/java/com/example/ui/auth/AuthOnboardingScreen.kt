package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoginResult
import com.example.data.model.RestaurantRegistrationData
import com.example.data.model.RestaurantStatus
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class MainAuthTab {
    LOGIN,
    REGISTER
}

enum class RegisterRoleChoice {
    CUSTOMER,
    RESTAURANT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOnboardingScreen(
    onLogin: suspend (identifier: String, password: String) -> LoginResult,
    onCustomerRegistered: (name: String, phone: String, password: String, city: String) -> LoginResult,
    onRestaurantRegistered: (RestaurantRegistrationData, password: String) -> LoginResult
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(MainAuthTab.LOGIN) }
    var registerChoice by remember { mutableStateOf(RegisterRoleChoice.CUSTOMER) }

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Customer Register Form State
    var custName by remember { mutableStateOf("") }
    var custPhone by remember { mutableStateOf("") }
    var custPass by remember { mutableStateOf("") }
    var isCustPassVisible by remember { mutableStateOf(false) }
    var custCity by remember { mutableStateOf("القاهرة - المعادي") }
    var custError by remember { mutableStateOf<String?>(null) }

    // Restaurant Register Form State
    var restName by remember { mutableStateOf("") }
    var restPhone by remember { mutableStateOf("") }
    var restPass by remember { mutableStateOf("") }
    var isRestPassVisible by remember { mutableStateOf(false) }
    var restCity by remember { mutableStateOf("القاهرة - التجمع الخامس") }
    var restCuisine by remember { mutableStateOf("مأكولات شرقية ومشويات 🍖") }
    var restLogoIcon by remember { mutableStateOf("🍔") }
    var restMinOrder by remember { mutableStateOf("50") }
    var restPrepTime by remember { mutableStateOf("30") }
    var restError by remember { mutableStateOf<String?>(null) }

    val popularCities = listOf(
        "القاهرة - المعادي",
        "القاهرة - مدينة نصر",
        "القاهرة - التجمع الخامس",
        "الجيزة - الدقي والمهندسين",
        "الجيزة - الشيخ زايد و6 أكتوبر",
        "الإسكندرية - سموحة ومحرم بك",
        "المنصورة - حي الجامعة",
        "طنطا - شارع النحاس",
        "الزقازيق - القومية"
    )

    val cuisinesList = listOf(
        "برجر وساندوتشات 🍔",
        "مأكولات شرقية ومشويات 🍖",
        "بيتزا وفطائر 🍕",
        "شاورما ودجاج مقرمش 🍗",
        "مأكولات بحرية وسوشي 🍣",
        "حلويات ومخبوزات 🍰",
        "كشري ومأكولات مصرية 🍲",
        "عصائر ومشروبات 🥤"
    )

    val logoIcons = listOf("🍔", "🍕", "🍗", "🍖", "🌯", "🍲", "🍣", "🌮", "🍰", "☕", "🥗", "🥪")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF7ED),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Brand Header
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = CircleShape,
                color = MinyooOrangePrimary,
                shadowElevation = 6.dp,
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🍽️", fontSize = 34.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "لقمة",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MinyooOrangePrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "lo2ma.click",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MinyooSlateLight
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "المنصة الموحدة لطلب الطعام وإدارة المطاعم في مصر 🇪🇬",
                style = MaterialTheme.typography.bodySmall,
                color = MinyooSlateMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Tab Selector: [تسجيل الدخول] vs [إنشاء حساب جديد]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Login Tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == MainAuthTab.LOGIN) MinyooOrangePrimary else Color.Transparent,
                        shadowElevation = if (selectedTab == MainAuthTab.LOGIN) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = MainAuthTab.LOGIN
                                loginError = null
                            }
                            .testTag("tab_login")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (selectedTab == MainAuthTab.LOGIN) Color.White else MinyooSlateLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تسجيل الدخول",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedTab == MainAuthTab.LOGIN) Color.White else MinyooSlateLight
                                )
                            }
                        }
                    }

                    // Register Tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == MainAuthTab.REGISTER) MinyooOrangePrimary else Color.Transparent,
                        shadowElevation = if (selectedTab == MainAuthTab.REGISTER) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTab = MainAuthTab.REGISTER
                                custError = null
                                restError = null
                            }
                            .testTag("tab_register")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = if (selectedTab == MainAuthTab.REGISTER) Color.White else MinyooSlateLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "حساب جديد",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedTab == MainAuthTab.REGISTER) Color.White else MinyooSlateLight
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tab Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "auth_tabs_anim"
            ) { tab ->
                when (tab) {
                    MainAuthTab.LOGIN -> {
                        // ==========================================
                        // UNIFIED LOGIN FORM (Customer, Owner, Admin)
                        // ==========================================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MinyooCardBorder, RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "تسجيل الدخول الموحد 🔐",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MinyooCharcoal
                                    )
                                    Text(
                                        text = "أدخل رقم الموبايل أو البريد الإلكتروني وكلمة المرور للدخول لحسابك (عميل، صاحب مطعم، أو لوحة التحكم)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                    )

                                    if (loginError != null) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFFEE2E2),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 14.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Color(0xFFDC2626),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = loginError ?: "",
                                                    color = Color(0xFFB91C1C),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    // Identifier (Phone or Email)
                                    Text(
                                        text = "رقم الموبايل أو البريد الإلكتروني",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MinyooCharcoal,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = loginIdentifier,
                                        onValueChange = {
                                            loginIdentifier = it
                                            loginError = null
                                        },
                                        placeholder = { Text("مثال: 01098765432 أو admin@lo2ma.click") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = null,
                                                tint = MinyooOrangePrimary
                                            )
                                        },
                                        trailingIcon = {
                                            if (loginIdentifier.isNotEmpty()) {
                                                IconButton(onClick = { loginIdentifier = "" }) {
                                                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MinyooOrangePrimary,
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("login_identifier_input")
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Password
                                    Text(
                                        text = "كلمة المرور",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MinyooCharcoal,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = loginPassword,
                                        onValueChange = {
                                            loginPassword = it
                                            loginError = null
                                        },
                                        placeholder = { Text("أدخل كلمة المرور") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = MinyooOrangePrimary
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (isPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                                    tint = MinyooSlateLight
                                                )
                                            }
                                        },
                                        singleLine = true,
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (!loginLoading) {
                                                    coroutineScope.launch {
                                                        loginLoading = true
                                                        val result = onLogin(loginIdentifier, loginPassword)
                                                        loginLoading = false
                                                        if (result is LoginResult.Error) {
                                                            loginError = result.message
                                                        }
                                                    }
                                                }
                                            }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MinyooOrangePrimary,
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("login_password_input")
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Login Action Button
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                loginLoading = true
                                                val result = onLogin(loginIdentifier, loginPassword)
                                                loginLoading = false
                                                if (result is LoginResult.Error) {
                                                    loginError = result.message
                                                }
                                            }
                                        },
                                        enabled = !loginLoading && loginIdentifier.isNotBlank() && loginPassword.isNotBlank(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MinyooOrangePrimary,
                                            disabledContainerColor = MinyooOrangePrimary.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("login_submit_button")
                                    ) {
                                        if (loginLoading) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.5.dp
                                            )
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Login,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "تسجيل الدخول",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Quick Demo & Testing Login Cards (Helpful presets for testing)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "⚡", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "حسابات جاهزة للاختبار والتجربة السريعة:",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooCharcoal
                                        )
                                    }
                                    Text(
                                        text = "اضغط على أي حساب لملء بياناته وتسجيل الدخول مباشرة:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateLight,
                                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                    )

                                    // Preset 1: Admin
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEF2F2),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                loginIdentifier = "admin@lo2ma.click"
                                                loginPassword = "Admin@Lo2ma#Secure992"
                                                loginError = null
                                            }
                                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                            .testTag("quick_fill_admin")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "🛡️", fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "حساب الأدمن المركزي (Admin)",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = Color(0xFF991B1B)
                                                    )
                                                    Text(
                                                        text = "admin@lo2ma.click",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFFB91C1C)
                                                    )
                                                }
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFDC2626)
                                            ) {
                                                Text(
                                                    text = "لوحة التحكم ⚙️",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Preset 2: Customer
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFFF7ED),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                loginIdentifier = "01098765432"
                                                loginPassword = "123456"
                                                loginError = null
                                            }
                                            .border(1.dp, Color(0xFFFDBA74), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                            .testTag("quick_fill_customer")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "🛍️", fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "حساب عميل (أحمد مصطفى)",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MinyooOrangeDark
                                                    )
                                                    Text(
                                                        text = "01098765432 • المعادي",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MinyooSlateLight
                                                    )
                                                }
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MinyooOrangePrimary
                                            ) {
                                                Text(
                                                    text = "واجهة العميل 🛒",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Preset 3: Restaurant Owner
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF0FDF4),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                loginIdentifier = "01012345678"
                                                loginPassword = "123456"
                                                loginError = null
                                            }
                                            .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                            .testTag("quick_fill_restaurant")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "👨‍🍳", fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "حساب مطعم (كشري أبو طارق)",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = Color(0xFF166534)
                                                    )
                                                    Text(
                                                        text = "01012345678 • وسط البلد",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF15803D)
                                                    )
                                                }
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF16A34A)
                                            ) {
                                                Text(
                                                    text = "شاشة المطعم 🍽️",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MainAuthTab.REGISTER -> {
                        // ==========================================
                        // REGISTRATION FORM (Customer or Restaurant only)
                        // ==========================================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Sub-choice: Customer vs Restaurant
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (registerChoice == RegisterRoleChoice.CUSTOMER) MinyooOrangeContainer else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (registerChoice == RegisterRoleChoice.CUSTOMER) MinyooOrangePrimary else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { registerChoice = RegisterRoleChoice.CUSTOMER }
                                        .testTag("reg_choice_customer")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "🛍️", fontSize = 26.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "تسجيل كعميل",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (registerChoice == RegisterRoleChoice.CUSTOMER) MinyooOrangeDark else MinyooCharcoal
                                        )
                                        Text(
                                            text = "طلب وتوصيل طعام",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MinyooSlateLight
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (registerChoice == RegisterRoleChoice.RESTAURANT) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (registerChoice == RegisterRoleChoice.RESTAURANT) Color(0xFFD97706) else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { registerChoice = RegisterRoleChoice.RESTAURANT }
                                        .testTag("reg_choice_restaurant")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "👨‍🍳", fontSize = 26.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "انضمام مطعم",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (registerChoice == RegisterRoleChoice.RESTAURANT) Color(0xFF92400E) else MinyooCharcoal
                                        )
                                        Text(
                                            text = "استقبال وإدارة طلبات",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MinyooSlateLight
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (registerChoice == RegisterRoleChoice.CUSTOMER) {
                                // ------------------------------------
                                // CUSTOMER REGISTRATION FORM
                                // ------------------------------------
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(20.dp))
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            text = "بيانات حساب العميل 🛍️",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooCharcoal
                                        )
                                        Text(
                                            text = "سجل حسابك لتصفح مئات المطاعم والعروض الحصرية",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MinyooSlateLight,
                                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                                        )

                                        if (custError != null) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFFEE2E2),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 12.dp)
                                            ) {
                                                Text(
                                                    text = custError ?: "",
                                                    color = Color(0xFFDC2626),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }

                                        // Name
                                        Text("الاسم بالكامل", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = custName,
                                            onValueChange = { custName = it; custError = null },
                                            placeholder = { Text("مثال: أحمد مصطفى") },
                                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MinyooOrangePrimary) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_name")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Phone
                                        Text("رقم الموبايل (المصري)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = custPhone,
                                            onValueChange = { custPhone = it; custError = null },
                                            placeholder = { Text("010xxxxxxxx أو 011xxxxxxxx") },
                                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MinyooOrangePrimary) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_phone")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Password
                                        Text("كلمة المرور الجديدة", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = custPass,
                                            onValueChange = { custPass = it; custError = null },
                                            placeholder = { Text("أدخل كلمة مرور الحساب") },
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MinyooOrangePrimary) },
                                            trailingIcon = {
                                                IconButton(onClick = { isCustPassVisible = !isCustPassVisible }) {
                                                    Icon(
                                                        imageVector = if (isCustPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = null
                                                    )
                                                }
                                            },
                                            visualTransformation = if (isCustPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_pass")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // City / Area
                                        Text("المدينة أو المنطقة السكنية", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = custCity,
                                            onValueChange = { custCity = it },
                                            placeholder = { Text("مثال: القاهرة - المعادي") },
                                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MinyooOrangePrimary) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("cust_reg_city")
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Quick City Chips
                                        Text("اختيار سريع للمنطقة:", style = MaterialTheme.typography.labelSmall, color = MinyooSlateLight)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            items(popularCities) { city ->
                                                val isSelected = custCity == city
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = if (isSelected) MinyooOrangePrimary else Color(0xFFF1F5F9),
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .clickable { custCity = city }
                                                ) {
                                                    Text(
                                                        text = city,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isSelected) Color.White else MinyooCharcoal,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Button(
                                            onClick = {
                                                if (custName.isBlank()) {
                                                    custError = "يرجى كتابة الاسم بالكامل"
                                                    return@Button
                                                }
                                                if (custPhone.isBlank() || custPhone.length < 9) {
                                                    custError = "يرجى إدخال رقم هاتف صالح"
                                                    return@Button
                                                }
                                                if (custPass.isBlank()) {
                                                    custError = "يرجى إدخال كلمة مرور"
                                                    return@Button
                                                }
                                                onCustomerRegistered(custName, custPhone, custPass, custCity)
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .testTag("cust_reg_submit")
                                        ) {
                                            Text("إنشاء حساب العميل والبدء 🛒", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                        }
                                    }
                                }
                            } else {
                                // ------------------------------------
                                // RESTAURANT REGISTRATION FORM
                                // ------------------------------------
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MinyooCardBorder, RoundedCornerShape(20.dp))
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            text = "انضمام كشريك مطعم 👨‍🍳",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MinyooCharcoal
                                        )
                                        Text(
                                            text = "سجل مطعمك في منصة لقمة وابدأ في استقبال وتجهيز طلبات الزبائن فور اعتماد الإدارة",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MinyooSlateLight,
                                            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                                        )

                                        if (restError != null) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFFEE2E2),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 12.dp)
                                            ) {
                                                Text(
                                                    text = restError ?: "",
                                                    color = Color(0xFFDC2626),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }

                                        // Restaurant Name
                                        Text("اسم المطعم التجاري", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = restName,
                                            onValueChange = { restName = it; restError = null },
                                            placeholder = { Text("مثال: شاورما الريم أو بيتزا كينج") },
                                            leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = MinyooOrangePrimary) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("rest_reg_name")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Phone
                                        Text("رقم هاتف الإدارة والطلبات", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = restPhone,
                                            onValueChange = { restPhone = it; restError = null },
                                            placeholder = { Text("010xxxxxxxx أو 012xxxxxxxx") },
                                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MinyooOrangePrimary) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("rest_reg_phone")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Password
                                        Text("كلمة المرور للوحة التحكم", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = restPass,
                                            onValueChange = { restPass = it; restError = null },
                                            placeholder = { Text("أدخل كلمة مرور قوية") },
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MinyooOrangePrimary) },
                                            trailingIcon = {
                                                IconButton(onClick = { isRestPassVisible = !isRestPassVisible }) {
                                                    Icon(
                                                        imageVector = if (isRestPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = null
                                                    )
                                                }
                                            },
                                            visualTransformation = if (isRestPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("rest_reg_pass")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // City / Area
                                        Text("الفرع والمنطقة", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = restCity,
                                            onValueChange = { restCity = it },
                                            placeholder = { Text("مثال: القاهرة - التجمع الخامس") },
                                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MinyooOrangePrimary) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("rest_reg_city")
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Cuisine Choice
                                        Text("نوع وتصنيف المطبخ الرئيسي", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            items(cuisinesList) { cui ->
                                                val isSelected = restCuisine == cui
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = if (isSelected) Color(0xFFD97706) else Color(0xFFF1F5F9),
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .clickable { restCuisine = cui }
                                                ) {
                                                    Text(
                                                        text = cui,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isSelected) Color.White else MinyooCharcoal,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Logo Icon Selection
                                        Text("أيقونة وشعار المطعم", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(logoIcons) { iconEmoji ->
                                                val isSelected = restLogoIcon == iconEmoji
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (isSelected) MinyooOrangePrimary else Color(0xFFF1F5F9),
                                                    border = BorderStroke(
                                                        1.5.dp,
                                                        if (isSelected) MinyooOrangePrimary else Color(0xFFE2E8F0)
                                                    ),
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(CircleShape)
                                                        .clickable { restLogoIcon = iconEmoji }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(text = iconEmoji, fontSize = 20.sp)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Button(
                                            onClick = {
                                                if (restName.isBlank()) {
                                                    restError = "يرجى كتابة اسم المطعم"
                                                    return@Button
                                                }
                                                if (restPhone.isBlank() || restPhone.length < 9) {
                                                    restError = "يرجى إدخال رقم هاتف صالح للإدارة"
                                                    return@Button
                                                }
                                                if (restPass.isBlank()) {
                                                    restError = "يرجى إدخال كلمة مرور"
                                                    return@Button
                                                }
                                                val data = RestaurantRegistrationData(
                                                    restaurantName = restName.trim(),
                                                    phone = restPhone.trim(),
                                                    cityArea = restCity.trim(),
                                                    cuisine = restCuisine,
                                                    logoIcon = restLogoIcon,
                                                    minOrder = restMinOrder.toDoubleOrNull() ?: 50.0,
                                                    deliveryTimeMinutes = restPrepTime.toIntOrNull() ?: 30,
                                                    status = RestaurantStatus.PENDING
                                                )
                                                onRestaurantRegistered(data, restPass)
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .testTag("rest_reg_submit")
                                        ) {
                                            Text("إرسال طلب انضمام المطعم 📋", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Security Notice: Admin accounts are managed directly from DB
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = MinyooSlateLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "حسابات الإدارة المركزية (Admin) يتم إنشاؤها وتعيين صلاحياتها يدويًا من قاعدة البيانات مباشرة لضمان أعلى مستويات الأمان.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MinyooSlateMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
