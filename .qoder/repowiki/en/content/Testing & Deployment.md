# Testing & Deployment

<cite>
**Referenced Files in This Document**
- [ExampleUnitTest.kt](file://app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt)
- [ExampleInstrumentedTest.kt](file://app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt)
- [MainActivity.kt](file://app/src/main/java/com/cktechhub/games/MainActivity.kt)
- [index.html](file://app/src/main/assets/index.html)
- [AndroidManifest.xml](file://app/src/main/AndroidManifest.xml)
- [build.gradle.kts](file://app/build.gradle.kts)
- [build.gradle.kts](file://build.gradle.kts)
- [libs.versions.toml](file://gradle/libs.versions.toml)
- [settings.gradle.kts](file://settings.gradle.kts)
- [proguard-rules.pro](file://app/proguard-rules.pro)
- [strings.xml](file://app/src/main/res/values/strings.xml)
- [ADMOB_SETUP.md](file://ADMOB_SETUP.md)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)
10. [Appendices](#appendices)

## Introduction
This document provides comprehensive guidance for testing and deployment of the Android application that integrates a WebView-based HTML5 game with an Android bridge for AdMob interstitial advertising. It covers:
- Unit testing strategies for Kotlin code
- Integration testing methodologies for WebView–JavaScript communication
- Performance testing for mobile devices
- Implementation details for test case structure, mock setups, and automated workflows
- Configuration options for testing environments and continuous integration
- Quality assurance processes
- Practical examples for test execution, debugging, and performance profiling
- Common testing challenges such as WebView automation, JavaScript bridge testing, and cross-platform compatibility
- Deployment procedures including build optimization, release preparation, and distribution channels
- Troubleshooting guidance for testing and deployment issues

## Project Structure
The project follows a standard Android module layout with separate source sets for unit tests and instrumentation tests. The application loads a local HTML5 game via WebView and exposes a JavaScript interface to trigger native behavior (e.g., showing interstitial ads).

```mermaid
graph TB
subgraph "Android App Module"
A["app/src/main/java/com/cktechhub/games/MainActivity.kt"]
B["app/src/main/AndroidManifest.xml"]
C["app/src/main/assets/index.html"]
D["app/src/main/res/values/strings.xml"]
E["app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt"]
F["app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt"]
G["app/build.gradle.kts"]
H["app/proguard-rules.pro"]
end
subgraph "Root Config"
R1["build.gradle.kts"]
R2["settings.gradle.kts"]
R3["gradle/libs.versions.toml"]
end
A --> C
A --> B
A --> D
E --> A
F --> A
G --> R3
R1 --> G
R2 --> G
```

**Diagram sources**
- [MainActivity.kt:1-441](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L1-L441)
- [AndroidManifest.xml:1-51](file://app/src/main/AndroidManifest.xml#L1-L51)
- [index.html:1-1094](file://app/src/main/assets/index.html#L1-L1094)
- [strings.xml:1-6](file://app/src/main/res/values/strings.xml#L1-L6)
- [ExampleUnitTest.kt:1-17](file://app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt#L1-L17)
- [ExampleInstrumentedTest.kt:1-24](file://app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt#L1-L24)
- [build.gradle.kts:1-43](file://app/build.gradle.kts#L1-L43)
- [proguard-rules.pro:1-21](file://app/proguard-rules.pro#L1-L21)
- [build.gradle.kts:1-4](file://build.gradle.kts#L1-L4)
- [settings.gradle.kts:1-27](file://settings.gradle.kts#L1-L27)
- [libs.versions.toml:1-28](file://gradle/libs.versions.toml#L1-L28)

**Section sources**
- [build.gradle.kts:1-43](file://app/build.gradle.kts#L1-L43)
- [settings.gradle.kts:1-27](file://settings.gradle.kts#L1-L27)
- [libs.versions.toml:1-28](file://gradle/libs.versions.toml#L1-L28)
- [build.gradle.kts:1-4](file://build.gradle.kts#L1-L4)

## Core Components
- MainActivity: Hosts the WebView, configures settings, injects a JavaScript bridge, handles lifecycle events, and manages AdMob banners and interstitials.
- WebView content: Local HTML/CSS/JS game loaded from app assets.
- Tests: Basic unit test and instrumentation test scaffolding present; extension points for deeper coverage.
- Dependencies: JUnit, AndroidX Test/JUnit, Espresso, and Play Services Ads.

Key testing-relevant areas:
- WebView client and chrome client configuration
- JavaScript interface exposed to JS
- AdMob integration and interstitial frequency logic
- Network connectivity checks and offline UI

**Section sources**
- [MainActivity.kt:165-263](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L165-L263)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)
- [MainActivity.kt:370-409](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L370-L409)
- [MainActivity.kt:296-302](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L296-L302)
- [ExampleUnitTest.kt:12-17](file://app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt#L12-L17)
- [ExampleInstrumentedTest.kt:17-24](file://app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt#L17-L24)
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)

## Architecture Overview
The app architecture centers around an Activity hosting a WebView that renders a local HTML5 game. The bridge between Android and JavaScript is implemented via a JavaScriptInterface. AdMob is initialized early and interstitials are shown based on game events.

```mermaid
graph TB
M["MainActivity.kt"]
WV["WebView"]
JS["index.html (Game)"]
BR["AdBridge (JavaScriptInterface)"]
AD["AdMob SDK"]
M --> WV
WV --> JS
M --> BR
BR <-- "window.AndroidBridge.onLevelComplete()" --> M
M --> AD
```

**Diagram sources**
- [MainActivity.kt:165-263](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L165-L263)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)
- [index.html:1-1094](file://app/src/main/assets/index.html#L1-L1094)

## Detailed Component Analysis

### WebView and JavaScript Bridge Testing
The WebView is configured with JavaScript enabled and a JavaScriptInterface named AndroidBridge. The bridge exposes a method invoked by the game when a level completes. The Activity injects a small script to wrap the game’s level-complete callback and invoke the bridge.

```mermaid
sequenceDiagram
participant Game as "index.html"
participant WV as "WebView"
participant Act as "MainActivity"
participant BR as "AdBridge"
Act->>WV : "loadUrl(file : ///android_asset/index.html)"
WV-->>Act : "onPageFinished(...)"
Act->>WV : "evaluateJavascript(inject bridge hook)"
Game->>Game : "showLevelComplete()"
Game->>BR : "window.AndroidBridge.onLevelComplete()"
BR->>Act : "onLevelComplete()"
Act->>Act : "increment counter<br/>check frequency<br/>showInterstitialAd()"
```

**Diagram sources**
- [MainActivity.kt:131](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L131)
- [MainActivity.kt:209-229](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L209-L229)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)

**Section sources**
- [MainActivity.kt:165-263](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L165-L263)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)
- [index.html:1-1094](file://app/src/main/assets/index.html#L1-L1094)

### Unit Testing Strategies for Kotlin
Current unit test coverage is minimal. Recommended extensions:
- Mock Android dependencies (context, WebView, ConnectivityManager) using a framework like MockK.
- Test logic in isolation:
  - AdMob bridge counting and interstitial scheduling
  - Connectivity checks and offline UI rendering
  - Lifecycle-safe WebView settings and clients
- Use Robolectric for Android framework classes when needed.

Test case structure recommendations:
- Arrange: Prepare mocks and stub Android services.
- Act: Invoke methods under test (e.g., onLevelComplete, isInternetAvailable).
- Assert: Verify interactions with mocks and state transitions.

**Section sources**
- [ExampleUnitTest.kt:12-17](file://app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt#L12-L17)
- [MainActivity.kt:296-302](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L296-L302)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)

### Integration Testing Methodologies for WebView–JavaScript Communication
Recommended approaches:
- Instrumentation tests with a local server or asset-based HTML to simulate real JS interactions.
- Use Espresso with Idling Resources to synchronize WebView loading and JS evaluation.
- Validate that evaluateJavascript executes and the bridge method is called without errors.
- Verify UI updates triggered by JS callbacks (e.g., interstitial shown).

Mock implementations:
- Stub WebViewClient and WebChromeClient to intercept navigation and console logs.
- Mock AdMob SDK initialization and interstitial callbacks to avoid network calls during tests.

**Section sources**
- [ExampleInstrumentedTest.kt:17-24](file://app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt#L17-L24)
- [MainActivity.kt:195-245](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L195-L245)
- [MainActivity.kt:247-256](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L247-L256)
- [MainActivity.kt:209-229](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L209-L229)

### Performance Testing for Mobile Devices
Guidance:
- Measure startup time from process launch to first visible frame.
- Profile WebView rendering and JS execution using Android Studio Profiler.
- Monitor memory usage during gameplay and interstitial transitions.
- Validate performance across device categories (entry-level vs flagship) using physical devices.

Common metrics:
- Time to load index.html from assets
- Time to first interstitial after threshold events
- Memory footprint during particle effects and animations

**Section sources**
- [MainActivity.kt:131](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L131)
- [MainActivity.kt:370-409](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L370-L409)
- [index.html:1-1094](file://app/src/main/assets/index.html#L1-L1094)

### Automated Testing Workflows
Recommended Gradle tasks and CI configuration:
- Unit tests: ./gradlew test
- Instrumentation tests: ./gradlew connectedAndroidTest
- Combined verification: ./gradlew check

CI pipeline suggestions:
- Trigger on pull requests and pushes to main branch.
- Run unit tests on every commit.
- Run instrumentation tests on emulator farm or cloud device providers.
- Upload test reports and artifacts.

**Section sources**
- [build.gradle.kts:16](file://app/build.gradle.kts#L16)
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)

### Configuration Options for Testing Environments
- Test runner: AndroidJUnitRunner configured in module build script.
- Dependencies: JUnit, AndroidX Test JUnit, Espresso.
- ProGuard/R8: Keep rules for JavaScript interfaces if obfuscation is enabled.

**Section sources**
- [build.gradle.kts:16](file://app/build.gradle.kts#L16)
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)
- [proguard-rules.pro:8-13](file://app/proguard-rules.pro#L8-L13)

### Quality Assurance Processes
- Code coverage: Integrate a coverage tool (e.g., Jacoco) to track unit test coverage.
- Static analysis: Apply lint checks and custom rules for WebView and AdMob usage.
- Accessibility: Ensure WebView content meets accessibility guidelines.
- Regression testing: Maintain a suite of UI tests covering critical user flows.

**Section sources**
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)

### Practical Examples: Test Execution and Debugging
- Running unit tests locally: ./gradlew testDebugUnitTest
- Running instrumentation tests: ./gradlew connectedDebugAndroidTest
- Debugging WebView console messages: Observe logs captured by WebChromeClient.
- Verifying bridge invocation: Add assertions in tests that the bridge method increments the internal counter.

**Section sources**
- [MainActivity.kt:247-256](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L247-L256)
- [MainActivity.kt:428-439](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L428-L439)

### Common Testing Challenges and Resolutions
- WebView automation: Use Espresso WebView matchers and ensure proper synchronization with onPageFinished.
- JavaScript bridge testing: Validate evaluateJavascript execution and confirm the bridge method is reachable from JS.
- Cross-platform compatibility: Test on various Android versions and screen sizes; verify WebView settings and permissions.

**Section sources**
- [MainActivity.kt:173-189](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L173-L189)
- [AndroidManifest.xml:5-8](file://app/src/main/AndroidManifest.xml#L5-L8)

## Dependency Analysis
The app depends on AndroidX libraries, JUnit, and Play Services Ads. The module build script defines test dependencies and the Android test runner.

```mermaid
graph LR
App["app/build.gradle.kts"]
JUnit["JUnit"]
AXJ["AndroidX Test JUnit"]
Espresso["Espresso Core"]
Ads["Play Services Ads"]
App --> JUnit
App --> AXJ
App --> Espresso
App --> Ads
```

**Diagram sources**
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)
- [libs.versions.toml:13-21](file://gradle/libs.versions.toml#L13-L21)

**Section sources**
- [build.gradle.kts:34-43](file://app/build.gradle.kts#L34-L43)
- [libs.versions.toml:13-21](file://gradle/libs.versions.toml#L13-L21)

## Performance Considerations
- Minimize WebView overhead by disabling unnecessary features and keeping JavaScript interface methods lightweight.
- Use conservative interstitial frequency to balance engagement and performance.
- Profile GPU and CPU usage during animations and particle effects.
- Optimize image assets and reduce DOM complexity in the HTML game.

[No sources needed since this section provides general guidance]

## Troubleshooting Guide
- AdMob IDs still set to test values: Replace both the App ID in AndroidManifest.xml and the ad unit IDs in MainActivity.kt before release.
- Interstitial not showing: Confirm ad readiness and callback handling; verify frequency logic and UI thread usage.
- WebView fails to load local content: Ensure the asset path is correct and permissions are declared.
- Network-related failures: Validate connectivity checks and offline UI flow.

**Section sources**
- [ADMOB_SETUP.md:1-104](file://ADMOB_SETUP.md#L1-L104)
- [AndroidManifest.xml:20-28](file://app/src/main/AndroidManifest.xml#L20-L28)
- [MainActivity.kt:402-409](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L402-L409)
- [MainActivity.kt:131](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L131)
- [MainActivity.kt:296-302](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L296-L302)

## Conclusion
This guide outlines a practical roadmap for testing and deploying the WebView-backed Android game with AdMob integration. By extending unit and instrumentation tests, validating the JavaScript bridge, and applying performance profiling, teams can ensure robust functionality and reliable releases. Proper configuration of testing environments and CI pipelines further strengthens quality assurance.

[No sources needed since this section summarizes without analyzing specific files]

## Appendices

### Appendix A: Test Case Structure Template
- Unit tests:
  - Arrange: Create mocks for Android services and WebView.
  - Act: Call the method under test (e.g., onLevelComplete).
  - Assert: Verify interactions and state changes.
- Instrumentation tests:
  - Launch activity and wait for WebView load.
  - Execute JS to trigger bridge.
  - Assert UI and native behavior.

**Section sources**
- [ExampleUnitTest.kt:12-17](file://app/src/test/java/com/cktechhub/games/ExampleUnitTest.kt#L12-L17)
- [ExampleInstrumentedTest.kt:17-24](file://app/src/androidTest/java/com/cktechhub/games/ExampleInstrumentedTest.kt#L17-L24)

### Appendix B: Release Preparation Checklist
- Replace test AdMob IDs with production IDs in both AndroidManifest.xml and MainActivity.kt.
- Enable ProGuard/R8 with appropriate keep rules for JavaScript interfaces.
- Build a release variant with minification disabled initially for stability.
- Validate interstitial flow and offline UI on real devices.
- Publish to distribution channels after QA approval.

**Section sources**
- [ADMOB_SETUP.md:1-104](file://ADMOB_SETUP.md#L1-L104)
- [proguard-rules.pro:8-13](file://app/proguard-rules.pro#L8-L13)
- [build.gradle.kts:20-26](file://app/build.gradle.kts#L20-L26)