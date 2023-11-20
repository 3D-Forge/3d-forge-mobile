package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.UploadAvatarViewModel
import com.example.a3dforge.models.UploadModelViewModel
import java.io.File

class UploadModelViewModelFactory(private val okHttpConfig: OkHttpConfig) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadModelViewModel::class.java)) {
            return UploadModelViewModel(okHttpConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}