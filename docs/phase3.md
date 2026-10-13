# Phase 3: Data Management - Detailed Plan

## 1. Objective
Enable users to organize extracted words into named collections (bundles) and manage their lifecycle (create, save, delete).

## 2. Tasks

### 3.1 Bundle & Word Management UI
- [ ] **Bundle Creation Screen:** A simple input field to name a new bundle.
- [ ] **Bundle List Screen (Home):**
    - Display all existing bundles in a grid or list.
    - Show metadata (e.g., number of words in each bundle).
    - Options to delete a bundle (with confirmation dialog).
- [ ] **Word Addition Workflow:**
    - After OCR/Parsing is confirmed, allow the user to "Assign to Bundle".
    - Provide a list of existing bundles to select from, or "Create New Bundle" on the fly.

### 3.2 Data Persistence Optimization
- [ ] Implement the "Batch Save" feature: saving hundreds of words at once into the Room database efficiently using a single transaction.
- [ ] Implement a "Delete Bundle" cascade: ensuring that when a bundle is deleted, all associated `WordItem` entries are also removed (using Room's `onDelete = CASCADE`).

### 3.3 Search & Filter (Optional/Stretch)
- [ ] Add a search bar to the Bundle List to find specific bundles by name.

## 3. Testing Strategy
- [ ] **Integration Tests:** Verify that saving a bundle and its words correctly reflects in the UI.
- [ ] **Unit Tests:** Test the `WordRepository`'s ability to handle batch inserts and deletions.

## 4. Deliverables
- A fully functional management system where users can create named bundles, add parsed words to them, and view/delete their collections.
