package com.example.wordmemory.data.local.room.dictionary

import android.content.Context
import kotlinx.coroutines.flow.Flow

class DictionaryRepository(private val dictionaryDao: DictionaryDao) {

    suspend fun getMeaning(word: String): String? {
        return dictionaryDao.getMeaning(word)
    }

    suspend fun insertAll(entries: List<DictionaryEntity>) {
        dictionaryDao.insertAll(entries)
    }

    suspend fun getEntries(limit: Int, offset: Int): List<DictionaryEntity> {
        return dictionaryDao.getEntries(limit, offset)
    }
}
