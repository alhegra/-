package com.example.security

import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Production authentication boundary.
 *
 * Important: the client never chooses an elevated role. Role is read from the
 * server-side users/{uid} document. Admin/restaurant/courier elevation must be
 * performed by trusted backend code or an administrator.
 */
class ProductionAuth(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUid: String?
        get() = auth.currentUser?.uid

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    suspend fun registerCustomer(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<String> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.length >= 8) { "Password must contain at least 8 characters" }
        require(name.isNotBlank()) { "Name is required" }
        require(phone.isNotBlank()) { "Phone is required" }

        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid = requireNotNull(result.user?.uid)

        // New accounts are ALWAYS customers. Elevated roles are server-controlled.
        firestore.collection("users").document(uid).set(
            mapOf(
                "uid" to uid,
                "email" to email.trim(),
                "name" to name.trim(),
                "phone" to phone.trim(),
                "role" to UserRole.CUSTOMER.name,
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        ).await()

        uid
    }

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.isNotBlank()) { "Password is required" }
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        requireNotNull(result.user?.uid)
    }

    suspend fun getServerRole(uid: String = requireNotNull(currentUid)): Result<UserRole> = runCatching {
        val snapshot = firestore.collection("users").document(uid).get().await()
        val roleName = snapshot.getString("role") ?: UserRole.CUSTOMER.name
        UserRole.entries.firstOrNull { it.name == roleName } ?: UserRole.CUSTOMER
    }

    fun signOut() {
        auth.signOut()
    }
}
