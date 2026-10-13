# OCR English Flashcard App

An Android application designed to automate the creation of English vocabulary flashcards using OCR technology.

## 🚀 Overview

The app allows users to capture or upload images to extract text using OCR, parse that text into English words and their meanings, and organize them into manageable **"Word Bundles"**. Users can then study these words via interactive flashcards with support for various English accents (US, UK, AU) through Text-to-Speech (TTS).

## ✨ Core Features

- **Multi-Image OCR Processing:** Capture or upload multiple photos simultaneously for text extraction.
- **Smart Parsing:** Automatically identifies English words and their corresponding meanings from raw OCR output.
- **Word Bundle Management:** Organize extracted words into named collections (e.g., "Travel Vocabulary", "Business English").
- **Interactive Flashcards:** Study via a smooth, animated flip-card interface (Word $\leftrightarrow$ Meaning).
- **Multi-Accent Audio (TTS):** Listen to pronunciations in US, UK, and AU accents to enhance learning.

## 🛠️ Technical Stack

- **Platform:** Android (Kotlin)
- **UI Framework:** Jetpack Compose
- **OCR Engine:** Google ML Kit (Text Recognition API)
- **Database:** Room Persistence Library (SQLite)
- **Text-to-Speech:** Android `TextToSpeech` engine
- **Image Handling:** Coil / Glide

## 📦 Development & CI/CD

This project follows a professional CI/CD workflow via **GitHub Actions**:

- **Continuous Integration (CI):** Automated unit tests and linting run on every pull request and push to `main`.
- **Continuous Delivery (CD):** Production-ready APK/AAB builds and GitHub Releases are automatically generated upon new version tags (e.g., `v1.0.0`).

## 📖 Documentation

For a detailed technical roadmap, architecture, and feature breakdown, please refer to the development plan:

[Detailed Development Plan](./docs/plan.md)

---
**GitHub Repository:** [word_memory_card](https://github.com/fearghost/word_memory_card)
