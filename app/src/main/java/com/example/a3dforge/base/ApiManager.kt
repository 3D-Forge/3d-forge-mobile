package com.example.a3dforge.base

import android.util.Log
import com.example.a3dforge.entities.SignInRequestBody
import com.example.a3dforge.entities.SignUpRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiManager(private val okHttpConfig: OkHttpConfig) {

    fun authenticateUser(loginOrEmail: String, password: String): Boolean {
        val requestBody = SignInRequestBody(loginOrEmail, password)
        val loginRequestBodyString = okHttpConfig.gson.toJson(requestBody)
        val okHttpRequestBody = loginRequestBodyString.toRequestBody(contentType)

        val request = Request.Builder()
            .post(okHttpRequestBody)
            .url(okHttpConfig.baseUrl + "login")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            if (response.isSuccessful) {
                return true
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                return false
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            return false
        }
    }

    fun registerUser(login: String, email: String, password: String): Boolean {
        val requestBody = SignUpRequestBody(login, email, password)
        val registerRequestBodyString = okHttpConfig.gson.toJson(requestBody)
        val okHttpRequestBody = registerRequestBodyString.toRequestBody(contentType)

        val request = Request.Builder()
            .post(okHttpRequestBody)
            .url(okHttpConfig.baseUrl + "register")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            if (response.isSuccessful) {
                return true
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                return false
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            return false
        }
    }

    companion object {
        val contentType = "application/json; charset=utf-8".toMediaType()
    }
}