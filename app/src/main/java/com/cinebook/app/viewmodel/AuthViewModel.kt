package com.cinebook.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cinebook.app.data.AppDatabase
import com.cinebook.app.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val userDao = AppDatabase.get(app).userDao()
    private val prefs = app.getSharedPreferences("cinebook_prefs", Context.MODE_PRIVATE)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        // จำการล็อกอินครั้งก่อนไว้
        viewModelScope.launch {
            val savedId = prefs.getInt("uid", -1)
            if (savedId != -1) _user.value = userDao.getById(savedId)
            _ready.value = true
        }
    }

    private fun hash(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun login(username: String, password: String) {
        val u = username.trim().lowercase()
        if (u.isEmpty() || password.isEmpty()) {
            _error.value = "กรุณากรอกชื่อผู้ใช้และรหัสผ่าน"
            return
        }
        viewModelScope.launch {
            val found = userDao.login(u, hash(password))
            if (found == null) {
                _error.value = "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง"
            } else {
                _error.value = null
                prefs.edit().putInt("uid", found.id).apply()
                _user.value = found
            }
        }
    }

    fun register(username: String, displayName: String, password: String) {
        val u = username.trim().lowercase()
        val name = displayName.trim()
        if (u.length < 3) {
            _error.value = "ชื่อผู้ใช้ต้องยาวอย่างน้อย 3 ตัวอักษร"
            return
        }
        if (name.isEmpty()) {
            _error.value = "กรุณากรอกชื่อที่แสดง"
            return
        }
        if (password.length < 4) {
            _error.value = "รหัสผ่านต้องยาวอย่างน้อย 4 ตัวอักษร"
            return
        }
        viewModelScope.launch {
            if (userDao.findByUsername(u) != null) {
                _error.value = "ชื่อผู้ใช้นี้ถูกใช้แล้ว"
                return@launch
            }
            val id = userDao.insert(User(username = u, passwordHash = hash(password), displayName = name))
            _error.value = null
            prefs.edit().putInt("uid", id.toInt()).apply()
            _user.value = userDao.getById(id.toInt())
        }
    }

    fun logout() {
        prefs.edit().remove("uid").apply()
        _user.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
