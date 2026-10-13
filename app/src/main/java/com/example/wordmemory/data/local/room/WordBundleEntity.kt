package com.example.wordmemory.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_bundles")
data class WordBundleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
