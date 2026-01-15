package com.example.edusnack.data

import com.example.edusnack.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun isCanteenTipo(tipo: String?): Boolean {
        return tipo?.lowercase()?.let { it == "cantina" || it == "canteen" } == true
    }

    suspend fun getUserByEmail(email: String): User? {
        val query = db.collection("usuarios").whereEqualTo("email", email).limit(1).get().await()
        val doc = query.documents.firstOrNull() ?: return null
        return doc.toObject(User::class.java)
    }

    // Escuta os dependentes (User) de um responsável em tempo real na coleção 'usuarios'
    fun getDependentesByUser(responsavelId: String): Flow<List<User>> = callbackFlow {
        val listener = db.collection("usuarios")
            .whereEqualTo("responsavelId", responsavelId)
            .whereEqualTo("tipo", "aluno")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveProfile(uid: String, profileData: Map<String, Any>, collectionName: String = "profiles") {
        db.collection(collectionName).document(uid).set(profileData).await()
    }

    suspend fun login(email: String, pass: String, selectedTipo: String): Result<String> {
        try {
            val existing = getUserByEmail(email)
            if (existing != null) {
                val existingIsCantina = isCanteenTipo(existing.tipo)
                val selectedIsCantina = isCanteenTipo(selectedTipo)
                if (existingIsCantina != selectedIsCantina) {
                    return Result.failure(Exception("Este e-mail já está cadastrado como ${if (existingIsCantina) "CANTINA" else "ALUNO/RESPONSÁVEL"} e não pode ser usado para o tipo selecionado."))
                }
            }

            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: ""

            val userSnap = db.collection("usuarios").document(uid).get().await()
            val user = userSnap.toObject(User::class.java)
            if (user == null) return Result.failure(Exception("Usuário não encontrado no banco."))

            val storedIsCantina = isCanteenTipo(user.tipo)
            val selectedIsCantina = isCanteenTipo(selectedTipo)
            if (storedIsCantina != selectedIsCantina) {
                return Result.failure(Exception("Tipo de usuário inválido para este e-mail."))
            }

            return Result.success(uid)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun register(nome: String, email: String, pass: String, tipo: String, responsavelId: String? = null): Result<String> {
        return try {
            val existing = getUserByEmail(email)
            if (existing != null) {
                val existingIsCantina = isCanteenTipo(existing.tipo)
                val newIsCantina = isCanteenTipo(tipo)
                if (existingIsCantina != newIsCantina) {
                    return Result.failure(Exception("Este e-mail já está cadastrado com um tipo incompatível."))
                } else {
                    return Result.failure(Exception("Este e-mail já está cadastrado."))
                }
            }

            auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: ""

            val user = User(
                id = uid,
                nome = nome,
                email = email,
                tipo = tipo,
                responsavelId = responsavelId
            )

            db.collection("usuarios").document(uid).set(user).await()

            Result.success(uid)
        } catch (e: Exception) {
            return Result.failure(e)
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
