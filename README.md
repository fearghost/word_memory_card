# Project Overview: OCR English Flashcard App

This project aims to build an Android application that automates the creation of English vocabulary flashcards using OCR technology.

## Summary
The app will allow users to:
1. Capture or upload multiple images.
2. Use OCR to extract text.
3. Parse the text into English words and their meanings.
4. Save these into named "Word Bundles".
5. Study via interactive flashcards (Word -> Meaning flip).
6. Listen to pronunciations in US, UK, and AU accents using TTS.

## GitHub Repository
- URL: [https://github.com/fearghost/word_memory_card](https://github.com/fearghost/word_memory_card)

## CI/CD Pipeline (GitHub Actions)
To ensure high code quality and automated distribution, the project follows a professional CI/CD workflow:
- **Continuous Integration (CI):** Every pull request and push to `main` triggers an automated workflow that runs unit tests and linting to catch regressions and style violations early.
- **Continuous Delivery/Deployment (CD):** When a new version tag is created (e.g., `v1.0.0`), GitHub Actions automatically builds the production-ready APK/AAB and creates a GitHub Release, attaching the build artifacts for easy download.

## Documentation
For a detailed technical roadmap, architecture, and feature breakdown, please refer to the planning document:

[Detailed Development Plan](./doc/plan.md)
