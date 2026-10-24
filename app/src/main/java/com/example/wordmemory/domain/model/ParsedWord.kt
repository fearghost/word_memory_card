package com.example.wordmemory.domain.model

data class ParsedWord(
    val id: Long = 0,
    val word: String,
    val meaning: String,
    val imagePath: String? = null,
    val isDifficult: Boolean = false
)
