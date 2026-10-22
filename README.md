# WordMemory — OCR 기반 영어 단어 플래시카드 앱

Android 기기에서 사진 찍기 또는 갤러리 선택을 통해 영어 단어를 추출하고, 플래시카드로 공부할 수 있는 앱입니다. OCR(ML Kit) → 자동 단어 파싱 → 번들 관리 → 플래시카드 학습의 전 과정을 지원합니다.

## 주요 기능

- **다중 이미지 OCR** — 카메라로 촬영하거나 갤러리에서 여러 장을 동시에 선택해 텍스트 추출
- **스마트 단어 파싱** — 추출된 원문에서 영어 단어와 뜻을 자동으로 분리
- **워드 번들 관리** — 추출한 단어를 이름 있는 번들로分组 (예: "여행 영어", "비즈니스 영단어")
- **인터랙티브 플래시카드** — 부드러운 3D 회전 애니메이션으로 단어 ↔ 뜻 전환 학습
- **다국어 발음 듣기 (TTS)** — 미국, 영국, 호주accent 지원. 자동 재생 및 수동 재생
- **단어 수정 / 추가** — 번들 편집 모드에서 단어와 뜻을 직접 수정하거나 새로 추가
- **학습 진행도 추적** — 각 번들의 총 단어 수, 학습 완료 여부 표시

## 앱 사용 흐름

```
홈 화면 (번들 목록)
  ├── 새 번들 만들기 → OCR 이미지 선택 → 텍스트 추출 → 단어 파싱 → 번들 저장
  └기 번들 선택 → 학습 시작 → 플래시카드 회전(단어↔뜻) → 완료
         └── 번들 편집 → 단어 수정 / 추가 → 저장
```

## 기술 스택

| 항목 | 기술 |
|------|------|
| 플랫폼 | Android (Kotlin) |
| UI | Jetpack Compose (Material 3) |
| OCR | Google ML Kit (Text Recognition API) |
| DB | Room Persistence Library (SQLite) |
| TTS | Android `TextToSpeech` 엔진 |
| 이미지 | Coil |
| AI | Google Gemini API (단어/뜻 파싱 보정) |

## 빌드 및 CI/CD

GitHub Actions를 통해 모든 push / PR에서 자동으로 빌드됩니다.

- **Debug APK:** `WordMemoryDbg.apk` — 테스트용
- **Release APK:** `WordMemory.apk` — 배포용 (서명 포함)

Release APK는 새 버전 태그(`vX.Y.Z`)를 push할 때 GitHub Releases에 자동 업로드됩니다.

## 설치

GitHub Actions의 build artifact 또는 [Releases 페이지](https://github.com/fearghost/word_memory_card/releases)에서 APK를 다운로드하여 설치하세요.

## 라이선스

MIT License
