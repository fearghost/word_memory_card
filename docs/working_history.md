

## [2026-10-14] Refactored dependencies to explicit versions and investigated CI error mismatch
- Replaced complex `dependencySubstitution` and `exclude` rules with explicit version declarations in `app/build.gradle.kts` to resolve artifact name corruption.
- Identified that CI is still reporting `p0` parameter errors, suggesting GMS version is still being prioritized or cached incorrectly.
