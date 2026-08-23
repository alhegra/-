package com.example.security

import com.example.data.model.UserRole
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Production authentication boundary.
 *
 * Firebase Authentication is the identity authority. The client never chooses
 * an elevated role. Authorization role is read from users/{uid} and must be
 * explicitly present and valid. Missing/invalid profiles fail closed.
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

        val result = awaitTask(auth.createUserWithEmailAndPassword(email.trim(), password))
        val uid = requireNotNull(result.user?.uid)

        awaitTask(
            firestore.collection("users").document(uid).set(
                mapOf(
                    "uid" to uid,
                    "email" to email.trim(),
                    "name" to name.trim(),
                    "phone" to phone.trim(),
                    "role" to UserRole.CUSTOMER.name,
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            )
        )

        uid
    }

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        require(email.isNotBlank()) { "Email is required" }
        require(password.isNotBlank()) { "Password is required" }
        val result = awaitTask(auth.signInWithEmailAndPassword(email.trim(), password))
        val uid = requireNotNull(result.user?.uid)

        // Resolve authorization immediately and fail closed if the account has
        // no valid server-side profile. This prevents an authenticated identity
        // from becoming an implicitly authorized application user.
        getServerRole(uid).getOrThrow()
        uid
    }

    suspend fun getServerRole(uid: String = requireNotNull(currentUid)): Result<UserRole> = runCatching {
        val snapshot = awaitTask(firestore.collection("users").document(uid).get())
        require(snapshot.exists()) { "User profile is not provisioned" }
        val roleName = snapshot.getString("role")
        require(!roleName.isNullOrBlank()) { "User role is not provisioned" }
        UserRole.entries.firstOrNull { it.name == roleName }
            ?: error("Invalid server role")
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun <T> awaitTask(task: Task<T>): T = suspendCancellableCoroutine { continuation ->
        task.addOnSuccessListener { value ->
            if (continuation.isActive) continuation.resume(value)
        }.addOnFailureListener { error ->
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    }
}
