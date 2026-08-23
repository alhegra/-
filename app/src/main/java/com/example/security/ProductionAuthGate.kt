package com.example.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Production authentication gate.
 *
 * This gate intentionally has no role-switching behavior. A signed-in user's
 * role is resolved from the trusted Firestore profile through
 * ProductionSessionManager.
 */
@Composable
fun ProductionAuthGate(
    session: ProductionSessionManager = remember { ProductionSessionManager() },
    authenticatedContent: @Composable (role: String) -> Unit,
    unauthenticatedContent: @Composable () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var role by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(session.currentUser?.uid) {
        loading = true
        role = runCatching { session.resolveRole() }.getOrNull()
        loading = false
    }

    when {
        loading -> Unit
        session.currentUser == null || role.isNullOrBlank() -> unauthenticatedContent()
        else -> authenticatedContent(role!!)
    }
}
