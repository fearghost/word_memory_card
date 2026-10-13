# Debugging History

## [2026-10-13 16:56:29]
- Issue: GitHub Action error: `chmod: cannot access 'gradlew': No such file or directory`
- Fix: Restored missing `gradlew` script and `gradle/wrapper/` files (jar and properties) to the repository.

## [2026-10-13 18:20:53]
- Issue: Gradle CI/CD error: `java.lang.ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain`
- Fix: Modified `gradlew` script to include the wrapper JAR in the classpath using the `-cp` flag instead of just a system property.
