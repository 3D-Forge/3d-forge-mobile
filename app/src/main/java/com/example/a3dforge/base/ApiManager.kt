package com.example.a3dforge.base

import OkHttpConfig
import android.util.Log
import com.example.a3dforge.entities.CartGetRequestBody
import com.example.a3dforge.entities.CartPutRequestBody
import com.example.a3dforge.entities.CatalogGetRequestBody
import com.example.a3dforge.entities.CategoriesGetRequestBody
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import com.example.a3dforge.entities.OrderPutRequestBody
import com.example.a3dforge.entities.ProfileGetRequestBody
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

    fun getProfile(userLogin: String): ProfileGetRequestBody? {
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
                    val userData = ProfileGetRequestBody.UserData(
                        login = jsonObject.getJSONObject("data").getString("login"),
                        email = jsonObject.getJSONObject("data").getString("email"),
                        phoneNumber = jsonObject.getJSONObject("data").optString("phoneNumber"),
                        firstName = jsonObject.getJSONObject("data").optString("firstName"),
                        midName = jsonObject.getJSONObject("data").optString("midName"),
                        lastName = jsonObject.getJSONObject("data").optString("lastName"),
                        country = jsonObject.getJSONObject("data").optString("country"),
                        region = jsonObject.getJSONObject("data").optString("region"),
                        cityRegion = jsonObject.getJSONObject("data").optString("cityRegion"),
                        city = jsonObject.getJSONObject("data").optString("city"),
                        street = jsonObject.getJSONObject("data").optString("street"),
                        house = jsonObject.getJSONObject("data").optString("house"),
                        apartment = jsonObject.getJSONObject("data").optString("apartment"),
                        departmentNumber = jsonObject.getJSONObject("data").optString("departmentNumber"),
                        deliveryType = jsonObject.getJSONObject("data").optString("deliveryType"),
                        orderStateChangedNotification = jsonObject.getJSONObject("data").getBoolean("orderStateChangedNotification"),
                        getForumResponseNotification = jsonObject.getJSONObject("data").getBoolean("getForumResponseNotification"),
                        modelRatedNotification = jsonObject.getJSONObject("data").getBoolean("modelRatedNotification"),
                        blocked = jsonObject.getJSONObject("data").getBoolean("blocked"),
                        canAdministrateForum = jsonObject.getJSONObject("data").getBoolean("canAdministrateForum"),
                        canRetrieveDelivery = jsonObject.getJSONObject("data").getBoolean("canRetrieveDelivery"),
                        canModerateCatalog = jsonObject.getJSONObject("data").getBoolean("canModerateCatalog"),
                        canAdministrateSystem = jsonObject.getJSONObject("data").getBoolean("canAdministrateSystem"),
                        isActivated = jsonObject.getJSONObject("data").getBoolean("isActivated"),
                        registrationDate = jsonObject.getJSONObject("data").getString("registrationDate")
                    )
                    return ProfileGetRequestBody(response.isSuccessful, null, userData)
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

    fun getAllModels(q: String?, categories: Array<String>?, keywords: Array<String>?, sort_parameter: String?, sort_direction: String?, min_price: Double?, max_price: Double?, rating: Array<Int>?, author: String?, page: Int?, page_size: Int?): CatalogGetRequestBody? {
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
        rating?.let { ratingList ->
            ratingList.forEach { index ->
                builder?.addQueryParameter("rating", index.toString())
            }
        }
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

    fun getModelById(id: Int): ModelByIdGetRequestBody? {
        val builder = (okHttpConfig.baseCatalogUrl + "/${id}").toHttpUrlOrNull()?.newBuilder()

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
                val data = gson.fromJson(responseData, ModelByIdGetRequestBody::class.java)
                Log.e("ApiManager", "$data")
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

    fun getCart(): CartGetRequestBody? {
        val request = Request.Builder()
            .get()
            .url(okHttpConfig.baseUrl + "cart/getItems")
            .build()

        try {
            val response = okHttpConfig.client.newCall(request).execute()
            val responseBody = response.body

            if (response.isSuccessful) {
                val responseData = responseBody?.string()
                val gson = Gson()
                val data = gson.fromJson(responseData, CartGetRequestBody::class.java)
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

    fun deleteCart(modelId: Int) {
        val builder = (okHttpConfig.baseUrl + "cart").toHttpUrlOrNull()?.newBuilder()

        modelId.let { builder?.addQueryParameter("orderedModelId", it.toString())}

        val request = Request.Builder()
            .delete()
            .url(builder?.build()!!)
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

    fun putModelToCart(putData: CartPutRequestBody) {
        val url = OkHttpConfig.baseUrl + "cart"
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("CatalogModelId", putData.catalogModelID.toString())
            .addFormDataPart("Pieces", putData.pieces.toString())
            .addFormDataPart("Depth", putData.depth.toString())
            .addFormDataPart("Scale", putData.scale.toString())
            .addFormDataPart("ColorId", putData.colorId.toString())
            .addFormDataPart("PrintTypeName", putData.printTypeName)
            .addFormDataPart("PrintMaterialName", putData.printMaterialName)
            .addFormDataPart("File", (putData.file ?: "").toString())

        val request = Request.Builder()
            .url(url)
            .put(requestBody.build())
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

    fun putOrder(putData: OrderPutRequestBody) {
        val url = OkHttpConfig.baseUrl + "orders"

        val jsonMediaType = "application/json".toMediaType()
        val requestBody = """
            {
                "cartId": ${putData.cartId},
                "firstname": "${putData.firstname}",
                "midname": "${putData.midname}",
                "lastname": "${putData.lastname}",
                "country": "${putData.country}",
                "region": "${putData.region}",
                "city": "${putData.city}",
                "cityRegion": "${putData.cityRegion}",
                "street": "${putData.street}",
                "house": "${putData.house}",
                "apartment": "${putData.apartment}"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
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

    companion object {
        val contentType = "application/json; charset=utf-8".toMediaType()
    }
}