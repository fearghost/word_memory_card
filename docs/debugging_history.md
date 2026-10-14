

## [2026-10-14] Dependency artifact name mismatch error
- Issue: `Could not find play-services-mlkit-text-recognition-16.0.0.aar (com.google.mlkit:text-recognition:16.0.0)` error.
- Cause: `dependencySubstitution` or `exclude` rules were corrupting the artifact identity in the CI environment.
- Action: Removed all `configurations.all` rules to let Gradle resolve dependencies naturally.
