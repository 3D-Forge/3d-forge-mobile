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

class DeleteCartViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _cartDeleteResult = MutableLiveData<Boolean>()

    val cartDeleteResult: LiveData<Boolean>
        get() = _cartDeleteResult

    fun deleteCart(modelId: Int) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val cart = withContext(Dispatchers.IO) {
                apiManager.deleteCart(modelId)
            }
            val isSuccessful = true
            _cartDeleteResult.value = isSuccessful
        }
    }
}