package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.ProfileBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val okHttpConfig: OkHttpConfig, private val userLogin: String) : ViewModel() {

    private val _profileResult = MutableLiveData<Pair<Boolean, ProfileBody?>>()

    val profileResult: LiveData<Pair<Boolean, ProfileBody?>>
        get() = _profileResult

    fun getProfile() {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val profile = withContext(Dispatchers.IO) {
                apiManager.getProfile(userLogin)
            }
            val isSuccessful = profile != null
            _profileResult.value = Pair(isSuccessful, profile)
        }
    }
}