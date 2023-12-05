package com.example.a3dforge.entities

data class CartGetRequestBody(
    val data: CartGetData,
    val success: Boolean,
    val message: Any,
)

data class CartGetData(
    val id: Int,
    val orderedModelIDs: List<OrderedModelID>
)

data class OrderedModelID(
    val id: Int,
    val catalogModelId: Int,
    val printExtensionName: String,
    val pricePerPiece: Int,
    val pieces: Int,
    val xSize: Int,
    val ySize: Int,
    val zSize: Int,
    val volume: Int,
    val scale: Int,
    val depth: Int,
    val color: String,
    val printMaterialName: String,
    val printTypeName: String,
    val totalPrice: Int
)