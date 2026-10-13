

## [2026-10-14] Unresolved references in CI (ML Kit & Material 3)
- Issue: Compilation failed due to missing classes despite correct imports.
- Cause: `app/build.gradle.kts` had a `repositories` block which conflicted with `settings.gradle.kts`'s `FAIL_ON_PROJECT_REPOS` mode.
- Fix: Removed the local `repositories` block in `app/build.gradle.kts` and simplified `MLKitOCRManager.kt` to use `TextRecognition.getClient()`.
