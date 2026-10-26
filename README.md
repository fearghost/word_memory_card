# WordMemory — OCR 기반 영어 단어 플래시카드 학습 앱

![GitHub Actions Workflow Status](https://github.com/fearghost/word_memory_card/actions/workflows/android_ci.yml/badge.svg)
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)
![Platform: Android](https://img.shields.io/badge/Platform-Android-green.svg)

Android 기기에서 **사진 찍기** 또는 **갤러리 선택**을 통해 영어 단어를 추출하고, 플래시카드로 효율적으로 공부할 수 있는 앱입니다. OCR(ML Kit) → 자동 단어 파싱 → 번들 관리 → 플래시카드 학습의 전 과정을 지원합니다.

---

## 🚀 주요 기능

### 📷 OCR 기반 단어 추출
- **다중 이미지 지원** — 카메라로 촬영하거나 갤러리에서 여러 장을 동시에 선택해 텍스트 추출
- **ML Kit Text Recognition** — 영어 + 한국어(CJK) 자동 인식
- **스마트 파싱** — 추출된 원문에서 영어 단어와 뜻을 자동으로 분리 (하이브리드 시맨틱 매처 적용)

### 📚 번들 관리
- 추출한 단어를 이름 있는 **번들**(예: "여행 영어", "비즈니스 영단어")로 그룹화
- 각 번들의 단어 수, 학습 진행도 표시
- 편집 모드에서 단어/뜻 직접 수정 또는 추가

### 🃏 3단계 학습 시스템

| 탭 | 기능 |
|----|------|
| **List** | 번들 내 전체 단어 목록. TTS 발음 듣기(스피크 버튼), ⭐ 어려움 표시, "어려운 단어만 보기" 필터 |
| **Study** | 플래시카드 학습 — 좌우 스와이프 네비게이션, 3D 플립 애니메이션, A-/A+ 폰트 크기 조절, 자동 TTS 재생 |
| **Test** | 영어 입력 테스트 — 한국어 의미 표시 → 직접 타이핑 → 모든 단어 완료 후 결과 페이지(정답/오답 색상 구분) |

### ✨ 학습 편의 기능
- **⭐ 어려움 표시** — 외우기 어려운 단어에 별표 → "Only Difficult" 필터로 집중复习
- **🔊 TTS 발음** — 미국 영어(Locale.US) 자동 재생, 플래시카드에서 스피크 버튼으로 수동 재생
- **📐 폰트 크기 조절** — A-/A+ 버튼으로 S/M/L 3단계 크기 조정
- **🔄 3D 카드 플립** — 부드러운 회전 애니메이션으로 단어 ↔ 뜻 전환

---

## 📱 앱 사용 방법

### 1. 새 번들 만들기 (OCR로 단어 추출)
```
홈 화면(번들 목록) → 우측 하단 "+" 버튼 → 이미지 선택(카메라/갤러리) → OCR 실행 → 단어 파싱 확인 → 번들 저장
```

1. 홈 화면에서 **"+"**(Create Bundle) 버튼 탭
2. 카메라 촬영 또는 갤러리에서 이미지 선택 (다중 선택 가능)
3. OCR가 자동으로 텍스트 인식 → 파싱된 단어 목록 표시
4. 단어/뜻 확인 후 **"Create"** 버튼으로 번들 저장

### 2. 플래시카드 학습 (Study 탭)
```
번들 선택 → Study 탭 진입 → 카드 좌우 스와이프 또는 페이지 indikator 탭 → 카드 터치로 플립 → A-/A+로 폰트 조절 → Speak 버튼으로 발음 듣기
```

- **스와이프**: 좌우 스와이프로 다음/이전 단어 이동
- **플립**: 카드 터치로 영어 ↔ 한국어 의미 전환
- **폰트 조절**: 상단 **A-** / **A+** 버튼으로 글자 크기 변경 (S/M/L)
- **발음 듣기**: 카드 우측 하단 🔊 버튼으로 TTS 발음 재생
- **어려움 표시**: 카드 우측 상단 ⭐ 버튼으로 difficult 마킹

### 3. 단어 목록 확인 (List 탭)
```
번들 선택 → List 탭 진입 → 단어 목록 스크롤 → Speak 버튼으로 발음 → ⭐로 어려움 표시 → "Only Difficult" 필터 적용
```

- 전체 단어 목록을 **LazyColumn**으로 스크롤
- 각 단어 옆 **🔊** 버튼으로 TTS 발음 듣기
- 각 단어 옆 **⭐** 버튼으로 어려움 마킹
- 상단 **"Only Difficult"** 칩을 탭하면 difficult 표시된 단어만 필터

### 4. 테스트 모드 (Test 탭)
```
번들 선택 → Test 탭 진입 → 한국어 의미 확인 → 영어 단어로 직접 입력 → Next로 다음 문제 → 전체 완료 후 결과 확인
```

- 한국어 의미가 표시되면 **영어 단어를 직접 타이핑**
- **Next** 버튼으로 다음 문제로 진행 (자동 다음 이동)
- 모든 단어 완료 시 **결과 페이지** 표시:
  - 🟢 정답: 보라색 배경에 정답 표시
  - 🔴 오답: 빨간색 배경에 오답 + 내 답안 표시

---

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| **플랫폼** | Android (API 26+, Kotlin) |
| **UI** | Jetpack Compose, Material 3 |
| **OCR** | Google ML Kit (Text Recognition — CJK) |
| **DB** | Room Persistence Library (SQLite) |
| **TTS** | Android `TextToSpeech` (Locale.US) |
| **이미지** | Coil |
| **AI 파싱** | Google Gemini API (하이브리드 시맨틱 매처: TextCleaner + LocalMatcher + CloudLLMMatcher) |

---

## 🏗 아키텍처

```
┌─────────────────────────────────────────────┐
│  UI Layer (Compose Screens)                 │
│  ├─ HomeScreen      (번들 목록)              │
│  ├─ StudyScreen     (List/Study/Test 탭)     │
│  ├─ NewBundleScreen (OCR + 파싱)             │
│  └─ ReviewEditScreen (단어 편집)             │
├─────────────────────────────────────────────┤
│  ViewModel Layer                              │
│  ├─ BundleViewModel   (번들 CRUD)            │
│  └─ WordViewModel     (단어 학습 상태 관리)    │
├─────────────────────────────────────────────┤
│  Repository Layer                             │
│  ├─ BundleRepository                          │
│  └─ WordRepository    (Flow 기반 실시간 동기화)│
├─────────────────────────────────────────────┤
│  Data Layer                                   │
│  ├─ Room DAO (WordDao, BundleDao)             │
│  ├─ MLKitOCRManager     (OCR 인식)            │
│  ├─ TextParser / HybridSemanticMatcher (파싱) │
│  └─ AndroidTtsManager     (TTS 발음)          │
└─────────────────────────────────────────────┘
```

---

## 🔨 빌드 및 CI/CD

GitHub Actions를 통해 모든 push에서 자동으로 빌드가 실행됩니다.

| 유형 | 파일명 | 설명 |
|------|--------|------|
| Debug | `WordMemoryDbg.apk` | 테스트용 빌드 |
| Release | `WordMemory.apk` | 서명 포함된 배포용 빌드 |

Release APK는 새 버전 태그(`vX.Y.Z`) push 시 [GitHub Releases](https://github.com/fearghost/word_memory_card/releases)에 자동 업로드됩니다.

---

## 📥 설치

1. [Actions 페이지](https://github.com/fearghost/word_memory_card/actions)에서 최신 빌드 다운로드
2. 또는 [Releases 페이지](https://github.com/fearghost/word_memory_card/releases)에서 APK 다운로드
3. 기기에 APK 설치 (Unknown Sources 허용 필요)

---

## 📄 라이선스

MIT License
