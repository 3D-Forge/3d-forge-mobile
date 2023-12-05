package com.example.a3dforge.entities

data class CartPutRequestBody(
    val success: Boolean,
    val message: String,
    val data: CartPutData
)

data class CartPutData(
    val color: String,
    val depth: Int,
    val id: Int,
    val pieces: Int,
    val pricePerPiece: Int,
    val printExtensionName: String,
    val printMaterialName: String,
    val printTypeName: String,
    val scale: Int,
    val totalPrice: Int,
    val volume: Int,
    val xSize: Int,
    val ySize: Int,
    val zSize: Int
)