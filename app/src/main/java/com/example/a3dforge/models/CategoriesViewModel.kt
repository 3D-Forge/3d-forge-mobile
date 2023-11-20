package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.CategoriesGetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _categoriesResult = MutableLiveData<CategoriesGetRequestBody?>()

    val categoriesResult: LiveData<CategoriesGetRequestBody?>
        get() = _categoriesResult

    fun getCategory() {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val category = withContext(Dispatchers.IO) {
                apiManager.getCategories()
            }
            _categoriesResult.value = category
        }
    }
}