package com.example.wordmemory.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bundleId: Long,
    val date: Long,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongWordsJson: String // JSON string of wrong words (word + meaning)
)
