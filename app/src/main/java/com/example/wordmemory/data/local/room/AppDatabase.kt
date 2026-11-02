package com.example.wordmemory.data.local.room

import androidx.room.Database
import androidx.room.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wordmemory.data.local.room.dictionary.DictionaryDao
import com.example.wordmemory.data.local.room.dictionary.DictionaryEntity

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `test_results` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`bundleId` INTEGER NOT NULL, " +
            "`date` INTEGER NOT NULL, " +
            "`totalQuestions` INTEGER NOT NULL, " +
            "`correctAnswers` INTEGER NOT NULL, " +
            "`wrongWordsJson` TEXT NOT NULL DEFAULT ''" +
            ")"
        )
    }
}

@Database(entities = [WordBundleEntity::class, WordItemEntity::class, DictionaryEntity::class, TestResultEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        @JvmStatic
        val MIGRATION_1_2 = MIGRATION_1_2
    }
}
