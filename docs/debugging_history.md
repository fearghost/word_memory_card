

## [2026-10-14] Persistent GMS ML Kit artifact mismatch in CI
- Issue: `p0` parameter error and `Unresolved reference` in CI despite explicit versioning in `app/build.gradle.kts`.
- Diagnosis: CI environment is likely still resolving the GMS version of ML Kit, possibly due to caching or shadowing in the dependency graph.
- Action: Requested full error logs from the user for deep analysis.
## [2026-10-14] ML Kit dependency resolution failure in CI
Updated com.google.mlkit:text-recognition and vision-common from 16.0.0 to 16.0.1 in app/build.gradle.kts to resolve artifact not found error.
