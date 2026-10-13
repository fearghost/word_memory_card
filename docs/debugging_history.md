# Debugging History

## [2026-10-13 16:56:29]
- Issue: GitHub Action error: `chmod: cannot access 'gradlew': No such file or directory`
- Fix: Restored missing `gradlew` script and `gradle/wrapper/` files (jar and properties) to the repository.

## [2026-10-13 18:20:53]
- Issue: Gradle CI/CD error: `java.lang.ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain`
- Fix: Modified `gradlew` script to include the wrapper JAR in the classpath using the `-cp` flag instead of just a system property.

## [2026-10-13 18:26:49]
- Issue: GitHub Action error: `Plugin [id: 'com.android.application', version: '8.2.0', apply: false] was not found`
- Fix: Added `pluginManagement` and `dependencyResolutionManagement` blocks to `settings.gradle.kts` to include `google()` and `mavenCentral()` repositories.

## [2026-10-13 18:34:06]
- Issue: Build failure: `checkDebugAarMetadata` failed because `android.useAndroidX` was not enabled.
- Fix: Created `gradle.properties` and set `android.useAndroidX=true` and `android.enableJetifier=true`.

## [2026-10-13 19:05:51]
- Issue: Build failure: `checkDebugAarMetadata` - `mipmap/ic_launcher` not found and `android:style/Theme.Material3.DayNight.NoActionBar` not found.
- Fix: Corrected theme namespace in AndroidManifest.xml and created missing resource files (strings.xml, colors.xml, ic_launcher.xml).

## [2026-10-13 19:56:11]
- Issue: Build failure: `Theme.Material3.DayNight.NoActionBar` not found in resources.
- Fix: Added `com.google.android.material:material` library to `app/build.gradle.kts` to provide the required theme.

## [2026-10-13 20:24:57]
- Issue: Massive compilation failure: Unresolved references in Repository, ML Kit, and various UI components.
- Fix: 1. Added ML Kit Text Recognition dependency.
2. Fixed premature class closure in WordRepository.kt.
3. Fixed illegal escape sequence in TextParser.kt regex.
