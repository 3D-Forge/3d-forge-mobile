package com.example.a3dforge.entities

data class CategoriesGetRequestBody(
    val success: Boolean,
    val message: String?,
    val data: List<Categoryes>
)