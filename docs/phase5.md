# Phase 5: CI/CD & Release - Detailed Plan

## 1. Objective
Automate the build, testing, and distribution process using GitHub Actions to ensure code quality and seamless releases.

## 2. Tasks

### 2.1 CI (Continuous Integration) Setup
- [ ] Create `.github/workflows/ci.yml`.
- [ ] **Trigger:** On `push` to `main` and all `pull_request` targets.
- [ ] **Workflow Steps:**
    1. Checkout code.
    2. Set up Java/Android SDK.
    3. Run `./gradlew test` (Execute all Unit Tests).
    4. Run `./gradlew lint` (Check for code quality/style issues).
    5. (Optional) Run `./gradlew detekt` if using Detekt for static analysis.

### 2.2 CD (Continuous Delivery/Deployment) Setup
- [ ] Create `.github/workflows/release.yml`.
- [ ] **Trigger:** On `push` to `tags` (e.g., `v1.0.0`).
- [ ] **Workflow Steps:**
    1. Checkout code.
    2. Set up Java/Android SDK.
    3. Run `./gradlew assembleRelease` (Build the production APK/AAB).
    4. Use `softprops/action-gh-release` to upload the resulting `.apk` or `.aab` to a GitHub Release.

### 2.3 Final Polishing & Optimization
- [ ] Perform a final code audit and fix any linting warnings.
- [ ] Optimize app size (ProGuard/R8 configuration).
- [ ] Conduct final manual testing on a real device/emulator.

## 3. Testing Strategy
- [ ] **Workflow Testing:** Run the CI workflow on a dummy PR to verify that the environment and commands are correctly configured.
- [ ] **Manual QA:** Verify that the GitHub Release contains the correct, signed artifacts.

## 4. Deliverables
- A fully automated pipeline where every code change is tested, and every tagged version results in a downloadable release artifact.
