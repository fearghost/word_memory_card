package com.example.wordmemory.data.local.room.dictionary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wordmemory.data.local.room.dictionary.DictionaryEntity

@Dao
interface DictionaryDao {
    @Query("SELECT meaning FROM dictionary WHERE word = :word LIMIT 1")
    suspend fun getMeaning(word: String): String?

    @Query("SELECT * FROM dictionary LIMIT :limit OFFSET :offset")
    suspend fun getEntries(limit: Int, offset: Int): List<DictionaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DictionaryEntity>)
}
