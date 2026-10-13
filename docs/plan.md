# English Word Memorization App (OCR-based Flashcards) - Development Plan

## 1. Project Overview
An Android application designed to facilitate English vocabulary learning by extracting words and meanings from images (OCR), organizing them into manageable "bundles," and providing an interactive flashcard study interface with multi-accent audio support.

## 2. Core Features
- **Multi-Image OCR Processing:** Capability to select and process multiple photos simultaneously to extract text.
- **Smart Parsing:** Logic to identify English words and their corresponding meanings from raw OCR output.
- **Word Bundle Management:** Save extracted words into named collections (e.g., "Travel Vocabulary", "Business English").
- **Interactive Flashcards:**
    - **Front side:** English spelling.
    - **Back side:** Meaning/Definition.
    - **Flip animation:** Smooth transition between front and back.
- **Multi-Accent Audio (TTS):** Buttons to listen to word pronunciations in US (American), UK (British), and AU (Australian) accents.

## 3. Technical Stack (Proposed)
- **Platform:** Android (Kotlin)
- **UI Framework:** Jetpack Compose (for modern, reactive UI)
- **OCR Engine:** Google ML Kit (Text Recognition API) - optimized for mobile performance and accuracy.
- **Database:** Room Persistence Library (SQLite) - to manage word bundles and individual word entries.
- **Text-to-Speech (TTS):** Android `TextToSpeech` engine.
    - *Note:* Accent availability depends on the device's installed TTS voices.
- **Image Handling:** Coil or Glide for efficient image loading.
- **Architecture:** MVVM (Model-View-ViewModel) to ensure separation of concerns and testability.

## 4. Development Roadmap

### Phase 1: Foundation & Setup
- [ ] Set up Android Studio project with Kotlin and Jetpack Compose.
- [ ] Define Room database schema (Entity: `WordBundle`, Entity: `WordItem`).
- [ ] Implement basic navigation (Home, Bundle List, Study Mode, Add New).

### Phase 2: OCR & Parsing Logic
- [ ] Integrate Google ML Kit Text Recognition.
- [ ] Implement multi-image selection from Gallery/Camera.
- [ ] Develop the "Parser" module:
    - Extract text from OCR blocks.
    - Use regex or NLP-based logic to separate words from definitions.
    - (Optional) Integrate a lightweight dictionary API or LLM if parsing raw OCR is too complex.

### Phase 3: Data Management
- [ ] Implement Bundle creation (Naming functionality).
- [ ] Implement saving parsed words into specific bundles.
- [ ] Implement CRUD operations for bundles.

### Phase 4: Flashcard & Audio UI
- [ ] Build the Flashcard component with flip animation.
- [ ] Integrate TTS engine.
- [ ] Implement accent selection logic (filtering available `Locale` for US, UK, AU).
- [ ] Create the "Study Mode" interface.

### Phase 5: Refinement & APK Generation
- [ ] UX/UI polishing (animations, transitions).
- [ ] Error handling (OCR failures, missing voices).
- [ ] Build and test the APK.

## 5. Challenges & Mitigations
- **OCR Accuracy:** OCR from photos can be messy. *Mitigation:* Implement a "Review/Edit" step after OCR so users can correct parsed words/meanings before saving.
- **Accent Availability:** Not all Android devices have AU or UK voices installed by default. *Mitigation:* Provide a fallback to the default system voice and a guide/notification for users to install additional voices in system settings.
- **Parsing Complexity:** Definitions aren't always clearly separated from words in OCR. *Mitigation:* Use an LLM-based approach (like Gemini API) or a structured dictionary lookup to ensure high-quality word-meaning pairs.
