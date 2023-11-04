package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.ChangeProfileViewModel
import org.json.JSONObject

class ChangeProfileViewModelFactory(private val okHttpConfig: OkHttpConfig, private val querry: JSONObject, private val userLogin: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeProfileViewModel::class.java)) {
            return ChangeProfileViewModel(okHttpConfig, querry, userLogin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}