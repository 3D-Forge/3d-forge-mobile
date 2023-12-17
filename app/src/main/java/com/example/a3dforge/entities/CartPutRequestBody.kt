package com.example.a3dforge.entities

import java.io.File

data class CartPutRequestBody(
    val catalogModelID: Int,
    val pieces: Int,
    val depth: Int,
    val scale: Int,
    val colorId: Int,
    val printTypeName: String,
    val printMaterialName: String,
    val file: File?
)