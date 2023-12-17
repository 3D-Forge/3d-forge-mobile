package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.CartGetRequestBody
import com.example.a3dforge.entities.CartPutRequestBody
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CartPutViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _cartPutResult = MutableLiveData<Boolean>()

    val cartPutResult: LiveData<Boolean>
        get() = _cartPutResult

    fun putCart(querry: CartPutRequestBody) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val cart = withContext(Dispatchers.IO) {
                apiManager.putModelToCart(querry)
            }
            val isSuccessful = true
            _cartPutResult.value = isSuccessful
        }
    }
}