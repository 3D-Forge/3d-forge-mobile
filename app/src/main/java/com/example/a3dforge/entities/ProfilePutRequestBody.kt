package com.example.a3dforge.entities

data class ProfilePutRequestBody(
    val success: Boolean,
    val message: String?,
    val data: UserPutData
) {
    data class UserPutData(
        val login: String?,
        val phoneNumber: String?,
        val firstName: String?,
        val midName: String?,
        val lastName: String?,
        val region: String?,
        val cityRegion: String?,
        val city: String?,
        val street: String?,
        val house: String?,
        val apartment: String?,
        val departmentNumber: String?,
        val deliveryType: String?,
        val orderStateChangedNotification: Boolean?,
        val getForumResponseNotification: Boolean?,
        val modelRatedNotification: Boolean?
    )
}
