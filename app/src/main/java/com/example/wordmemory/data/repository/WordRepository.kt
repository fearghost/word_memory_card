package com.example.wordmemory.data.repository

import com.example.wordmemory.data.local.room.*
import com.example.wordmemory.domain.model.ParsedWord
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    
    fun getAllBundles(): Flow<List<WordBundleEntity>> = wordDao.getAllBundles()

    suspend fun createBundle(name: String): Long {
        val bundle = WordBundleEntity(name = name)
        return wordDao.insertBundle(bundle)
    }

    suspend fun saveWordsToBundle(bundleId: Long, words: List<ParsedWord>) {
        val entities = words.map { 
            WordItemEntity(bundleId = bundleId, word = it.word, meaning = it.meaning) 
        }
        wordDao.insertItems(entities)
    }
}
