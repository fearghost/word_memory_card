

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
## [2026-10-16]
Resolved JVM target mismatch in app/build.gradle.kts by upgrading compileOptions and kotlinOptions to target Java 11.
## [2026-10-16]
Refactored TextParser to be a pure Kotlin class by injecting DictionaryRepository and CloudLLMMatcher, removing Android Context dependency for unit testing.
## [2026-10-16]
Resolved Unresolved Reference errors in MainActivity.kt by adding missing imports for DictionaryRepository, CloudLLMMatcher, and GeminiCloudLLMClient.
## [2026-10-16]
Added dictionaryDao() to AppDatabase to resolve Unresolved reference in MainActivity.kt.
## [2026-10-16]
Added DictionaryEntity to AppDatabase entities list to resolve Room KSP compilation error.
## [2026-10-16] Fixed UI build warnings and unit test failures in TextParser
- Fixed Deprecated `VolumeUp` icon by using `Icons.AutoMirrored.Filled.VolumeUp`.
- Removed unused parameters in `HomeScreen`, `ReviewEditScreen`, and `ParsingScreen`.
- Fixed `TextParserTest` failures by implementing "ignore until first label" logic in `TextCleaner.tokenize` and properly configuring `coEvery` mocks for `DictionaryRepository` and `CloudLLMMatcher`.

## [2026-10-16] Fixed AppNavigation.kt call sites to match updated screen signatures
- Corrected HomeScreen call in AppNavigation.kt by removing the unused viewModel parameter.
- Corrected ReviewEditScreen call in AppNavigation.kt by removing the unused bundleId parameter.

## [2026-10-16] Fixed TextParser unit test failure (noise filtering)
- Resolved `test noise filtering with labels` failure in `TextParserTest.kt`.
- Fixed `TextCleaner.tokenize` to only process lines that explicitly contain "Word" or "Meaning" labels, preventing noise (like "Page 2") from being incorrectly identified as English words.

## [2026-10-16] Secure Gemini API Key Integration Plan
- **Problem:** API key is hardcoded as `"YOUR_GEMINI_API_KEY"` in `MainActivity.kt`, causing silent failures in Cloud Matcher.
- **Strategy:** Implement GitHub Secrets $
ightarrow$ Gradle $
ightarrow$ BuildConfig injection to keep the key out of the source code.
- **Next Steps:**
    1. Update `app/build.gradle.kts` to inject `GEMINI_API_KEY` into `BuildConfig`.
    2. Update `app/src/main/java/com/example/wordmemory/ui/MainActivity.kt` to use `BuildConfig.GEMINI_API_KEY`.
    3. Update `.github/workflows/*.yml` to pass the `GEMINI_API_KEY` from GitHub Secrets to the Gradle build process.

## [2026-10-16] Secure Gemini API Key Integration
- Implemented secure Gemini API Key integration using GitHub Secrets, BuildConfig, and CI/CD injection.
- Updated app/build.gradle.kts to read GEMINI_API_KEY from environment variables.
- Updated MainActivity.kt to use BuildConfig.GEMINI_API_KEY.
- Updated .github/workflows/android_ci.yml to pass GEMINI_API_KEY from GitHub Secrets.


## [2026-10-16] Fixed TextParserTest.kt to match the updated TextParser logic, resolving AssertionError in CI.

## [2026-10-16] Fixed TextParserTest.kt to match the updated TextParser logic, resolving AssertionError in CI.

## [2026-10-16] Replaced TextParserTest with a diagnostic version (no labels) to isolate the cause of persistent AssertionErrors.

## [2026-10-18] Fixed TextParserTest.kt structure and identified TextCleaner punctuation removal issue.

## [2026-10-18] Fixed TextCleaner punctuation regex, added flashcard swap button, and removed raw OCR debug UI.

## [2026-10-18] Fixed TextCleaner punctuation, added UI swap button, removed raw OCR UI, and fixed TTS accent initialization.

## [2026-10-18] Replaced 'Next Card' button with dual arrow buttons (Previous/Next) for improved navigation in StudyScreen.

## [2026-10-22] Comprehensive UI/UX redesign across all screens
- Added light/dark theme support with Material 3 color scheme in MainActivity.kt
- HomeScreen: TopAppBar, ExtendedFloatingActionButton, empty state screen, word count display per bundle, pill-shaped action buttons
- StudyScreen: Progress indicator bar, completion screen with emoji, locale chips with flag emojis, improved flashcard with larger elevation and rounded corners, speak button as circular overlay, navigation buttons with icons
- FlashcardView: Distinct front/back colors (primary/tertiary containers), labeled faces ("Word"/"Meaning"), larger bold typography
- ParsingScreen: TopAppBar, empty/loading states, bottom fixed action bar, individual word cards with speak button
- NewBundleScreen: TopAppBar with close button, icon-enhanced image picker card, extracted words list with badge count, error display in error container, create button with word count badge
- ReviewEditScreen: TopAppBar with save action, bottom fixed action bar, rounded OutlinedTextField with focus colors, empty state screen
- Added WordDao.getAllBundlesWithWordCount() query for bundle-level word count statistics
- Added WordRepository.getAllBundlesWithWordCount() to expose the new DAO method

## [2026-10-22] Fixed CI compilation errors after UI/UX redesign commit
- HomeScreen.kt: `Icons.AutoMirrored.Filled.Book` → `Icons.Default.Book` (Book icon not in AutoMirrored)
- NewBundleScreen.kt: `Icons.AutoMirrored.Filled.CloudUpload` → `Icons.Default.CloudUpload` (CloudUpload not in AutoMirrored)
- ReviewEditScreen.kt: `Icons.AutoMirrored.Filled.Save` → `Icons.Default.Save` (2 occurrences, Save not in AutoMirrored)
- ParsingScreen.kt: Added missing `import androidx.compose.foundation.clickable`
- StudyScreen.kt: Removed `FilterDefaults.border()` parameter from FilterChip (API doesn't exist in Compose BOM 2024.05.00)

## [2026-10-22] Fixed FlashcardView alpha blending for proper 3D face visibility
- Issue: Opening a bundle showed inverted Korean instead of English; flip revealed normal Korean. Swap mode had the reverse problem. Root cause: Compose's 3D transform lacks backface culling — both faces overlapped; now uses rotation-based alpha fading so only one face is dominant at any angle.
- Fix: Replaced `isFront` conditional visibility with alpha-based blending. Front face fades out as rotation passes 90°; back face fades in from 90° to 180°. Only one face is visually dominant at any rotation angle.

## [2026-10-22] Replaced app icon with flashcard design (letter A on card)
- Created `ic_launcher_foreground.xml` vector drawable — white rounded card with purple accent bar, bold "A" letter (vocabulary), and two thin lines below (word definition). Matches the flashcard/learning app theme.
- Fixed: `ic_launcher.xml` had `<foreground>` set to `@android:color/transparent` (blank). Changed to `@drawable/ic_launcher_foreground`.
- Root cause of failed renders: initial vector paths used invalid Android vector syntax (`C` with radius values instead of cubic bezier coordinates). Rewrote all paths using proper `A` (arc) commands for rounded corners.
- Added `ic_launcher_round.xml` (required for round launcher icons on API 26+).

## [2026-10-19] Resolved TTS automatic playback issues and CI build failures
- Fixed TTS automatic playback issue where the first word was being played multiple times due to a blind retry logic. Implemented a smart retry mechanism that monitors the `isSpeaking` state.
- Resolved CI compilation error in `AndroidTtsManager.kt` caused by an incorrect `speak` function signature.
- Fixed a missing `kotlinx.coroutines.delay` import in `StudyScreen.kt`.
- Verified fix via successful GitHub Actions CI build.

