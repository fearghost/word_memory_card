package com.example.wordmemory.data.repository

import com.example.wordmemory.data.local.room.*
import com.example.wordmemory.domain.model.ParsedWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map as flowMap

class WordRepository(private val wordDao: WordDao) {
    
    fun getAllBundles(): Flow<List<WordBundleEntity>> = wordDao.getAllBundles()

    fun getAllBundlesWithWordCount(): Flow<List<WordDao.BundledWordCount>> = wordDao.getAllBundlesWithWordCount()

    suspend fun createBundle(name: String): Long {
        val bundle = WordBundleEntity(name = name)
        return wordDao.insertBundle(bundle)
    }

    suspend fun saveWordsToBundle(bundleId: Long, words: List<ParsedWord>) {
        val entities = words.map { 
            WordItemEntity(bundleId = bundleId, word = it.word, meaning = it.meaning, imagePath = it.imagePath) 
        }
        wordDao.insertItems(entities)
    }

    suspend fun updateWordsInBundle(bundleId: Long, words: List<ParsedWord>) {
        wordDao.deleteItemsByBundleId(bundleId)
        val entities = words.map { 
            WordItemEntity(bundleId = bundleId, word = it.word, meaning = it.meaning, imagePath = it.imagePath) 
        }
        wordDao.insertItems(entities)
    }


    suspend fun deleteBundle(bundle: com.example.wordmemory.data.local.room.WordBundleEntity) {
        wordDao.deleteBundle(bundle)
    }

    suspend fun getWordsByBundle(bundleId: Long): List<ParsedWord> {
        val entities = wordDao.getItemsByBundle(bundleId)
        return entities.map { 
            ParsedWord(
                id = it.id,
                word = it.word,
                meaning = it.meaning,
                imagePath = it.imagePath,
                isDifficult = it.isDifficult
            ) 
        }
    }

    fun getWordsByBundleFlow(bundleId: Long): Flow<List<ParsedWord>> {
        return wordDao.getWordsByBundleFlow(bundleId).flowMap { entities ->
            entities.map { 
                ParsedWord(
                    id = it.id,
                    word = it.word,
                    meaning = it.meaning,
                    imagePath = it.imagePath,
                    isDifficult = it.isDifficult
                ) 
            }
        }
    }

    suspend fun getDifficultWordsByBundle(bundleId: Long): List<ParsedWord> {
        val entities = wordDao.getDifficultItemsByBundle(bundleId)
        return entities.map { 
            ParsedWord(
                id = it.id,
                word = it.word,
                meaning = it.meaning,
                imagePath = it.imagePath,
                isDifficult = it.isDifficult
            ) 
        }
    }

    suspend fun toggleWordDifficult(itemId: Long, bundleId: Long, isDifficult: Boolean) {
        wordDao.toggleDifficult(itemId, bundleId, isDifficult)
    }

    // Test result history
    suspend fun saveTestResult(bundleId: Long, totalQuestions: Int, correctAnswers: Int, wrongWordsJson: String) {
        val result = TestResultEntity(
            bundleId = bundleId,
            date = System.currentTimeMillis(),
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            wrongWordsJson = wrongWordsJson
        )
        wordDao.insertTestResult(result)
        wordDao.pruneOldResults(bundleId)
    }

    fun getTestResultsFlow(bundleId: Long): Flow<List<TestResultEntity>> {
        return wordDao.getRecentTestResults(bundleId)
    }
}
