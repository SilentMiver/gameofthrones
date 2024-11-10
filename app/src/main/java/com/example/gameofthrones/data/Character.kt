package com.example.gameofthrones.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val url: String = "",
    val name: String = "",
    val culture: String = "",
    val born: String = "",
    val titles: List<String> = emptyList(),
    val aliases: List<String> = emptyList(),
    @SerialName("playedBy")
    val playedBy: List<String> = emptyList()
)