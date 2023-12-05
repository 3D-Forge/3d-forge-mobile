package com.example.a3dforge.entities

data class ModelByIdGetRequestBody(
    val success: Boolean,
    val message: String?,
    val data: Item
)