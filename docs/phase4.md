# Phase 4: Flashcard & Audio UI - Detailed Plan

## 1. Objective
Create the primary user experience: an interactive, engaging study mode with high-quality audio feedback.

## 2. Tasks

### 4.1 Flashcard Component (The "Core" Experience)
- [ ] **UI Design (Jetpack Compose):**
    - Create a `Flashcard` composable with a card-like appearance.
    - Implement a **Flip Animation** using `graphicsLayer` (rotation around the Y-axis).
- [ ] **Front Side:** Display the English word in large, clear typography.
- [ ] **Back Side:** Display the meaning/definition.
- [ ] **Navigation in Study Mode:**
    - "Next" and "Previous" buttons.
    - Swipe gestures (Left/Right) to move between cards.

### 4.2 Text-to-Speech (TTS) Integration
- [ ] **Engine Setup:** Initialize Android `TextToSpeech` engine within a ViewModel.
- [ ] **Accent Implementation:**
    - Implement a mechanism to query available `Locale`s from the system.
    - Create three specific buttons: `[US]`, `[UK]`, `[AU]`.
    - **Mapping Logic:** 
        - `en-US` $\rightarrow$ American
        - `en-GB` $\rightarrow$ British
        - `en-AU` $\rightarrow$ Australian (Fallback to `en-GB` or `en-US` if `en-AU` is not installed).
- [ ] **UI/UX for Audio:** Add a "speaker" icon or individual accent buttons on the card to trigger playback.

### 4.3 Study Mode Logic
- [ ] Implement "Shuffle" mode to randomize the order of words in a bundle.
- [ ] (Optional) Implement "Review Mode" for words the user found difficult.

## 3. Testing Strategy
- [ ] **UI Tests (Compose Testing Library):** Verify that tapping the card triggers the flip animation.
- [ ] **Integration Tests:** Ensure the TTS engine correctly receives the text and the selected locale.

## 4. Deliverables
- A polished, interactive study interface where users can flip cards to learn and listen to various English accents.
