# Hybrid Semantic Matcher Architecture

## Overview
The `Hybrid Semantic Matcher` is a specialized parsing engine designed to extract English-Korean word pairs from noisy OCR text. Unlike traditional rule-based parsers, it focuses on **semantic relevance** rather than strict sequential ordering.

## Problem Statement
OCR output often lacks clear "Word:" or "Meaning:" labels and frequently contains:
- Misaligned pairs (word and meaning are on different lines or out of order).
- Significant noise (page numbers, headers, footers, instructional text).
- OCR typos (e.g., `l` instead of `1`).

## Core Architecture

### 1. Pipeline Flow
`Raw OCR Text` $\rightarrow$ **[Cleaner]** $\rightarrow$ `Candidate Tokens` $\rightarrow$ **[Matcher]** $\rightarrow$ `WordPair List` $\rightarrow$ **[Sorter]** $\rightarrow$ `Sorted WordPair List`

### 2. Component Details

#### **A. `TextCleaner` (Preprocessing)**
- **Noise Removal**: Uses regex to strip out non-semantic elements (e.g., `Vocabulary`, `Check`, `Lesson X`).
- **Typo Correction**: Applies heuristic-based corrections for common OCR errors (e.g., `1` $\leftrightarrow$ `l`, `0` $\leftrightarrow$ `O`).
- **Tokenization**: Splits the cleaned text into language-specific candidate pools.

#### **B. `SemanticMatcher` (The Core Engine)**
Uses a dual-strategy approach to balance speed and intelligence.
- **Strategy 1: `LocalDictionaryMatcher` (Fast/Offline)**
    - Queries a local SQLite/Room database containing high-frequency English-Korean pairs.
    - Returns matches with high confidence scores.
- **Strategy 2: `CloudLLMMatcher` (Smart/Online)**
    - Triggered when local matches fail or confidence is low.
    - Sends text snippets to a Cloud LLM (e.g., Gemini API).
    - Uses the LLM's semantic knowledge to identify pairs even in highly corrupted text.

#### **C. `PairingEngine` (Integration & Verification)**
- **Deduplication**: Ensures no duplicate word pairs are created.
- **Confidence Filtering**: Prioritizes Cloud-verified matches over local matches in case of conflicts.

#### **D. `ResultSorter` (Finalization)**
- Sorts the final `List<WordPair>` alphabetically by the English word.

## Implementation Interface (Kotlin Concept)

```kotlin
data class WordPair(val word: String, val meaning: String)

interface SemanticMatcher {
    /**
     * Processes raw OCR text and returns a list of matched word-meaning pairs.
     */
    suspend fun match(rawText: String): List<WordPair>
}

class HybridSemanticMatcher(
    private val localDict: LocalDictionary,
    private val cloudApi: CloudLLMClient
) : SemanticMatcher {
    override suspend fun match(rawText: String): List<WordPair> {
        // Implementation logic following the pipeline
    }
}
```
