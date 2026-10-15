package com.example.wordmemory.data.local.room.dictionary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordmemory.data.local.room.dictionary.DictionaryDao
import com.example.wordmemory.data.local.room.dictionary.DictionaryEntity

@Database(entities = [DictionaryEntity::class], version = 1, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        fun getDatabase(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
