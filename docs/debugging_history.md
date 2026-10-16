

## [2026-10-14] Persistent GMS ML Kit artifact mismatch in CI
- Issue: `p0` parameter error and `Unresolved reference` in CI despite explicit versioning in `app/build.gradle.kts`.
- Diagnosis: CI environment is likely still resolving the GMS version of ML Kit, possibly due to caching or shadowing in the dependency graph.
- Action: Requested full error logs from the user for deep analysis.
## [2026-10-14] ML Kit dependency resolution failure in CI
Updated com.google.mlkit:text-recognition and vision-common from 16.0.0 to 16.0.1 in app/build.gradle.kts to resolve artifact not found error.
## [2026-10-14] ML Kit AAR name mismatch error during dependency substitution
Replaced dependencySubstitution with exclude in app/build.gradle.kts. The substitution was causing a mismatch between the module name and the actual AAR filename. Using exclude ensures the GMS version is ignored while the bundled version is used.

## [2026-10-15] Unexpected +/- setting in VM option 'MaxMetaspaceSize=512m' - Removed '+' prefix.

## [2026-10-15] Integrated OCR results with Bundle creation: Modified WordViewModel to associate image URIs with extracted ParsedWords, and updated NewBundleScreen to allow image selection and automatic parsing.

## [2026-10-15] Missing imagePath in WordRepository.kt: The updateWordsInBundle function was missing the imagePath argument when creating WordItemEntity, leading to a compilation error after adding image support.

## [2026-10-15] OCR returning dummy data and empty flashcards: Found MockOCRManager being used in MainActivity and StudyScreen receiving empty list in AppNavigation. Fixed by injecting MLKitOCRManager and passing bundleId to StudyScreen to fetch data from repository.

## [2026-10-15] OCR failed on rotated images or non-standard formats: Implemented automatic rotation attempts (0, 90, 180, 270) in MLKitOCRManager and updated TextParser to return words without meanings as a fallback.

## [2026-10-15] Syntax Error (Expecting expression/closing parenthesis) in TextParser.kt: Fixed an issue where incorrect escaping of triple quotes caused a compilation failure in the TextParser regex definition.

## [2026-10-15] AssertionError in TextParserTest.kt: Fixed mismatch between TextParser's fallback behavior (including invalid lines) and the test's expectation (ignoring invalid lines).

## [2026-10-15] AssertionError in TextParserTest.kt: Fixed mismatch between TextParser's fallback behavior (including invalid lines) and the test's expectation (ignoring invalid lines).

## [2026-10-15] AssertionError in TextParserTest.kt: Fixed mismatch between TextParser's fallback behavior (including invalid lines) and the test's expectation (ignoring invalid lines).

## [2026-10-15] Korean meanings missing on flashcard back side: Implemented regex-based Hangul detection in TextParser to split English and Korean words on the same line when no explicit separator is present.

## [2026-10-15] Korean text not detected in OCR: Identified that the OCR engine was using Latin-only options; switched to CJK (Chinese/Japanese/Korean) recognition model.

## [2026-10-15] Korean meanings missing in flashcards: Identified that the OCR engine was using Latin-only options; switched to CJK (Chinese, Japanese, Korean) model to detect Hangul in table cells.

## [2026-10-15] compileDebugKotlin FAILED (Unresolved reference: DEFAULT_OPTIONS): Identified that the OCR client initialization was using incorrect/missing options for Korean. Fixed by adding text-recognition-korean dependency and using KoreanTextRecognizerOptions.Builder().build().

## [2026-10-15] Potential noise capture (e.g., page numbers) in OCR: Identified that implicit transitions could capture non-data text. Fixed by requiring explicit 'Word' and 'Meaning' labels for state changes and adding language-type validation.

## [2026-10-15] Test failure due to mismatch between new TextParser logic and old TextParserTest. Resolved by updating the test cases to use explicit labels and noise-filtering verification.

## [2026-10-15] AssertionError in TextParserTest: The parser was skipping data on the same line as the label due to the 'continue' keyword. Resolved by stripping the label and processing the remaining content in the same loop iteration.

## [2026-10-15] User reported OCR alignment/quality issues. Implemented a debug view to see unparsed text.

## [2026-10-15] User reported OCR quality and alignment issues. Implemented a raw OCR text display for debugging purposes.

## [2026-10-15] Resolved 'Unresolved reference: verticalScroll' and 'rememberScrollState' in NewBundleScreen.kt by adding necessary imports.

## [2026-10-16] User reported OCR quality and alignment issues. Implemented a raw OCR text display for debugging purposes.

## [2026-10-16] Documented the design for the Semantic Matcher to provide a roadmap for solving OCR alignment issues.

## [2026-10-16] Refactoring TextParser to be suspend-based
Issue: Changing TextParser.parse() to a suspend function broke MainActivity and TextParserTest.
Fix: Updated MainActivity to pass context/API key and updated tests with runTest and mockk.
## [2026-10-16] Dependency Error: com.google.generativeai:generativeai:0.7.0 not found
Issue: Gradle could not find the Generative AI SDK in the existing repositories.
Fix: Added mavenCentral() and jitpack to dependencyResolutionManagement in settings.gradle.kts.
## [2026-10-16] Dependency Error: com.google.generativeai:generativeai:0.7.0 not found
Issue: Version 0.7.0 was not found in Maven Central.
Fix: Updated version to 0.9.0 in app/build.gradle.kts.
## [2026-10-16] Dependency Error: Incorrect Group ID for Generative AI SDK
Issue: The dependency was using the wrong Group ID (com.google.generativeai).
Fix: Corrected the Group ID to com.google.ai.client.generativeai in app/build.gradle.kts.
## [2026-10-16] Unresolved references in GeminiCloudLLMClient.kt
Issue: Missing Kotlinx Serialization plugin and dependency in app/build.gradle.kts.
Fix: Added id("org.jetbrains.kotlin.plugin.serialization") and implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") to app/build.gradle.kts.