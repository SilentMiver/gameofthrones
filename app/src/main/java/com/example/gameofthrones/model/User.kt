package com.example.gameofthrones.model

import java.io.Serializable

data class User(
    val username: String,
    val email: String,
    val password: String
) : Serializable