package com.example.a3dforge.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.base.OkHttpConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {
    private val _authResult = MutableLiveData<Boolean>()

    val authResult: LiveData<Boolean>
        get() = _authResult

    fun authenticateUser(loginOrEmail: String, password: String) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                apiManager.authenticateUser(loginOrEmail, password)
            }
            _authResult.value = success
        }
    }

}
