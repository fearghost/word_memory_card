

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
