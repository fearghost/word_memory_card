# Bundle Renewal Plan

## 개요
Bundle 진입 후의 StudyScreen을 전면 개편한다. 현재 1개의 탭 + 버튼 네비게이션 구조에서, 3개 탭 + 제스처 기반 카드 전환으로 변경하며 TTS/폰트/카드 UI도 개선한다.

---

## 1. TTS Locale 고정 (US Only)

### Current State
- `StudyScreen.kt:230-254`: FilterChip 3개 (🇺🇸 US, 🇬🇧 UK, 🇦🇺 AU) 표시
- `selectedLocale` StateFlow로 관리, 변경 시 `ttsManager.setAccent()` 호출

### Change Plan
- Locale 선택 FilterChip 제거
- `selectedLocale`를 `Locale.US`로 고정
- `LaunchedEffect(selectedLocale)` 제거 (초기 설정만 필요)
- TTS 초기화 시 US만 설정

### Files
- `StudyScreen.kt`: locale chip row 삭제, `selectedLocale` 제거

---

## 2. Bug Fix: TTS 자동 재생 버그

### Root Cause 분석
`StudyScreen.kt:62-87`의 `LaunchedEffect(isTtsReady, words, currentIndex, isRotated, isSwapped)`:
- `isSpeaking` 변수가 함수 내에서 `remember { mutableStateOf(false) }`로 선언됨
- 하지만 `isSpeaking`은 **StudyScreen의 로컬 상태**이고, TTS 엔진의 실제 재생 상태를 추적하지 못함
- `AndroidTtsManager.isSpeaking` (StateFlow)와 `StudyScreen.isSpeaking` (Boolean)이 **둘 다 존재**하지만 동기화 안 됨
- `while` 루프에서 `isSpeaking`을 확인하는 방식이 Compose state update와 race condition 발생
- `currentIndex` 변경 시 TTS가 계속 반복 재생되는 문제

### Fix Plan
1. `StudyScreen`의 로컬 `isSpeaking` 제거 — `ttsManager.isSpeaking` StateFlow만 사용
2. `LaunchedEffect`에서 `while` 루프 제거, 단순 `delay(200)` 후 `speak()` 호출로 변경
3. `currentIndex` 변경 시 `QUEUE_FLUSH`로 이전 음성 중지 후 재생
4. `AndroidTtsManager.speak()`에서 기존 재생 중인 음성을 먼저 중단

### Files
- `StudyScreen.kt`: TTS 재생 로직 단순화
- `AndroidTtsManager.kt`: speak() 전에 stop() 추가

---

## 3. 3개 탭 구조 도입

### Current State
- StudyScreen이 단일 뷰로 모든 기능 수행
- Navigation: Home → study/{bundleId}

### New Structure
```
StudyScreen (TabRow)
├── Tab 1: Word List (전체 단어 목록)
│   - LazyColumn으로 단어 표시
│   - 단어 터치 시 TTS 발음 재생
│   - Scrollable list
├── Tab 2: Study Mode (공부 모드)
│   - 기존 FlashcardView 유지
│   - 영어/한글 flip + swap 지원
│   - "잘 모르는 단어" marking 기능 추가
│   - Filtering toggle (marked only)
└── Tab 3: Test Mode (테스트 모드)
│   - 한글 의미만 표시
│   - 영어 입력 필드 + 제출 버튼
│   - 정답/오답 피드백
```

### DB Schema 변경 필요
`WordItemEntity`에 `isDifficult: Boolean = false` 필드 추가:
```kotlin
@Entity(tableName = "word_items", ...)
data class WordItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bundleId: Long,
    val word: String,
    val meaning: String,
    val imagePath: String?,
    val isDifficult: Boolean = false  // NEW
)
```

`WordDao`에 쿼리 추가:
- `@Query("SELECT * FROM word_items WHERE bundleId = :bundleId AND isDifficult = 1")` — difficult words only
- `@Query("UPDATE word_items SET isDifficult = :isDifficult WHERE id = :itemId")` — toggle

### Files
- `WordItemEntity.kt`: `isDifficult` 필드 추가
- `WordDao.kt`: filtering/toggle 쿼리 추가
- `StudyScreen.kt`: TabRow + 3개 탭 Composable로 재구성
- `WordViewModel.kt`: `toggleWordDifficult()`, `getWordsByBundleWithDifficultFilter()` 메서드 추가

---

## 4. Swipe Gesture 네비게이션

### Current State
- `NavigationButton` (Previous/Next)으로 카드 전환
- `currentIndex` 직접 조작

### Change Plan
- `HorizontalPager` 사용 (Compose 이미 `rememberPagerState` import됨)
- `HorizontalPager`의 `state`로 `currentPage` 관리
- **터치 = Card Flip**: `onTap`으로 `isRotated` 토글
- **swipe left → next**, **swipe right → prev**: `HorizontalPager` 기본 동작

```kotlin
val pagerState = rememberPagerState(pageCount = { words.size })

HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxSize()
) { page ->
    currentIndex = page
    FlashcardView(...)
}
```

- 기존 `onNext`/`onPrev` 버튼 삭제
- Page 변경 시 TTS 자동 재생 유지 (LaunchedEffect에서 pagerState.currentPage 추적)

### Files
- `StudyScreen.kt`: 버튼 제거, HorizontalPager로 교체

---

## 5. Font Size — dp 단위 변환 + 크기 조절

### Current State
- 모든 폰트 `MaterialTheme.typography.*` 사용 (이미 dp 기반)
- 하지만 `headlineMedium`, `titleLarge` 등이 절대적 크기라 Tablet에서 작게 보임
- 사용자 폰트 크기 조절 기능 없음

### Change Plan
1. **Tablet 대응**: `LocalTextStyle.Provider` 또는 `ConfigurationCompat`로 디바이스 크기에 따른 스케일링
2. **폰트 크기 조절 UI**: StudyScreen에 `+` / `-` 버튼 추가
   - `fontWeightAdjustment` State 추가 (0, 1, 2 단계)
   - `MaterialTheme.typography.headlineMedium.copy(fontSize = ...)`로 동적 적용
3. **Jetpack WindowManager** 또는 `WindowMetricsCalculator`로 Tablet 감지

### Files
- `StudyScreen.kt`: 폰트 크기 조절 UI 추가 (슬라이더 또는 +/- 버튼)
- `FlashcardView.kt`: 폰트 크기를 파라미터로 받도록 수정

---

## 6. Card UI 단순화

### Current State
- `FlashcardView.kt:73-115`: `CardContent`에서 `label`("Word"/"Meaning") + 실제 텍스트 표시
- 라벨이 항상 보이므로 시각적 노이즈

### Change Plan
- `CardContent`에서 `label` 제거
- 단어면 단어만, 뜻이면 뜻만 중앙 정렬로 표시
- `FlashcardView`의 `label` 파라미터 삭제
- `isFront`/`isSwapped`에 따라 동적으로 word 또는 meaning 표시

```kotlin
// Before: label + text
Text(label, style = labelMedium)   // ← 제거
Spacer(height = 12.dp)              // ← 제거
Text(word, style = headlineMedium)  // ← 유지

// After: word만 표시
Text(word, style = headlineLarge, textAlign = TextAlign.Center)
```

### Files
- `FlashcardView.kt`: label 파라미터 삭제, 단순화
- `StudyScreen.kt`: FlashcardView 호출에서 label 관련 인자 제거

---

## 구현 순서 (권장)

| Phase | Priority | Description | Estimated Impact |
|-------|----------|-------------|-----------------|
| P0 | HIGH | #2 TTS Bug Fix + #1 Locale 고정 | Bug fix, 빠른 영향 |
| P1 | HIGH | #6 Card UI 단순화 | Low risk, 즉시 적용 |
| P2 | HIGH | #5 Font 크기 조절 | UX 개선 |
| P3 | MED | #4 Swipe Gesture 네비게이션 | Compose Pager 사용, 비교적 단순 |
| P4 | MED | #3 DB Schema 변경 + 3개 탭 구조 | 가장 큰 변경, 별도 작업 |

### 파일별 변경 요약

| File | Changes |
|------|---------|
| `WordItemEntity.kt` | `isDifficult: Boolean` 추가 |
| `WordDao.kt` | difficult filtering/toggle 쿼리 추가 |
| `WordRepository.kt` | difficult 관련 메서드 추가 |
| `WordViewModel.kt` | toggle/getDifficultWords 메서드 |
| `StudyScreen.kt` | 전면 재구성 (TabRow, Pager, TTS fix, font control) |
| `FlashcardView.kt` | label 제거, 단순화, font 파라미터 추가 |
| `AndroidTtsManager.kt` | speak() 전 stop() 추가 |
| `AppNavigation.kt` | 변경 없음 (호환 유지) |

---

## 검증 항목
- [ ] Bundle 진입 시 TTS US accent만 적용, locale chip 없음
- [ ] 카드 진입/이동 시 TTS가 한 번만 재생 (반복 없음)
- [ ] 3개 탭 정상 표시 및 전환
- [ ] Word List에서 단어 터치 → TTS 재생
- [ ] Study Mode에서 flip + swap正常工作
- [ ] Study Mode에서 difficult marking + filtering
- [ ] Test Mode에서 정답/오답 판정正常工作
- [ ] Swipe left/right로 카드 이동
- [ ] Card 터치로 flip
- [ ] 폰트 크기 조절 UI 작동 (3단계)
- [ ] Tablet에서 폰트 적절히 표시
- [ ] Card에 label 없이 단어/뜻만 표시
