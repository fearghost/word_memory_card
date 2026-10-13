package com.example.wordmemory.data.local.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_items",
    foreignKeys = [
        ForeignKey(
            entity = WordBundleEntity::class,
            parentColumns = ["id"],
            childColumns = ["bundleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["bundleId"])]
)
data class WordItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bundleId: Long,
    val word: String,
    val meaning: String
)
