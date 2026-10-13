

## [2026-10-14] Unresolved references in CI (ML Kit & Material 3)
- Issue: Compilation failed due to missing classes despite correct imports.
- Cause: `app/build.gradle.kts` had a `repositories` block which conflicted with `settings.gradle.kts`'s `FAIL_ON_PROJECT_REPOS` mode.
- Fix: Removed the local `repositories` block in `app/build.gradle.kts` and simplified `MLKitOCRManager.kt` to use `TextRecognition.getClient()`.

## [2026-10-14] CI Compilation Failure (Unresolved references & parameter mismatch)
- Issue: 'Unresolved reference' for Material 3 and ML Kit, and 'No value passed for parameter p0' for TextRecognition.
- Cause: Conflicting repository configuration in app/build.gradle.kts and mismatch in ML Kit library version.
- Fix: Removed conflicting repositories block, switched to GMS ML Kit dependency, and explicitly versioned Material 3.
