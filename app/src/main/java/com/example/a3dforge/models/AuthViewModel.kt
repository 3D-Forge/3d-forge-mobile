package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {
    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()

    val authResult: LiveData<Pair<Boolean, String?>>
        get() = _authResult

    fun authenticateUser(loginOrEmail: String, password: String) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val login = withContext(Dispatchers.IO) {
                apiManager.authenticateUser(loginOrEmail, password)
            }
            val isSuccessful = login != null
            _authResult.value = Pair(isSuccessful, login)
        }
    }
}

