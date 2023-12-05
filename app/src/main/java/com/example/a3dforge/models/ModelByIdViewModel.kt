package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.CatalogGetRequestBody
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelByIdViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _modelGetResult = MutableLiveData<Pair<Boolean, ModelByIdGetRequestBody?>>()

    val modelResult: LiveData<Pair<Boolean, ModelByIdGetRequestBody?>>
        get() = _modelGetResult

    fun getModelById(id: Int) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val model = withContext(Dispatchers.IO) {
                apiManager.getModelById(id)
            }
            val isSuccessful = model != null
            _modelGetResult.value = Pair(isSuccessful, model)
        }
    }
}