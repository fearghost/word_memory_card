package com.example.wordmemory.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wordmemory.data.local.room.dictionary.DictionaryDao
import com.example.wordmemory.data.local.room.dictionary.DictionaryEntity

@Database(entities = [WordBundleEntity::class, WordItemEntity::class, DictionaryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dictionaryDao(): DictionaryDao
}
