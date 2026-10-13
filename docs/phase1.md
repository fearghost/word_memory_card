# Phase 1: Foundation & Setup - Detailed Plan

## 1. Objective
Establish the core Android development environment, project architecture, and data persistence layer.

## 2. Tasks

### 1.1 Project Initialization
- [ ] Create a new Android project using **Jetpack Compose**.
- [ ] Configure `build.gradle` (Project & App levels):
    - Add Kotlin serialization.
    - Add Compose dependencies.
    - Add Room, Hilt (for Dependency Injection), and Lifecycle dependencies.
- [ ] Set up basic folder structure following Clean Architecture/MVVM (data, domain, ui, di).

### 1.2 Data Layer Implementation (Room)
- [ ] Define `WordBundle` Entity:
    - `id` (Primary Key)
    - `name` (String)
    - `createdAt` (Long)
- [ ] Define `WordItem` Entity:
    - `id` (Primary Key)
    - `bundleId` (Foreign Key to `WordBundle`)
    - `english` (String)
    - `meaning` (String)
- [ ] Create DAOs (Data Access Objects):
    - `BundleDao`: insert, delete, getAllBundles, getBundleById.
    - `WordDao`: insert, delete, getWordsByBundleId.
- [ ] Implement `RoomDatabase` class and provide it via Hilt.

### 1.3 Repository & DI Setup
- [ ] Implement `WordRepository` interface and its implementation.
- [ ] Set up Hilt `@Module` to provide `RoomDatabase`, `Dao`s, and `Repository`.

### 1.4 Basic Navigation
- [ ] Implement `NavHost` using Compose Navigation.
- [ ] Define routes: `home`, `bundle_list`, `study_mode`, `add_bundle`.

## 3. Testing Strategy
- [ ] **Unit Tests:** Test Room DAO operations (insert/query) using an in-memory database.
- [ ] **Unit Tests:** Test Repository logic for data flow.

## 4. Deliverables
- A running "Hello World" Android app with navigation and a working local database.
