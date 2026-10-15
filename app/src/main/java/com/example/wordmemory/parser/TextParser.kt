package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord
import java.util.regex.Pattern

class TextParser {
    /**
     * Extracts word-meaning pairs from raw OCR text.
     * Supports patterns like:
     * "apple: 사과"
     * "banana - 바나나"
     * "orange | 오렌지"
     */
    fun parse(rawText: String): List<ParsedWord> {
        val results = mutableListOf<ParsedWord>()
        val lines = rawText.lines()

        // Regex patterns for common separators: ':', '-', '|', '='
        val pattern = Pattern.compile("[:\\-|\\=]")

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            val matcher = pattern.matcher(trimmedLine)
            if (matcher.find()) {
                val index = matcher.start()
                val word = trimmedLine.substring(0, index).trim()
                val meaning = trimmedLine.substring(index + 1).trim()

                if (word.isNotEmpty() && meaning.isNotEmpty()) {
                    results.add(ParsedWord(word, meaning))
                }
            } else {
                // Fallback: Treat the line as a word with an empty meaning to help with OCR debugging
                results.add(ParsedWord(trimmedLine, ""))
            }
        }
        return results
    }
}
