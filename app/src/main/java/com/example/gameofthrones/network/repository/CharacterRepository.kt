package com.example.gameofthrones.network.repository

import com.example.gameofthrones.network.ApiClient
import com.example.gameofthrones.network.ApiService
import com.example.gameofthrones.network.model.Character
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharacterRepository {
    private val apiService: ApiService = ApiClient.client.create(ApiService::class.java)

    fun fetchCharacters(page: Int, limit: Int, callback: FetchCharactersCallback) {
        apiService.getCharacters(page, limit).enqueue(object : Callback<List<Character>> {
            override fun onResponse(
                call: Call<List<Character>>,
                response: Response<List<Character>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Character>>, t: Throwable) {
                callback.onError("Network error: ${t.message}")
            }
        })
    }

    interface FetchCharactersCallback {
        fun onSuccess(characters: List<Character>)
        fun onError(errorMessage: String)
    }
}