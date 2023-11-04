package com.example.a3dforge.entities

import com.google.gson.annotations.SerializedName

data class ProfileBody(
    val success: Boolean,
    val message: String?,
    val data: UserData
) {
    data class UserData(
        val login: String,
        val email: String?,
        @SerializedName("phoneNumber")
        val phone: String?,
        val firstName: String?,
        @SerializedName("midName")
        val middleName: String?,
        val lastName: String?,
        val region: String?,
        @SerializedName("cityRegion")
        val cityRegion: String?,
        val city: String?,
        val street: String?,
        val house: String?,
        val apartment: String?,
        @SerializedName("departmentNumber")
        val departmentNumber: String?,
        @SerializedName("deliveryType")
        val deliveryType: String?,
    )
}
