

## [2026-10-14] Refactored dependencies to explicit versions and investigated CI error mismatch
- Replaced complex `dependencySubstitution` and `exclude` rules with explicit version declarations in `app/build.gradle.kts` to resolve artifact name corruption.
- Identified that CI is still reporting `p0` parameter errors, suggesting GMS version is still being prioritized or cached incorrectly.
## [2026-10-14] Updated ML Kit dependency version to 16.0.1 to fix build error
## [2026-10-14] Fixed ML Kit dependency resolution error in app/build.gradle.kts

## [2026-10-15] Fixed JVM option syntax error in gradle.properties to resolve OOM during Dexing.

## [2026-10-15] Implemented photo-to-bundle feature: Users can now select multiple images, perform OCR, and create a bundle containing the extracted words with their associated image paths.

## [2026-10-15] Fixed compilation error in WordRepository: Added missing imagePath parameter in updateWordsInBundle function to support image-based flashcard creation.

## [2026-10-15] Fixed OCR and Flashcard workflow: Replaced MockOCR with MLKitOCRManager and implemented actual word loading in StudyScreen via bundleId.

## [2026-10-15] Implemented OCR auto-rotation retry logic and improved text parsing robustness: Added 0-270 degree rotation attempts in MLKitOCRManager and fallback parsing in TextParser to handle non-standard formats.

## [2026-10-15] Fixed syntax error in TextParser.kt and finalized OCR improvements: Resolved a compilation error caused by incorrect string escaping in the regex pattern and ensured rotation-robust OCR.

## [2026-10-15] Fixed unit test failure in TextParserTest.kt: Resolved an AssertionError by aligning TextParser's fallback logic with the test's expectation to ignore invalid lines (lines without separators).

## [2026-10-15] Fixed unit test failure in TextParserTest.kt: Re-introduced fallback logic in TextParser to show raw text during OCR, and updated unit tests to match this behavior.

## [2026-10-15] Fixed unit test failure in TextParserTest.kt: Re-introduced fallback logic in TextParser to show raw text during OCR, and updated unit tests to match this behavior.

## [2026-10-15] Implemented Hangul-based automatic splitting in TextParser: Enhanced OCR parsing to link English words and Korean meanings on the same line even without explicit separators.

## [2026-10-15] Implemented CJK (Chinese, Japanese, Korean) support in ML Kit OCR: Switched from Latin-only to CJK text recognition to enable Korean character detection.

## [2026-10-15] Implemented CJK (Chinese, Japanese, Korean) support in ML Kit OCR: Switched from Latin-only to CJK text recognition to enable Korean character detection in table-structured images.

## [2026-10-15] Resolved ML Kit compilation error by switching to Korean-specific OCR module: Replaced Latin-only/Chinese-based recognizer with KoreanTextRecognizerOptions to correctly support Hangul.

## [2026-10-15] Implemented strict label-based parsing in TextParser to prevent noise capture: Switched from implicit language-based transitions to explicit label-based transitions, ensuring page numbers or dates are not captured as words/meanings.

## [2026-10-15] Updated TextParser to use strict label-based parsing and synchronized TestParserTest to match the new logic. This prevents noise (like page numbers) from being captured as words/meanings.

## [2026-10-15] Fixed TextParser logic by implementing label-stripping to allow data on the same line as the label and ensuring strict state transitions. This resolves the AssertionError in unit tests and correctly filters noise like page numbers.

## [2026-10-15] Implemented Raw OCR text debugging view in NewBundleScreen to help diagnose OCR quality and alignment issues.

## [2026-10-15] Added a debug view in NewBundleScreen to display raw OCR text, enabling better diagnosis of OCR quality and parsing alignment issues.

## [2026-10-15] Fixed compilation error in NewBundleScreen.kt by adding missing foundation imports (rememberScrollState, verticalScroll).

## [2026-10-16] Implemented raw OCR text debugging view in NewBundleScreen to help diagnose alignment and recognition issues.

## [2026-10-16] Documented the Hybrid Semantic Matcher architecture in docs/architecture/semantic_matcher.md and linked it in .hermes.md.

## [2026-10-16]
Implemented the Hybrid Semantic Matcher (Local + Cloud LLM). This replaces the rule-based parsing with a tiered approach (TextCleaner -> LocalMatcher -> CloudLLMMatcher) for high-accuracy OCR word-meaning extraction.
## [2026-10-16]
Fixed Gradle dependency error by adding mavenCentral() and JitPack to settings.gradle.kts to resolve Generative AI SDK download issue.
## [2026-10-16]
Updated Generative AI SDK version to 0.9.0 in app/build.gradle.kts to resolve dependency resolution issues.
## [2026-10-16]
Fixed the incorrect Group ID for the Generative AI SDK in app/build.gradle.kts (changed from com.google.generativeai to com.google.ai.client.generativeai).
## [2026-10-16]
Fixed dependency issues in app/build.gradle.kts by adding Kotlinx Serialization plugin and dependency, and ensuring correct Generative AI SDK Group ID.
## [2026-10-16]
Finalized GeminiCloudLLMClient.kt by correcting the package imports from com.google.generativeai to com.google.ai.client.generativeai.