package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord

class TextParser {
    /**
     * Extracts word-meaning pairs from raw OCR text.
     * Supports patterns like:
     * "apple: 사과" (Explicit separator)
     * "apple 사과" (Implicit separator - split by Hangul)
     */
    fun parse(rawText: String): List<ParsedWord> {
        val results = mutableListOf<ParsedWord>()
        val lines = rawText.lines()

        // Explicit separators: ':', '-', '|', '='
        val separatorRegex = Regex("[:\\-|\\=]")

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            val match = separatorRegex.find(trimmedLine)
            if (match != null) {
                val index = match.range.first
                val word = trimmedLine.substring(0, index).trim()
                val meaning = trimmedLine.substring(index + 1).trim()

                if (word.isNotEmpty() && meaning.isNotEmpty()) {
                    results.add(ParsedWord(word, meaning))
                } else {
                    // If one side is empty, treat the whole line as a word
                    results.add(ParsedWord(trimmedLine, ""))
                }
            } else {
                // No explicit separator found.
                // Attempt to split by the first occurrence of a Hangul character.
                val hangulRegex = Regex("[\\uAC00-\\uD7A3\\u3130-\\u318F]")
                val hangulMatch = hangulRegex.find(trimmedLine)

                if (hangulMatch != null) {
                    val index = hangulMatch.range.first
                    val word = trimmedLine.substring(0, index).trim()
                    val meaning = trimmedLine.substring(index).trim()

                    if (word.isNotEmpty() && meaning.isNotEmpty()) {
                        results.add(ParsedWord(word, meaning))
                    } else {
                        // If split results in empty word or meaning, treat as single word
                        results.add(ParsedWord(trimmedLine, ""))
                    }
                } else {
                    // No Hangul found, treat the whole line as a word
                    results.add(ParsedWord(trimmedLine, ""))
                }
            }
        }
        return results
    }
}
