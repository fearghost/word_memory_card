package com.example.wordmemory.parser

/**
 * Cleans OCR text and parses it into English and Korean phrase pools.
 * Instead of tokenizing into individual words, it preserves phrases 
 * by treating each line as a potential English-Korean pair.
 */
class TextCleaner {

    // Regex for Hangul (Korean)
    private val hangulRegex = Regex("[\\uAC00-\\uD7A3\\u3130-\\u318F]")

    // Patterns for common noise (case-insensitive)
    private val noisePatterns = listOf(
        Regex("(?i)vocabulary"),
        Regex("(?i)check"),
        Regex("(?i)lesson\\s*\\d+"),
        Regex("(?i)page\\s*\\d+"),
        Regex("(?i)unit\\s*\\d+"),
        Regex("(?i)number\\s*\\d+"),
    )

    // Regex for punctuation (non-alphanumeric, non-whitespace, non-hangul)
    private val punctuationRegex = Regex("[^a-zA-Z0-9\\s\\uAC00-\\uD7A3\\u3130-\\u318F]")

    /**
     * Removes identified noise patterns from the raw text.
     */
    fun clean(rawText: String): String {
        var cleaned = rawText
        for (pattern in noisePatterns) {
            cleaned = cleaned.replace(pattern, "")
        }
        cleaned = cleaned.replace(punctuationRegex, "")
        return cleaned.trim()
    }

    /**
     * Splits the cleaned text into two pools: English phrases and Korean phrases.
     * Uses a line-based approach to preserve semantic units (e.g., "take off" or "만들어 내다").
     * 
     * @param cleanedText The text after noise removal.
     * @return A Pair containing (List of English phrases, List of Korean phrases).
     */
    fun tokenize(cleanedText: String): Pair<List<String>, List<String>> {
        val englishPhrases = mutableListOf<String>()
        val koreanPhrases = mutableListOf<String>()

        // Split by lines to treat each line as a semantic unit
        val lines = cleanedText.lines()

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            // Find the first occurrence of a Hangul character to separate English and Korean
            val hangulIndex = trimmedLine.indexOfFirst { hangulRegex.matches(it.toString()) }

            if (hangulIndex != -1) {
                // Line contains both English and Korean (e.g., "take off: 이륙하다")
                val englishPart = trimmedLine.substring(0, hangulIndex).trim()
                val hangulPart = trimmedLine.substring(hangulIndex).trim()

                if (englishPart.isNotEmpty()) {
                    englishPhrases.add(englishPart)
                }
                if (hangulPart.isNotEmpty()) {
                    koreanPhrases.add(hangulPart)
                }
            } else {
                // Line contains only one language (assumed English if no Hangul is present)
                // We check length to avoid adding tiny noise fragments
                if (trimmedLine.length >= 2) {
                    englishPhrases.add(trimmedLine)
                }
            }
        }

        return Pair(englishPhrases, koreanPhrases)
    }
}
