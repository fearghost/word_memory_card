package com.example.wordmemory.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WordBundleEntity::class, WordItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
