package com.example.a3dforge.entities

data class SignInRequestBody(
    val loginOrEmail: String,
    val password: String
)