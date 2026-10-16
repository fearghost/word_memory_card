package com.example.wordmemory.parser

/**
 * Cleans OCR text and tokenizes it into English and Korean pools.
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

    // Patterns for labels to strip during tokenization
    private val labelPatterns = listOf(
        Regex("(?i)word[:\\s]*"),
        Regex("(?i)meaning[:\\s]*")
    )

    /**
     * Removes identified noise patterns from the raw text.
     */
    fun clean(rawText: String): String {
        var cleaned = rawText
        for (pattern in noisePatterns) {
            cleaned = cleaned.replace(pattern, "")
        }
        return cleaned.trim()
    }

    /**
     * Splits the cleaned text into two pools: English words and Korean meanings.
     * 
     * @param cleanedText The text after noise removal.
     * @return A Pair containing (List of English tokens, List of Korean tokens).
     */
    fun tokenize(cleanedText: String): Pair<List<String>, List<String>> {
        val englishTokens = mutableListOf<String>()
        val koreanTokens = mutableListOf<String>()

        val lines = cleanedText.lines()

        for (line in lines) {
            var trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            // Check if this line contains a label
            val hasWordLabel = trimmed.contains("Word", ignoreCase = true)
            val hasMeaningLabel = trimmed.contains("Meaning", ignoreCase = true)

            if (hasWordLabel || hasMeaningLabel) {
                // Strip labels (Word/Meaning) from the line
                for (pattern in labelPatterns) {
                    trimmed = trimmed.replace(pattern, "").trim()
                }

                if (trimmed.isEmpty()) continue

                val hasHangul = hangulRegex.find(trimmed) != null
                
                if (hasHangul) {
                    koreanTokens.add(trimmed)
                } else {
                    // Only add to words if it's not empty and has a reasonable length
                    if (trimmed.length >= 2) {
                        englishTokens.add(trimmed)
                    }
                }
            }
            // If no label is present, the line is treated as noise and skipped.
        }

        return Pair(englishTokens, koreanTokens)
    }
}
