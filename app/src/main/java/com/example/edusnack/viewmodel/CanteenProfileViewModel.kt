package com.example.edusnack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Simple data models for the profile
data class Employee(
    val name: String = "",
    val role: String = ""
)

data class CanteenProfile(
    val profileImageUrl: String = "",
    val canteenName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val responsibleName: String = "",
    val employees: List<Employee> = emptyList()
)

class CanteenProfileViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _profile = MutableStateFlow<CanteenProfile?>(null)
    val profile: StateFlow<CanteenProfile?> = _profile

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Only allow editing when the current user's tipo is canteen.
    private val _isEditable = MutableStateFlow(false)
    val isEditable: StateFlow<Boolean> = _isEditable

    private var profileListenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            observeUserTipo(uid)
            observeProfile(uid)
        } else {
            _loading.value = false
            _error.value = "Usuário não autenticado"
        }
    }

    private fun observeUserTipo(uid: String) {
        // Listen to 'usuarios/{uid}' to determine if this is a canteen account
        db.collection("usuarios").document(uid).addSnapshotListener { snap, ex ->
            if (ex != null) {
                _error.value = ex.message
                return@addSnapshotListener
            }
            val tipo = snap?.getString("tipo") ?: ""
            val isCanteen = tipo.lowercase().let { it == "cantina" || it == "canteen" || it == "canteen".lowercase() }
            _isEditable.value = isCanteen
        }
    }

    private fun observeProfile(uid: String) {
        _loading.value = true
        profileListenerRegistration = db.collection("users").document(uid).addSnapshotListener { snap, ex ->
            if (ex != null) {
                _error.value = ex.message
                _loading.value = false
                return@addSnapshotListener
            }

            if (snap == null || !snap.exists()) {
                _profile.value = CanteenProfile()
                _loading.value = false
                return@addSnapshotListener
            }

            try {
                val profileImageUrl = snap.getString("profileImageUrl") ?: ""
                val canteenName = snap.getString("canteenName") ?: ""
                val contactPhone = snap.getString("contactPhone") ?: ""
                val contactEmail = snap.getString("contactEmail") ?: ""
                val responsibleName = snap.getString("responsibleName") ?: ""

                val employeesRaw = snap.get("employees")
                val employees = when (employeesRaw) {
                    is List<*> -> employeesRaw.mapNotNull { elem ->
                        if (elem is Map<*, *>) {
                            val name = elem["name"] as? String ?: ""
                            val role = elem["role"] as? String ?: ""
                            Employee(name = name, role = role)
                        } else null
                    }
                    else -> emptyList()
                }

                val cp = CanteenProfile(
                    profileImageUrl = profileImageUrl,
                    canteenName = canteenName,
                    contactPhone = contactPhone,
                    contactEmail = contactEmail,
                    responsibleName = responsibleName,
                    employees = employees
                )

                _profile.value = cp
            } catch (e: Exception) {
                _error.value = e.message
            }

            _loading.value = false
        }
    }

    fun saveProfile(updated: CanteenProfile) {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true
        viewModelScope.launch {
            try {
                val map = mapOf(
                    "profileImageUrl" to updated.profileImageUrl,
                    "canteenName" to updated.canteenName,
                    "contactPhone" to updated.contactPhone,
                    "contactEmail" to updated.contactEmail,
                    "responsibleName" to updated.responsibleName,
                    "employees" to updated.employees.map { emp -> mapOf("name" to emp.name, "role" to emp.role) }
                )
                db.collection("users").document(uid).set(map).await()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
            _loading.value = false
        }
    }

    fun addEmployee(employee: Employee) {
        val current = _profile.value ?: CanteenProfile()
        val updated = current.copy(employees = current.employees + employee)
        _profile.value = updated
        saveProfile(updated)
    }

    fun updateEmployee(index: Int, employee: Employee) {
        val current = _profile.value ?: CanteenProfile()
        if (index < 0 || index >= current.employees.size) return
        val mutable = current.employees.toMutableList()
        mutable[index] = employee
        val updated = current.copy(employees = mutable)
        _profile.value = updated
        saveProfile(updated)
    }

    fun removeEmployee(index: Int) {
        val current = _profile.value ?: CanteenProfile()
        if (index < 0 || index >= current.employees.size) return
        val mutable = current.employees.toMutableList()
        mutable.removeAt(index)
        val updated = current.copy(employees = mutable)
        _profile.value = updated
        saveProfile(updated)
    }

    override fun onCleared() {
        super.onCleared()
        profileListenerRegistration?.remove()
    }
}
