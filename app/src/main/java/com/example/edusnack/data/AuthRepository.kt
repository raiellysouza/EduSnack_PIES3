package com.example.edusnack.data

import com.example.edusnack.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(nome: String, email: String, pass: String, tipo: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: ""

            val user = User(
                id = uid,
                nome = nome,
                email = email,
                tipo = tipo
            )

            db.collection("usuarios").document(uid).set(user).await()

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val snap = db.collection("usuarios").document(uid).get().await()
        return snap.toObject(User::class.java)
    }

    fun logout() = auth.signOut()
}
