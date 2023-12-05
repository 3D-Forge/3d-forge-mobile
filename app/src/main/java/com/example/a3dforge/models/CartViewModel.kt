package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.CartGetRequestBody
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _cartResult = MutableLiveData<Pair<Boolean, CartGetRequestBody?>>()

    val cartResult: LiveData<Pair<Boolean, CartGetRequestBody?>>
        get() = _cartResult

    fun getCart() {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val cart = withContext(Dispatchers.IO) {
                apiManager.getCart()
            }
            val isSuccessful = cart != null
            _cartResult.value = Pair(isSuccessful, cart)
        }
    }
}