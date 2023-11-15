package com.example.a3dforge.entities

data class CatalogGetRequestBody(
    val success: Boolean,
    val message: String?,
    val data: Data
)

data class Data(
    val pageIndex: Int,
    val pageSize: Int,
    val pageCount: Int,
    val items: List<Item>
)

data class Categoryes(
    val id: Int,
    val name: String
)

data class Item(
    val id: Int,
    val name: String,
    val description: String,
    val printFileSize: Int,
    val modelFileSize: Int,
    val owner: String,
    val uploaded: String,
    val height: Int,
    val width: Int,
    val depth: Int,
    val minPrice: Int,
    val rating: Float,
    val volume: Float,
    val picturesIDs: List<Int>,
    val categoryes: List<Categoryes>,
    val keywords: List<Any>
)