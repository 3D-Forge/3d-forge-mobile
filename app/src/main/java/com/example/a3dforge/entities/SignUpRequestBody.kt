package com.example.a3dforge.entities

data class SignUpRequestBody(
    val login: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)