package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LoginResult
import com.example.data.model.RestaurantRegistrationData
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class RegisterRoleType {
    CUSTOMER,
    RESTAURANT,
    COURIER
}

enum class AuthScreenMode {
    LOGIN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOnboardingScreen(
    isEnglish: Boolean = false,
    onToggleLanguage: () -> Unit = {},
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    onLogin: suspend (identifier: String, password: String) -> LoginResult,
    onCustomerRegistered: (name: String, phone: String, password: String, city: String) -> LoginResult,
    onRestaurantRegistered: (RestaurantRegistrationData, password: String) -> LoginResult,
    onCourierRegistered: (name: String, phone: String, password: String, city: String) -> LoginResult
) {
    val coroutineScope = rememberCoroutineScope()
    
    var authMode by remember { mutableStateOf(AuthScreenMode.LOGIN) }
    var registerRole by remember { mutableStateOf(RegisterRoleType.CUSTOMER) }

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Customer / Courier Register Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var isRegPassVisible by remember { mutableStateOf(false) }
    var regCity by remember { mutableStateOf("القاهرة - المعادي") }
    var regError by remember { mutableStateOf<String?>(null) }
    var regLoading by remember { mutableStateOf(false) }

    // Restaurant Register Form State
    var restName by remember { mutableStateOf("") }
    var restPhone by remember { mutableStateOf("") }
    var restPass by remember { mutableStateOf("") }
    var isRestPassVisible by remember { mutableStateOf(false) }
    var restCity by remember { mutableStateOf("القاهرة - التجمع الخامس") }
    var restCuisine by remember { mutableStateOf("مأكولات شرقية ومشويات") }
    var restLogoIcon by remember { mutableStateOf("🍔") }
    var restMinOrder by remember { mutableStateOf("50") }
    var restPrepTime by remember { mutableStateOf("30") }
    var restError by remember { mutableStateOf<String?>(null) }
    var restLoading by remember { mutableStateOf(false) }

    val popularCities = listOf(
        "القاهرة - المعادي",
        "القاهرة - مدينة نصر",
        "القاهرة - التجمع الخامس",
        "الجيزة - الدقي والمهندسين",
        "الجيزة - الشيخ زايد و6 أكتوبر",
        "الإسكندرية - سموحة ومحرم بك",
        "المنصورة - حي الجامعة"
    )

    val cuisinesList = listOf(
        "برجر وساندوتشات",
        "مأكولات شرقية ومشويات",
        "بيتزا وفطائر",
        "شاورما ودجاج مقرمش",
        "مأكولات بحرية وسوشي",
        "كشري ومأكولات مصرية"
    )

    val logoIcons = listOf("🍔", "🍕", "🍗", "🍖", "🌯", "🍲", "🍣", "🌮")

    // Solid clean surface with high contrast text colors
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Theme & Language Switcher at Top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val nextMode = when (themeMode) {
                            ThemeMode.SYSTEM -> ThemeMode.LIGHT
                            ThemeMode.LIGHT -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.SYSTEM
                        }
                        onThemeModeChanged(nextMode)
                    },
                    modifier = Modifier.testTag("auth_theme_toggle")
                ) {
                    Icon(
                        imageVector = when (themeMode) {
                            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                        },
                        contentDescription = "تغيير المظهر",
                        tint = MinyooOrangePrimary
                    )
                }

                TextButton(
                    onClick = onToggleLanguage,
                    colors = ButtonDefaults.textButtonColors(contentColor = MinyooOrangePrimary),
                    modifier = Modifier.testTag("auth_lang_toggle")
                ) {
                    Text(text = if (isEnglish) "🇪🇬 العربية" else "🇬🇧 English", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Brand Header Logo
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MinyooOrangePrimary,
                modifier = Modifier.size(72.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lo2ma_logo),
                    contentDescription = "شعار لقمة",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isEnglish) "lo2ma" else "لقمة",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (authMode == AuthScreenMode.LOGIN) "تسجيل الدخول إلى حسابك" else "إنشاء حساب جديد",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Mode Switcher (Login vs Register)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { 
                            authMode = AuthScreenMode.LOGIN
                            loginError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (authMode == AuthScreenMode.LOGIN) Color.White else Color.Transparent,
                            contentColor = if (authMode == AuthScreenMode.LOGIN) Color(0xFF111827) else Color(0xFF4B5563)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = if (authMode == AuthScreenMode.LOGIN) ButtonDefaults.buttonElevation(2.dp) else ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier.weight(1f).fillMaxHeight().testTag("tab_login")
                    ) {
                        Text(text = "تسجيل الدخول", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = { 
                            authMode = AuthScreenMode.REGISTER
                            regError = null
                            restError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (authMode == AuthScreenMode.REGISTER) Color.White else Color.Transparent,
                            contentColor = if (authMode == AuthScreenMode.REGISTER) Color(0xFF111827) else Color(0xFF4B5563)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = if (authMode == AuthScreenMode.REGISTER) ButtonDefaults.buttonElevation(2.dp) else ButtonDefaults.buttonElevation(0.dp),
                        modifier = Modifier.weight(1f).fillMaxHeight().testTag("tab_register")
                    ) {
                        Text(text = "حساب جديد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error banner if any
            val currentError = when {
                authMode == AuthScreenMode.LOGIN -> loginError
                registerRole == RegisterRoleType.RESTAURANT -> restError
                else -> regError
            }

            if (currentError != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF2F2),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // LOGIN MODE (Unified, no pre-choice needed)
            // ==========================================
            if (authMode == AuthScreenMode.LOGIN) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = {
                            loginIdentifier = it
                            loginError = null
                        },
                        placeholder = { Text("رقم الهاتف أو البريد الإلكتروني", color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinyooOrangePrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("login_identifier_input")
                    )

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = {
                            loginPassword = it
                            loginError = null
                        },
                        placeholder = { Text("كلمة المرور", color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (loginIdentifier.isNotBlank() && loginPassword.isNotBlank()) {
                                coroutineScope.launch {
                                    loginLoading = true
                                    val res = onLogin(loginIdentifier, loginPassword)
                                    loginLoading = false
                                    if (res !is LoginResult.Success) {
                                        loginError = when (res) {
                                            is LoginResult.Error -> res.message
                                            else -> "فشل تسجيل الدخول"
                                        }
                                    }
                                }
                            }
                        }),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinyooOrangePrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loginLoading = true
                                val res = onLogin(loginIdentifier, loginPassword)
                                loginLoading = false
                                if (res !is LoginResult.Success) {
                                    loginError = when (res) {
                                        is LoginResult.Error -> res.message
                                        else -> "فشل تسجيل الدخول"
                                    }
                                }
                            }
                        },
                        enabled = !loginLoading && loginIdentifier.isNotBlank() && loginPassword.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MinyooOrangePrimary,
                            disabledContainerColor = Color(0xFFE5E7EB)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (loginLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "تسجيل الدخول",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // REGISTER MODE (Secondary role selector at bottom)
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Secondary Role Selector at bottom/top of registration
                    Text(
                        text = "اختر نوع الحساب الجديد:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Customer Option
                        val isCust = registerRole == RegisterRoleType.CUSTOMER
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCust) Color(0xFFFFF7ED) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isCust) MinyooOrangePrimary else Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { registerRole = RegisterRoleType.CUSTOMER }
                                .testTag("reg_choice_customer")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🛒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "عميل",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCust) MinyooOrangePrimary else Color(0xFF374151)
                                )
                            }
                        }

                        // Restaurant Option
                        val isRest = registerRole == RegisterRoleType.RESTAURANT
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isRest) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isRest) Color(0xFF16A34A) else Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { registerRole = RegisterRoleType.RESTAURANT }
                                .testTag("reg_choice_restaurant")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🏪", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مطعم",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRest) Color(0xFF16A34A) else Color(0xFF374151)
                                )
                            }
                        }

                        // Courier Option
                        val isCourier = registerRole == RegisterRoleType.COURIER
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCourier) Color(0xFFF3E8FF) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isCourier) Color(0xFF9333EA) else Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { registerRole = RegisterRoleType.COURIER }
                                .testTag("reg_choice_courier")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🛵", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "طيار",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCourier) Color(0xFF9333EA) else Color(0xFF374151)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Registration Form based on selected role
                    if (registerRole == RegisterRoleType.RESTAURANT) {
                        // Restaurant Register Form
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = restName,
                                onValueChange = { restName = it; restError = null },
                                placeholder = { Text("اسم المطعم أو البراند", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF16A34A),
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("rest_reg_name")
                            )

                            OutlinedTextField(
                                value = restPhone,
                                onValueChange = { restPhone = it; restError = null },
                                placeholder = { Text("رقم هاتف المطعم للتواصل", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF16A34A),
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("rest_reg_phone")
                            )

                            OutlinedTextField(
                                value = restPass,
                                onValueChange = { restPass = it; restError = null },
                                placeholder = { Text("كلمة المرور لإدارة المطعم", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                visualTransformation = if (isRestPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isRestPassVisible = !isRestPassVisible }) {
                                        Icon(
                                            imageVector = if (isRestPassVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = null,
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF16A34A),
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("rest_reg_pass")
                            )

                            Text(text = "رمز أو أيقونة المطعم", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(logoIcons) { icon ->
                                    val isSelected = restLogoIcon == icon
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                        border = if (isSelected) BorderStroke(2.dp, Color(0xFF16A34A)) else null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .clickable { restLogoIcon = icon }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = icon, fontSize = 20.sp)
                                        }
                                    }
                                }
                            }

                            Text(text = "نوع المأكولات والتصنيف", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(cuisinesList) { cuisine ->
                                    val isSelected = restCuisine == cuisine
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) Color(0xFF16A34A) else Color(0xFFF3F4F6),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable { restCuisine = cuisine }
                                    ) {
                                        Text(
                                            text = cuisine,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF374151),
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (restName.isBlank() || restPhone.isBlank() || restPass.isBlank()) {
                                        restError = "يرجى ملء جميع الحقول المطلوبة للمطعم"
                                        return@Button
                                    }
                                    restLoading = true
                                    val regData = RestaurantRegistrationData(
                                        restaurantName = restName,
                                        phone = restPhone,
                                        cityArea = restCity,
                                        cuisine = restCuisine,
                                        logoIcon = restLogoIcon,
                                        minOrder = restMinOrder.toDoubleOrNull() ?: 40.0,
                                        deliveryTimeMinutes = restPrepTime.toIntOrNull() ?: 30
                                    )
                                    val res = onRestaurantRegistered(regData, restPass)
                                    restLoading = false
                                    if (res !is LoginResult.Success) {
                                        restError = when (res) {
                                            is LoginResult.Error -> res.message
                                            else -> "فشل تسجيل المطعم"
                                        }
                                    }
                                },
                                enabled = !restLoading,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("rest_reg_submit")
                            ) {
                                if (restLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text(text = "تسجيل مطعم جديد للانضمام", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }
                    } else {
                        // Customer or Courier Register Form
                        val isCourier = registerRole == RegisterRoleType.COURIER
                        val brandColor = if (isCourier) Color(0xFF9333EA) else MinyooOrangePrimary

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it; regError = null },
                                placeholder = { Text("الاسم بالكامل", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandColor,
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("cust_reg_name")
                            )

                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = { regPhone = it; regError = null },
                                placeholder = { Text("رقم الهاتف (مثال: 01012345678)", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandColor,
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("cust_reg_phone")
                            )

                            OutlinedTextField(
                                value = regPass,
                                onValueChange = { regPass = it; regError = null },
                                placeholder = { Text("كلمة المرور (6 أحرف على الأقل)", color = Color(0xFF9CA3AF)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                                visualTransformation = if (isRegPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isRegPassVisible = !isRegPassVisible }) {
                                        Icon(
                                            imageVector = if (isRegPassVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = null,
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandColor,
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("cust_reg_pass")
                            )

                            Text(text = "المدينة والمنطقة", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(popularCities) { city ->
                                    val isSelected = regCity == city
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) brandColor else Color(0xFFF3F4F6),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable { regCity = city }
                                    ) {
                                        Text(
                                            text = city,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF374151),
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (regName.isBlank() || regPhone.isBlank() || regPass.isBlank()) {
                                        regError = "يرجى ملء جميع الحقول المطلوبة"
                                        return@Button
                                    }
                                    regLoading = true
                                    val res = if (isCourier) {
                                        onCourierRegistered(regName, regPhone, regPass, regCity)
                                    } else {
                                        onCustomerRegistered(regName, regPhone, regPass, regCity)
                                    }
                                    regLoading = false
                                    if (res !is LoginResult.Success) {
                                        regError = when (res) {
                                            is LoginResult.Error -> res.message
                                            else -> "فشل التسجيل"
                                        }
                                    }
                                },
                                enabled = !regLoading,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("cust_reg_submit")
                            ) {
                                if (regLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text(
                                        text = if (isCourier) "إنشاء حساب طيار توصيل جديد" else "إنشاء حساب عميل جديد",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
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
