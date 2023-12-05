package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.CatalogGetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogSearchViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _catalogGetResult = MutableLiveData<Pair<Boolean, CatalogGetRequestBody?>>()

    val profileResult: LiveData<Pair<Boolean, CatalogGetRequestBody?>>
        get() = _catalogGetResult

    fun getCatalog(q: String?, categories: Array<String>?, keywords: Array<String>?, sort_parameter: String?, sort_direction: String?, min_price: Double?, max_price: Double?, rating: Array<Int>?, author: String?, page: Int?, page_size: Int?) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val profile = withContext(Dispatchers.IO) {
                apiManager.getAllModels(q, categories, keywords, sort_parameter, sort_direction, min_price, max_price, rating, author, page, page_size)
            }
            val isSuccessful = profile != null
            _catalogGetResult.value = Pair(isSuccessful, profile)
        }
    }
}