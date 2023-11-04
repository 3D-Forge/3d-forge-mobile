package com.example.a3dforge.base

import OkHttpConfig
import android.util.Log
import com.example.a3dforge.entities.ProfileBody
import com.example.a3dforge.entities.SignInRequestBody
import com.example.a3dforge.entities.SignUpRequestBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ApiManager(private val okHttpConfig: OkHttpConfig) {

    fun authenticateUser(loginOrEmail: String, password: String): String? {
        val requestBody = SignInRequestBody(loginOrEmail, password)
        val loginRequestBodyString = okHttpConfig.gson.toJson(requestBody)
        val okHttpRequestBody = loginRequestBodyString.toRequestBody(contentType)

        val request = Request.Builder()
            .post(okHttpRequestBody)
            .url(okHttpConfig.baseUrl + "login")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonObject = responseData?.let { JSONObject(it) }
                val login = jsonObject?.getJSONObject("data")?.getString("login")
                responseBody?.close()
                return login
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                responseBody?.close()
                return null
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            return null
        }
    }

    fun registerUser(login: String, email: String, password: String, confirmPassword: String): Boolean {
        val requestBody = SignUpRequestBody(login, email, password, confirmPassword)
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

    fun getProfile(userLogin: String): ProfileBody? {
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseUrl + userLogin + "/info")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.string()
                responseBody?.close()
                responseData?.let {
                    val jsonObject = JSONObject(it)
                    val userData = ProfileBody.UserData(
                        login = jsonObject.getJSONObject("data").getString("login"),
                        email = jsonObject.getJSONObject("data").getString("email"),
                        phone = jsonObject.getJSONObject("data").optString("phoneNumber"),
                        firstName = jsonObject.getJSONObject("data").optString("firstName"),
                        middleName = jsonObject.getJSONObject("data").optString("midName"),
                        lastName = jsonObject.getJSONObject("data").optString("lastName"),
                        region = jsonObject.getJSONObject("data").optString("region"),
                        cityRegion = jsonObject.getJSONObject("data").optString("cityRegion"),
                        city = jsonObject.getJSONObject("data").optString("city"),
                        street = jsonObject.getJSONObject("data").optString("street"),
                        house = jsonObject.getJSONObject("data").optString("house"),
                        apartment = jsonObject.getJSONObject("data").optString("apartment"),
                        departmentNumber = jsonObject.getJSONObject("data").optString("departmentNumber"),
                        deliveryType = jsonObject.getJSONObject("data").optString("deliveryType")
                    )
                    return ProfileBody(response.isSuccessful, null, userData)
                }
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                responseBody?.close()
                return null
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            return null
        }
        return null
    }

    fun changeProfile(querry: JSONObject, userLogin: String) {
        val jsonRequestBody = querry.toString()
        val requestBody = jsonRequestBody.toRequestBody("application/json".toMediaTypeOrNull())
        val url = OkHttpConfig.baseUrl + "update/info"
        val httpUrl = url.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("login", userLogin)
            ?.build()
        val request = Request.Builder()
            .url(httpUrl!!)
            .put(requestBody)
            .build()
        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                responseBody?.close()
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                responseBody?.close()
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
        }
    }

    fun getAvatar(userLogin: String): ByteArray? {
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseUrl + userLogin + "/avatar")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.bytes()
                responseBody?.close()
                return responseData
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                responseBody?.close()
                return null
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
            return null
        }
    }

    fun uploadAvatar(userLogin: String, file: File){
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userAvatarFile", file.name, file.asRequestBody("image/png".toMediaTypeOrNull()))
            .addFormDataPart("login", userLogin)
            .build()

        val request = Request.Builder()
            .url(OkHttpConfig.baseUrl + "update/avatar")
            .put(requestBody)
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                responseBody?.close()
            } else {
                Log.e("ApiManager", "Unsuccessful response: ${response.code}")
                responseBody?.close()
            }
        } catch (e: IOException) {
            Log.e("ApiManager", "Network error", e)
        }

    }


    companion object {
        val contentType = "application/json; charset=utf-8".toMediaType()
    }
}