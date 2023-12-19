package com.example.a3dforge.entities

import java.io.File

data class OrderPutRequestBody(
    val cartId: Int,
    val firstname: String,
    val midname: String,
    val lastname: String,
    val country: String,
    val region: String,
    val city: String,
    val cityRegion: String,
    val street: String,
    val house: String,
    val apartment: String
)