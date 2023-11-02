package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.ProfileViewModel

class ProfileViewModelFactory(private val okHttpConfig: OkHttpConfig, private val userLogin: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(okHttpConfig, userLogin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}