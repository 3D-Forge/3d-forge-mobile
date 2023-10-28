package com.example.a3dforge.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.base.OkHttpConfig

class AuthRegisterViewModelFactory(private val okHttpConfig: OkHttpConfig) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(okHttpConfig) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(okHttpConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}