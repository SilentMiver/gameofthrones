package com.example.gameofthrones.network

import com.example.gameofthrones.data.Character
import retrofit2.http.GET
import retrofit2.http.Query

interface GoTService {
    @GET("api/characters")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<Character>
}