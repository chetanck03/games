# Sortify: Ball Sort Puzzle

A vibrant ball-sort puzzle game for Android — sort colorful balls into glass test tubes in this brain-teasing, visually stunning mobile experience.

## Screenshots

| Sortify | Gameplay | Hint |
|:-----------:|:--------:|:-------:|
| ![Home Screen](app/src/main/assets/sortify/pic0.png) | ![Gameplay](app/src/main/assets/sortify/pic1.png) | ![Victory](app/src/main/assets/sortify/pic2.png) |

| Level Select | In-Progress |
|:--------------:|:-------------:|
| ![Level Select](app/src/main/assets/sortify/pic0.png) | ![In-Progress](app/src/main/assets/sortify/pic3.png) |

| Theme: Fruits | Theme: Gems | Theme: Neon |
|:-----------:|:-----------:|:-----------:|
| ![Fruits Theme](app/src/main/assets/sortify/pic4.png) | ![Gems Theme](app/src/main/assets/sortify/pic5.png) | ![Neon Theme](app/src/main/assets/sortify/pic6.png) |

<img src="app/src/main/assets/sortify/pic7.png" alt="Game Preview" width="320"/>

---

## Features

- **40 Brain-Teasing Levels** — Sort colored balls into test tubes until each tube contains only one color
- **Progressive Difficulty** — Levels scale from 2 colors to 10 colors, with varying tube capacities
- **5 Ball Themes** — Fruits, Veggies, Gems, Candy, and Neon — switch anytime in Settings
- **Power-Ups** — Undo moves and get Hints to help through tough levels
- **Scoring System** — +10 points per level win, progress saved locally
- **Stunning Visuals** — Glass tubes with 3D glossy balls, particle effects, and dark themed backgrounds
- **Ad-Supported** — Banner ads and interstitial ads (every 2 level completions) via AdMob

---

## Architecture

The app uses a **hybrid architecture** — a native Android shell wrapping an HTML5/JavaScript game engine inside a WebView.

```
Android Native (Kotlin)          HTML5 Game Engine
┌─────────────────────┐          ┌──────────────────┐
│  MainActivity       │          │  index.html       │
│  ├─ WebView         │◄────────►│  ├─ Ball Physics  │
│  ├─ AdMob (Banner)  │  JS      │  ├─ Level System  │
│  ├─ AdMob (Interstitial)│Bridge │  ├─ Rendering     │
│  ├─ Internet Check  │          │  └─ Game State     │
│  └─ Immersive Mode  │          │                    │
└─────────────────────┘          └──────────────────┘
```

### Key Components

| Component | File | Description |
|-----------|------|-------------|
| Native Shell | [MainActivity.kt](app/src/main/java/com/cktechhub/games/MainActivity.kt) | WebView setup, AdMob, immersive mode, internet check |
| Game Engine | [index.html](app/src/main/assets/index.html) | Full HTML5/JS game with Tailwind CSS |
| Marketing Site | [website/index.html](website/index.html) | Privacy policy & app landing page |
| Ad Bridge | `AdBridge` inner class | JavaScript-to-Android bridge for ad triggers |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Native | Kotlin, Android SDK 36, AppCompat |
| Game Engine | HTML5, JavaScript, CSS3, Tailwind CSS |
| Ads | Google AdMob (Banner + Interstitial) |
| Build | Gradle (Kotlin DSL), AGP 9.0.1 |

---

## Project Structure

```
games/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   ├── img/                    # Legacy game images
│   │   │   ├── sortify/                # Updated game screenshots
│   │   │   │   ├── balls-sort-logo.png # Home screen logo
│   │   │   │   ├── pic0.png            # Level select screen
│   │   │   │   ├── pic1.png            # Gameplay screenshot
│   │   │   │   ├── pic2.png            # Victory screen
│   │   │   │   ├── pic3.png            # In-progress level
│   │   │   │   ├── pic4.png            # Fruits theme
│   │   │   │   ├── pic5.png            # Gems theme
│   │   │   │   ├── pic6.png            # Neon theme
│   │   │   │   └── pic7.png            # Game preview
│   │   │   └── index.html              # HTML5 game engine
│   │   ├── java/com/cktechhub/games/
│   │   │   └── MainActivity.kt         # Native Android activity
│   │   ├── res/                        # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── website/
│   └── index.html                      # Marketing/privacy site
├── gradle/
│   └── libs.versions.toml              # Dependency versions catalog
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android SDK 36
- Min SDK 29 (Android 10+)

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/chetanck03/games
   cd games
   ```

2. Open in Android Studio

3. Sync Gradle and run on a device or emulator

---

## AdMob Configuration

The app uses test AdMob IDs. Before releasing to production, replace these in [MainActivity.kt](app/src/main/java/com/cktechhub/games/MainActivity.kt):

```kotlin
private const val BANNER_AD_UNIT_ID = "ca-app-pub-XXXXX/YYYYY"
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXX/YYYYY"
```

Also update the application ID in [AndroidManifest.xml](app/src/main/AndroidManifest.xml):

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXX~YYYYY" />
```

Interstitial ads show every **2 level completions** (configurable via `INTERSTITIAL_FREQUENCY`).

---

## Game Mechanics

1. **Goal** — Sort all balls so each test tube contains balls of only one color
2. **Moves** — Tap a tube to pick up the top ball, then tap another tube to drop it
3. **Rules**:
   - You can only place a ball on top of a ball of the same color, or into an empty tube
   - Tubes have a maximum capacity (typically 4 balls)
4. **Power-Ups**:
   - **Undo** — Reverse the last move
   - **Hint** — Highlights a valid move
   - **Restart** — Restart the current level from scratch

---

## License

This project is proprietary. All rights reserved.
