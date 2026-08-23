package com.example.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.security.ProductionAuth
import kotlinx.coroutines.launch

/**
 * Production authentication UI.
 *
 * This screen intentionally supports only CUSTOMER public registration.
 * Restaurant/courier activation is an admin/backend workflow and is not
 * exposed as a client-controlled role selector.
 */
@Composable
fun ProductionAuthScreen(
    auth: ProductionAuth = remember { ProductionAuth() },
    onAuthenticated: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var registerMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (registerMode) "إنشاء حساب عميل" else "تسجيل الدخول",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(20.dp))

        if (registerMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("الاسم") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; error = null },
                label = { Text("رقم الهاتف") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            label = { Text("البريد الإلكتروني") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("كلمة المرور (8 أحرف على الأقل)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val result = if (registerMode) {
                        auth.registerCustomer(email, password, name, phone)
                    } else {
                        auth.signIn(email, password)
                    }
                    loading = false
                    result.onSuccess { uid ->
                        onAuthenticated(uid)
                    }.onFailure {
                        error = it.message ?: "حدث خطأ في المصادقة"
                    }
                }
            }
        ) {
            if (loading) CircularProgressIndicator() else Text(if (registerMode) "إنشاء الحساب" else "دخول")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
            onClick = { registerMode = !registerMode; error = null }
        ) {
            Text(if (registerMode) "لدي حساب بالفعل" else "إنشاء حساب جديد")
        }
    }
}
