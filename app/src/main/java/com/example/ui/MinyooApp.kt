package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.security.ProductionSessionManager
import com.example.ui.auth.ProductionAuthScreen

/**
 * Production authentication boundary. The existing application UI remains
 * behind this gate until its session is backed by Firebase identity.
 */
@Composable
fun ProductionAppEntry(
    sessionManager: ProductionSessionManager = remember { ProductionSessionManager() },
    content: @Composable () -> Unit,
) {
    var checking by remember { mutableStateOf(true) }
    var authenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authenticated = try {
            val user = sessionManager.currentUser
            user != null && sessionManager.resolveRole() != null
        } catch (_: Exception) {
            false
        }
        checking = false
    }

    when {
        checking -> Unit
        authenticated -> content()
        else -> ProductionAuthScreen(
            onAuthenticated = { uid ->
                // ProductionAuth has already verified the server-side profile
                // before invoking this callback. Keep the UID only as the
                // identity transition signal; authorization remains server-side.
                authenticated = uid.isNotBlank()
            }
        )
    }
}
