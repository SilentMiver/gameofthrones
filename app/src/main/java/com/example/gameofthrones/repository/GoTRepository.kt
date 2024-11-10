package com.example.gameofthrones.repository

import com.example.gameofthrones.network.GoTService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class GoTRepository {
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.anapioficeandfire.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val service = retrofit.create(GoTService::class.java)

    suspend fun getCharacters(page: Int, pageSize: Int = 50) = service.getCharacters(page, pageSize)
}