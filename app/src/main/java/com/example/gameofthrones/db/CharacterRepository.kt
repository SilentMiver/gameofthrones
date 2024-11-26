package com.example.gameofthrones.db

import com.example.gameofthrones.db.model.CharacterEntity
import kotlinx.coroutines.flow.Flow

class CharacterRepository(private val characterDao: CharacterDao) {

    suspend fun insert(character: CharacterEntity) {
        characterDao.insert(character)
    }

    suspend fun insertAll(characters: List<CharacterEntity>) {
        characterDao.insertAll(characters)
    }

    fun getAllCharacters(): Flow<List<CharacterEntity>> {
        return characterDao.getAllCharacters()
    }

    suspend fun getCharacterById(id: Int): CharacterEntity? {
        return characterDao.getCharacterById(id)
    }

    suspend fun update(character: CharacterEntity) {
        characterDao.update(character)
    }

    suspend fun delete(character: CharacterEntity) {
        characterDao.delete(character)
    }

    suspend fun deleteAllCharacters() {
        characterDao.deleteAll()
    }
}

