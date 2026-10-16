package com.example.wordmemory.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wordmemory.data.local.room.dictionary.DictionaryDao

@Database(entities = [WordBundleEntity::class, WordItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dictionaryDao(): DictionaryDao
}
