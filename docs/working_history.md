

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
