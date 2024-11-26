package com.example.gameofthrones.network.model

import com.google.gson.annotations.SerializedName

data class Character(
    var name: String? = null,
    var culture: String? = null,
    var born: String? = null,
    var titles: List<String>? = null,
    var aliases: List<String>? = null,
    @SerializedName("playedBy")
    var playedBy: List<String>? = null
)