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
import java.io.File

class UploadAvatarViewModel(private val okHttpConfig: OkHttpConfig, private val file: File, private val userLogin: String) : ViewModel() {
    private val _avatarUploadResult = MutableLiveData<Boolean>()

    val avatarUploadResult: LiveData<Boolean>
        get() = _avatarUploadResult

    fun avatarUpload(){
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val avatarUpload = withContext(Dispatchers.IO) {
                apiManager.uploadAvatar(userLogin, file)
            }
            val isSuccessful = true
            _avatarUploadResult.value = isSuccessful
        }
    }
}