package com.example.a3dforge.entities

import java.io.File

data class modelUploadBody(
    val name: String,
    val description: String,
    val depth: Int,
    val keywords: MutableList<String>?,
    val categoryes: MutableList<modelCategorye>,
    val files: MutableList<File>
)

data class modelCategorye(
    val id: Int
)