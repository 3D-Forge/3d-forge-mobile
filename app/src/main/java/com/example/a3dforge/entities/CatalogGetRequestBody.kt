package com.example.a3dforge.entities

import android.os.Parcelable

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

data class Categories(
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
    val xSize: Int,
    val ySize: Int,
    val zSize: Int,
    val depth: Int,
    val minPrice: Int,
    val rating: Float,
    val volume: Float,
    val picturesIDs: List<Int>,
    val categories: List<Categories>,
    val keywords: List<Any>
)