

## [2026-10-14] Reverted to clean dependency configuration to resolve artifact mismatch
- Identified that complex resolution rules were causing artifact name corruption in CI.
- Reverted `app/build.gradle.kts` to a clean state without `configurations.all` blocks.
