package com.example.a3dforge.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a3dforge.base.ApiManager.Companion.contentType
import com.example.a3dforge.base.OkHttpConfig
import com.example.a3dforge.entities.SignUpRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _registrationResult = MutableLiveData<Boolean>()

    val registrationResult: LiveData<Boolean>
        get() = _registrationResult

    suspend fun registerUser(login: String, email: String, password: String) {
        val requestBody = SignUpRequestBody(login, email, password)
        val registerRequestBodyString = okHttpConfig.gson.toJson(requestBody)
        val okHttpRequestBody = registerRequestBodyString.toRequestBody(contentType)

        val request = Request.Builder()
            .post(okHttpRequestBody)
            .url(okHttpConfig.baseUrl + "register")
            .build()

        try {
            val response = withContext(Dispatchers.IO) {
                okHttpConfig.client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                _registrationResult.postValue(true)
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                _registrationResult.postValue(false)
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            _registrationResult.postValue(false)
        }
    }
}