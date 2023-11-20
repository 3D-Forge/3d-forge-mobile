package com.example.a3dforge.base

import OkHttpConfig
import android.util.Log
import com.example.a3dforge.entities.CatalogGetRequestBody
import com.example.a3dforge.entities.CategoriesGetRequestBody
import com.example.a3dforge.entities.ProfileRequestBody
import com.example.a3dforge.entities.SignInRequestBody
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
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
            .url(okHttpConfig.baseUserUrl + "login")
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

    fun getProfile(userLogin: String): ProfileRequestBody? {
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseUserUrl + userLogin + "/info")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.string()
                responseBody?.close()
                responseData?.let {
                    val jsonObject = JSONObject(it)
                    val userData = ProfileRequestBody.UserData(
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
                    return ProfileRequestBody(response.isSuccessful, null, userData)
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
        val url = OkHttpConfig.baseUserUrl + "update/info"
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
            .url(okHttpConfig.baseUserUrl + userLogin + "/avatar")
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
            .url(OkHttpConfig.baseUserUrl + "update/avatar")
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

    fun uploadModel(name: String, desc: String, depth: String, keywords: Array<String>?, categories: Array<Int>, files: Array<File>) {
        val url = (okHttpConfig.baseCatalogUrl).toHttpUrlOrNull()

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        name.let { requestBodyBuilder.addFormDataPart("Name", it) }
        desc.let { requestBodyBuilder.addFormDataPart("Description", it) }
        depth.let { requestBodyBuilder.addFormDataPart("Depth", it) }

        keywords?.let { keywordsArray ->
            keywordsArray.forEach {
                requestBodyBuilder.addFormDataPart("Keywords", it)
            }
        }

        categories.let { categoriesArray ->
            categoriesArray.forEach { category ->
                requestBodyBuilder.addFormDataPart("Categories", category.toString())
            }
        }

        files.forEach { file ->
            val requestBody = file.asRequestBody()
            if (file.extension != "png"){
                requestBodyBuilder.addFormDataPart("files", file.name, requestBody)
            }
            else{
                requestBodyBuilder.addFormDataPart("files", file.name, file.asRequestBody("image/png".toMediaTypeOrNull()))
            }
        }


        val request = Request.Builder()
            .url(url!!)
            .post(requestBodyBuilder.build())
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

    fun getAllModels(q: String?, categories: Array<String>?, keywords: Array<String>?, sort_parameter: String?, sort_direction: String?, min_price: Double?, max_price: Double?, min_rating: Float?, max_rating: Float?, author: String?, page: Int?, page_size: Int?): CatalogGetRequestBody? {
        val builder = (okHttpConfig.baseCatalogUrl + "/search").toHttpUrlOrNull()?.newBuilder()

        q?.let { builder?.addQueryParameter("q", it) }
        categories?.let { categoriesList ->
            categoriesList.forEach { index ->
                builder?.addQueryParameter("categories", index)
            }
        }
        keywords?.let { builder?.addQueryParameter("keywords", it.joinToString(",")) }
        sort_parameter?.let { builder?.addQueryParameter("sort_parameter", it) }
        sort_direction?.let { builder?.addQueryParameter("sort_direction", it) }
        min_price?.let { builder?.addQueryParameter("min_price", it.toString()) }
        max_price?.let { builder?.addQueryParameter("max_price", it.toString()) }
        min_rating?.let { builder?.addQueryParameter("min_rating", it.toString()) }
        max_rating?.let { builder?.addQueryParameter("max_rating", it.toString()) }
        author?.let { builder?.addQueryParameter("author", it) }
        page?.let { builder?.addQueryParameter("page", it.toString()) }
        page_size?.let { builder?.addQueryParameter("page_size", it.toString()) }

        val request = Request.Builder()
            .get()
            .url(builder?.build() ?: return null)
            .build()
        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.string()
                val gson = Gson()
                val data = gson.fromJson(responseData, CatalogGetRequestBody::class.java)
                responseBody?.close()
                return data
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

    fun getProductPicture(pictureId: Int): ByteArray?{
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseCatalogUrl + "/model/picture/${pictureId}")
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

    fun getCategories() : CategoriesGetRequestBody? {
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseCatalogUrl + "/categories")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.string()
                val gson = Gson()
                val data = gson.fromJson(responseData, CategoriesGetRequestBody::class.java)
                responseBody?.close()
                return data
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

    companion object {
        val contentType = "application/json; charset=utf-8".toMediaType()
    }
}