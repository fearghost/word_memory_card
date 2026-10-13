# Phase 2: OCR & Parsing Logic - Detailed Plan

## 1. Objective
Implement the core technology: extracting text from images and intelligently converting that text into structured word-meaning pairs.

## 2. Tasks

### 2.1 OCR Integration
- [ ] Integrate **Google ML Kit Text Recognition API**.
- [ ] Implement `ImageCapture` and `ImagePicker` functionality using CameraX or Android's MediaStore API.
- [ ] Create an `OcrService` that:
    - Takes a URI (image source).
    - Converts it to an `InputImage`.
    - Performs text recognition.
    - Returns a raw `String` of all detected text.

### 2.2 Intelligent Parsing Engine
- [ ] Develop the `TextParser` module.
- [ ] **Strategy A (Regex/Pattern Based):** Use regex to identify common patterns in OCR output (e.g., "Word: Meaning" or "Word - Meaning").
- [ ] **Strategy B (NLP/Heuristic):** If regex fails, use a more advanced approach:
    - Split text by lines/blocks.
    - Identify English words using a dictionary check or language detection.
    - Attempt to pair a word with the text immediately following it.
- [ ] **Review Step (Crucial):** Implement a UI screen that shows the raw OCR results and allows the user to manually edit/confirm the parsed pairs before they are saved.

### 2.3 Multi-Image Workflow
- [ ] Implement a "batch selection" UI where users can select multiple images from the gallery.
- [ ] Create a background processing queue (using Coroutines) to process images one by one to avoid memory spikes.

## 3. Testing Strategy
- [ ] **Unit Tests:** Create a suite of "Mock OCR Text" (messy, formatted, and unformatted) and verify that the `TextParser` correctly extracts `WordItem` objects.
- [ ] **Integration Tests:** Test the flow from `Image URI` $\rightarrow$ `ML Kit` $\rightarrow$ `Parser`.

## 4. Deliverables
- Ability to pick multiple photos and see a list of parsed English words and meanings ready for confirmation.
