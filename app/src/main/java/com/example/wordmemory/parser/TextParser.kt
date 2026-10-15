package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord

class TextParser {
    private enum class ParserState {
        SEARCHING,
        COLLECTING_WORD,
        COLLECTING_MEANING
    }

    /**
     * Extracts word-meaning pairs from raw OCR text using a strict label-based state machine.
     * 
     * Pattern: [Word Label] -> [English Content] -> [Meaning Label] -> [Korean Content] ...
     * 
     * This version is optimized to ignore noise (like page numbers or dates) that 
     * appears between the intended data rows, by requiring explicit labels for 
     * state transitions and validating content type (English vs Hangul).
     */
    fun parse(rawText: String): List<ParsedWord> {
        val words = mutableListOf<String>()
        val meanings = mutableListOf<String>()
        var currentState = ParserState.SEARCHING
        
        // Regex for Hangul (Korean)
        val hangulRegex = Regex("[\\uAC00-\\uD7A3\\u3130-\\u318F]")

        val lines = rawText.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            val lower = trimmed.lowercase()

            var contentToProcess = trimmed

            // 1. Explicit Label Transitions and Stripping
            // We check for labels and if a label is found, we update the state 
            // and strip the label part so we can process the remaining content on the same line.
            if (lower.contains("word")) {
                currentState = ParserState.COLLECTING_WORD
                contentToProcess = trimmed.replace(Regex("(?i)word[:\\s]*"), "").trim()
            } else if (lower.contains("meaning")) {
                currentState = ParserState.COLLECTING_MEANING
                contentToProcess = trimmed.replace(Regex("(?i)meaning[:\\s]*"), "").trim()
            }

            // 2. Data Collection with Type Validation
            // If the line had a label, contentToProcess now contains the data on that same line.
            // If it didn't, contentToProcess is just the original line.
            val processedTrimmed = contentToProcess.trim()
            if (processedTrimmed.isNotEmpty()) {
                when (currentState) {
                    ParserState.COLLECTING_WORD -> {
                        // Only add to words if it doesn't contain Hangul (to avoid accidental meaning capture)
                        if (hangulRegex.find(processedTrimmed) == null) {
                            words.add(processedTrimmed)
                        }
                    }
                    ParserState.COLLECTING_MEANING -> {
                        // Only add to meanings if it contains Hangul (to avoid capturing noise like "Page 2")
                        if (hangulRegex.find(processedTrimmed) != null) {
                            meanings.add(processedTrimmed)
                        }
                    }
                    ParserState.SEARCHING -> {
                        // Ignore everything until a label is found
                    }
                }
            }
        }

        // 3. Pairing the collected words and meanings
        val results = mutableListOf<ParsedWord>()
        val count = minOf(words.size, meanings.size)
        for (i in 0 until count) {
            results.add(ParsedWord(words[i], meanings[i]))
        }

        return results
    }
}
