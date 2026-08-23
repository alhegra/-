package com.example.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Production identity/session boundary.
 *
 * Firebase Authentication is the identity authority. The role is read from
 * users/{uid}; it is never accepted from local preferences or user input.
 */
class ProductionSessionManager(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    val currentUser get() = auth.currentUser

    suspend fun resolveRole(): String? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.getString("role")
    }

    suspend fun requireRole(expected: String): Boolean = resolveRole() == expected

    fun signOut() {
        auth.signOut()
    }
}
