package com.example.a3dforge.entities

data class ProfileGetRequestBody(
    val success: Boolean,
    val message: String?,
    val data: UserData
) {
    data class UserData(
        val login: String,
        val email: String,
        val phoneNumber: String,
        val firstName: String,
        val midName: String,
        val lastName: String,
        val country: String,
        val region: String,
        val cityRegion: String,
        val city: String,
        val street: String,
        val house: String,
        val apartment: String,
        val departmentNumber: String,
        val deliveryType: String,
        val orderStateChangedNotification: Boolean,
        val getForumResponseNotification: Boolean,
        val modelRatedNotification: Boolean,
        val blocked: Boolean,
        val canAdministrateForum: Boolean,
        val canRetrieveDelivery: Boolean,
        val canModerateCatalog: Boolean,
        val canAdministrateSystem: Boolean,
        val isActivated: Boolean,
        val registrationDate: String
    )
}
