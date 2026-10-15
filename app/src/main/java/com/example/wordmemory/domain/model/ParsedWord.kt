package com.example.wordmemory.domain.model

data class ParsedWord(
    val word: String,
    val meaning: String,
    val imagePath: String? = null
)
