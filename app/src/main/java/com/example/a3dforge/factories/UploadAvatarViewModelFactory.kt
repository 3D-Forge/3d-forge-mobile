package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.UploadAvatarViewModel
import java.io.File

class UploadAvatarViewModelFactory(private val okHttpConfig: OkHttpConfig, private val file: File, private val userLogin: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadAvatarViewModel::class.java)) {
            return UploadAvatarViewModel(okHttpConfig, file, userLogin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}