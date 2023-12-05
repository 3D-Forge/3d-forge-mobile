package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.ModelByIdViewModel

class ModelByIdViewModelFactory(private val okHttpConfig: OkHttpConfig) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModelByIdViewModel::class.java)) {
            return ModelByIdViewModel(okHttpConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
