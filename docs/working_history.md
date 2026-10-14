

## [2026-10-14] Refactored dependencies to explicit versions and investigated CI error mismatch
- Replaced complex `dependencySubstitution` and `exclude` rules with explicit version declarations in `app/build.gradle.kts` to resolve artifact name corruption.
- Identified that CI is still reporting `p0` parameter errors, suggesting GMS version is still being prioritized or cached incorrectly.
## [2026-10-14] Updated ML Kit dependency version to 16.0.1 to fix build error
## [2026-10-14] Fixed ML Kit dependency resolution error in app/build.gradle.kts
