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
import com.example.a3dforge.entities.OrderPutRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class OrderPutViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _orderPutResult = MutableLiveData<Boolean>()

    val orderPutResult: LiveData<Boolean>
        get() = _orderPutResult

    fun putOrder(querry: OrderPutRequestBody) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val order = withContext(Dispatchers.IO) {
                apiManager.putOrder(querry)
            }
            val isSuccessful = true
            _orderPutResult.value = isSuccessful
        }
    }
}