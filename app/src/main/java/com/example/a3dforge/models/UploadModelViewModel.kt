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

class UploadModelViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {
    private val _uploadModelResult = MutableLiveData<Boolean>()

    val uploadModelResult: LiveData<Boolean>
        get() = _uploadModelResult

    fun uploadModel(name: String, desc: String, depth: String, keywords: Array<String>?, categories: Array<Int>, files: Array<File>){
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    apiManager.uploadModel(name, desc, depth, keywords, categories, files)
                }
                _uploadModelResult.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadModelResult.value = false
            }
        }
    }
}