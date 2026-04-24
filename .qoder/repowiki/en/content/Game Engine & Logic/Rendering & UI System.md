# Rendering & UI System

<cite>
**Referenced Files in This Document**
- [MainActivity.kt](file://app/src/main/java/com/cktechhub/games/MainActivity.kt)
- [index.html](file://app/src/main/assets/index.html)
- [AndroidManifest.xml](file://app/src/main/AndroidManifest.xml)
- [build.gradle.kts](file://app/build.gradle.kts)
- [settings.gradle.kts](file://settings.gradle.kts)
</cite>

## Update Summary
**Changes Made**
- Added comprehensive theme system documentation with 5 visual themes (fruits, veggies, gems, candy, neon)
- Enhanced 3D sphere rendering with realistic lighting effects and emoji overlays
- Improved tube styling with glow animations and theme-specific styling
- Updated responsive tube rendering algorithm to support theme variations
- Added theme management functionality including storage and application

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Theme System Implementation](#theme-system-implementation)
7. [Enhanced 3D Sphere Rendering](#enhanced-3d-sphere-rendering)
8. [Dependency Analysis](#dependency-analysis)
9. [Performance Considerations](#performance-considerations)
10. [Troubleshooting Guide](#troubleshooting-guide)
11. [Conclusion](#conclusion)

## Introduction
This document explains the rendering system and UI implementation for a mobile puzzle game embedded in an Android WebView. It focuses on:
- Responsive tube rendering algorithm and dynamic sizing calculations
- Ball positioning logic inside tubes with enhanced 3D sphere rendering
- Event delegation for tube interactions, touch/click handling, and user feedback
- Comprehensive theme system with 5 visual themes (fruits, veggies, gems, candy, neon)
- Emoji overlays for themed balls and improved tube styling with glow animations
- Integration with CSS animations and visual effect triggers
- Practical guidance for performance optimization, responsive layout adaptation, and cross-device compatibility

The rendering engine is implemented in a single HTML file loaded by the Android app's WebView. The Android layer handles immersive UI, ad integration, and lifecycle events, while the HTML/CSS/JS layer manages the game logic, rendering, and animations.

## Project Structure
The project consists of:
- An Android app module that hosts a WebView and loads a local HTML page
- A single HTML file containing the entire game logic, rendering, and UI styles
- Minimal native dependencies for ads and window controls

```mermaid
graph TB
A["Android App<br/>MainActivity.kt"] --> B["WebView<br/>index.html"]
B --> C["Game Logic<br/>renderTubes(), event handlers"]
B --> D["CSS Animations<br/>.tube, .ball, @keyframes"]
B --> E["Particle System<br/>Canvas"]
B --> F["Theme System<br/>5 Visual Themes"]
A --> G["AdMob Integration<br/>Interstitial/Banner"]
A --> H["Immersive Mode<br/>WindowInsets"]
```

**Diagram sources**
- [MainActivity.kt:66-135](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L66-L135)
- [index.html:548-624](file://app/src/main/assets/index.html#L548-L624)
- [index.html:8-203](file://app/src/main/assets/index.html#L8-L203)

**Section sources**
- [MainActivity.kt:42-154](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L42-L154)
- [index.html:1-203](file://app/src/main/assets/index.html#L1-L203)

## Core Components
- Rendering pipeline: calculates tube/ball sizes, lays out tubes, and renders balls with enhanced 3D sphere effects and theme styling
- Event delegation: centralized handler for tube clicks/touches with debouncing and feedback
- Animation triggers: CSS animations for selection, completion, invalid moves, undo, and particle effects
- Responsive layout: adaptive columns, gaps, and ball sizes based on viewport and tube count
- Particle system: offscreen canvas for animated particle bursts
- Audio feedback: Web Audio API-generated sounds for pick/drop/invalid/win
- **New**: Theme system: comprehensive 5-theme system with fruits, veggies, gems, candy, and neon themes

Key implementation references:
- [getTubeDimensions():548-576](file://app/src/main/assets/index.html#L548-L576)
- [renderTubes():578-624](file://app/src/main/assets/index.html#L578-L624)
- [Event delegation setup:664-689](file://app/src/main/assets/index.html#L664-L689)
- [onTubeClick():694-755](file://app/src/main/assets/index.html#L694-L755)
- [Theme system:397-483](file://app/src/main/assets/index.html#L397-L483)
- [CSS animations:8-203](file://app/src/main/assets/index.html#L8-L203)
- [Particle system:426-469](file://app/src/main/assets/index.html#L426-L469)

**Section sources**
- [index.html:548-624](file://app/src/main/assets/index.html#L548-L624)
- [index.html:664-755](file://app/src/main/assets/index.html#L664-L755)
- [index.html:397-483](file://app/src/main/assets/index.html#L397-L483)
- [index.html:8-203](file://app/src/main/assets/index.html#L8-L203)
- [index.html:426-469](file://app/src/main/assets/index.html#L426-L469)

## Architecture Overview
The rendering and UI system is a hybrid of Android and web technologies:
- Android initializes the WebView, sets immersive mode, and injects a JavaScript bridge for ad triggers
- The WebView loads index.html, which defines the game screens, styles, animations, and logic
- The game logic computes dimensions, renders tubes and balls with themes, and triggers animations
- Touch/click events are delegated to a single handler that updates state and re-renders
- **New**: Theme system manages visual themes and applies them consistently across the UI

```mermaid
sequenceDiagram
participant OS as "Android OS"
participant Act as "MainActivity"
participant WV as "WebView"
participant DOM as "index.html"
participant CSS as "Styles/Animations"
participant THEME as "Theme System"
participant CAN as "Particle Canvas"
OS->>Act : onCreate()
Act->>WV : configure settings, add JS interface
Act->>WV : loadUrl("file : ///android_asset/index.html")
WV->>DOM : parse HTML/CSS/JS
DOM->>THEME : initialize BALL_THEMES
DOM->>DOM : initSettings(), renderPreviewTubes(), showScreen("homeScreen")
WV-->>Act : onPageFinished()
DOM->>DOM : getTubeDimensions()
DOM->>DOM : renderTubes() with theme styling
DOM->>CSS : apply .tube/.ball classes and animations
DOM->>CAN : spawnParticles() on drop/complete
DOM->>DOM : setupTubeEvents() (delegated click/touch)
DOM->>DOM : onTubeClick(ti)
DOM->>DOM : canMove()/saveHistory()/updateStats()
DOM->>DOM : renderTubes() and trigger animations
THEME->>DOM : applyTheme() for visual consistency
```

**Diagram sources**
- [MainActivity.kt:66-135](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L66-L135)
- [index.html:548-624](file://app/src/main/assets/index.html#L548-L624)
- [index.html:664-755](file://app/src/main/assets/index.html#L664-L755)
- [index.html:426-469](file://app/src/main/assets/index.html#L426-L469)
- [index.html:397-483](file://app/src/main/assets/index.html#L397-L483)

## Detailed Component Analysis

### Responsive Tube Rendering Algorithm
The algorithm computes tube and ball dimensions based on viewport, tube count, and balls per color. It ensures:
- Tubes fit within available height and width
- Columns adapt to tube count (1–5, 6–8, 9+)
- Ball size constrained by both width and height budgets
- Consistent spacing and minimum sizes

```mermaid
flowchart TD
Start(["getTubeDimensions"]) --> GetViewport["Get window.innerWidth/innerHeight"]
GetViewport --> ComputeAvail["Compute available height/width<br/>deduct top/bottom/stats/progress bars"]
ComputeAvail --> ComputeCols["Compute columns based on tube count"]
ComputeCols --> Rows["rows = ceil(tubes / cols)"]
Rows --> CalcBallW["maxBallByW = floor(availW / cols * 0.72)"]
CalcBallW --> CalcBallH["maxBallByH = floor((availH / rows - 16) / (ballsPerColor + 0.5))"]
CalcBallH --> ChooseBallSize["ballSize = clamp(min(maxBallByW, maxBallByH, 48, max(20, availW/(cols*1.6))), 18..inf)"]
ChooseBallSize --> TubeDims["tubeW = ballSize + 10<br/>tubeH = ballsPerColor*(ballSize + 3) + 10"]
TubeDims --> Return(["Return {ballSize, tubeW, tubeH, cols}"])
```

**Diagram sources**
- [index.html:548-576](file://app/src/main/assets/index.html#L548-L576)

**Section sources**
- [index.html:548-576](file://app/src/main/assets/index.html#L548-L576)

### Dynamic Sizing Calculations and Layout
- Gap calculation: dynamically derived from viewport and tube count to prevent overlap
- Column distribution: 1–5 tubes use 1 column, 6–8 use 2, 9+ use 3 columns
- Ball slots: each tube has a fixed number of slots equal to balls per color; empty slots remain transparent
- Flex layout: tubes use flex-direction column with justify-content:flex-end to stack balls from bottom to top

Key references:
- [Gap computation](file://app/src/main/assets/index.html#L582)
- [Column/row logic:563-564](file://app/src/main/assets/index.html#L563-L564)
- [Slot rendering loop:598-620](file://app/src/main/assets/index.html#L598-L620)

**Section sources**
- [index.html:578-624](file://app/src/main/assets/index.html#L578-L624)
- [index.html:582](file://app/src/main/assets/index.html#L582)
- [index.html:563-564](file://app/src/main/assets/index.html#L563-L564)

### Ball Positioning Logic
- Each tube slot is sized to ballSize and styled with enhanced 3D sphere effects
- Transparent slots are rendered for unused capacity
- Ball colors are mapped from levelColors using the color index stored in the tube array
- **New**: Emoji overlays are conditionally applied based on theme configuration

References:
- [Ball slot creation and styling:600-619](file://app/src/main/assets/index.html#L600-L619)
- [Color mapping:608-612](file://app/src/main/assets/index.html#L608-L612)
- [Emoji overlay implementation:780-789](file://app/src/main/assets/index.html#L780-L789)

**Section sources**
- [index.html:600-619](file://app/src/main/assets/index.html#L600-L619)
- [index.html:780-789](file://app/src/main/assets/index.html#L780-L789)

### Event Delegation System for Tube Interactions
A single delegated listener handles both touch and mouse clicks:
- Prevents rapid duplicate triggers by tracking last touch time and a processing flag
- Uses closest selector to find the tube element and extract its index
- Invokes onTubeClick(ti) to process selection, validation, movement, and feedback

```mermaid
sequenceDiagram
participant User as "User"
participant Area as "#tubeArea"
participant DL as "Delegated Listener"
participant GL as "onTubeClick(ti)"
participant UI as "renderTubes()"
participant FX as "CSS Animations"
User->>Area : touchend/click
Area->>DL : dispatch event
DL->>DL : debounce check
DL->>GL : onTubeClick(ti)
GL->>GL : canMove()/isValidTarget()
GL->>UI : renderTubes() with theme styling
GL->>FX : add/remove animation classes
GL->>GL : playSound()/spawnParticles()
GL->>GL : updateStats()/checkWin()
```

**Diagram sources**
- [index.html:664-689](file://app/src/main/assets/index.html#L664-L689)
- [index.html:694-755](file://app/src/main/assets/index.html#L694-L755)
- [index.html:578-624](file://app/src/main/assets/index.html#L578-L624)

**Section sources**
- [index.html:664-689](file://app/src/main/assets/index.html#L664-L689)
- [index.html:694-755](file://app/src/main/assets/index.html#L694-L755)

### User Feedback Mechanisms and Visual Effects
- Selection: tube.selected adds translateY and scale with glow
- Valid target: tube.valid-target highlights with green glow
- Completion: tube.complete pulses with yellow glow animation
- Invalid move: shake animation triggered via inline style reset
- Undo: ball-undo animation applied to all balls
- Drop: ball-drop animation applied to the newly placed ball
- Particles: spawnParticles() emits directional particles on drop/complete

References:
- [Tube states and animations:40-57](file://app/src/main/assets/index.html#L40-L57)
- [Invalid move shake:716-721](file://app/src/main/assets/index.html#L716-L721)
- [Undo animation:773-778](file://app/src/main/assets/index.html#L773-L778)
- [Drop animation:744-749](file://app/src/main/assets/index.html#L744-L749)
- [Particle spawning:737-749](file://app/src/main/assets/index.html#L737-L749)

**Section sources**
- [index.html:40-57](file://app/src/main/assets/index.html#L40-L57)
- [index.html:716-721](file://app/src/main/assets/index.html#L716-L721)
- [index.html:773-778](file://app/src/main/assets/index.html#L773-L778)
- [index.html:744-749](file://app/src/main/assets/index.html#L744-L749)
- [index.html:737-749](file://app/src/main/assets/index.html#L737-L749)

### CSS Animation System and Triggers
- .tube.selected/.valid-target/.complete define transitions and keyframe-driven pulsing
- .ball-drop/.ball-bounce/.ball-undo control per-ball animations
- Shake animation injected dynamically for invalid moves
- Star burst and particle effects enhance level completion

References:
- [Animation definitions:54-83](file://app/src/main/assets/index.html#L54-L83)
- [Injected shake animation:1069-1071](file://app/src/main/assets/index.html#L1069-L1071)

**Section sources**
- [index.html:54-83](file://app/src/main/assets/index.html#L54-L83)
- [index.html:1069-1071](file://app/src/main/assets/index.html#L1069-L1071)

### Responsive Layout Adaptation and Cross-Device Compatibility
- Viewport meta tag prevents zoom and sets initial scale
- Safe area padding applied to game screen for devices with notches
- Resize throttling: renderTubes() is invoked after a short delay on resize
- Adaptive columns and gaps ensure compact layouts on small screens

References:
- [Viewport and safe area](file://app/src/main/assets/index.html#L5)
- [Safe area padding](file://app/src/main/assets/index.html#L193)
- [Resize handling:1058-1064](file://app/src/main/assets/index.html#L1058-L1064)

**Section sources**
- [index.html:5](file://app/src/main/assets/index.html#L5)
- [index.html:193](file://app/src/main/assets/index.html#L193)
- [index.html:1058-1064](file://app/src/main/assets/index.html#L1058-L1064)

## Theme System Implementation

### Comprehensive Theme Architecture
The game now features a sophisticated theme system with 5 distinct visual themes, each providing unique color palettes, tube styling, and emoji representations:

```mermaid
graph TB
THEME_SYS["Theme System"] --> FRUITS["Fruits Theme<br/>🍎 🫐 🍋 🍉 🍊 🍇 🍓 🍐 🥝 🥭"]
THEME_SYS --> VEGGIES["Veggies Theme<br/>🌶️ 🥬 🌽 🫑 🥕 💜 🥒 🩷 🥬 🧅"]
THEME_SYS --> GEMS["Gems Theme<br/>❤️ 💎 💛 💚 🧡 💜 🩷 🩵 🤍 🖤"]
THEME_SYS --> CANDY["Candy Theme<br/>🍬 🍭 🍋 🌿 🍮 💜 🫧 🍏 🫧 🍯"]
THEME_SYS --> NEON["Neon Theme<br/>🔴 🔵 🟡 🟢 🟠 🟣 🟡 🟣 🟢 🟠"]
THEME_SYS --> MANAGER["Theme Manager<br/>applyTheme(), renderThemeGrid()"]
THEME_SYS --> STORAGE["Local Storage<br/>bsp_theme"]
```

**Diagram sources**
- [index.html:397-483](file://app/src/main/assets/index.html#L397-L483)
- [index.html:1282-1296](file://app/src/main/assets/index.html#L1282-L1296)
- [index.html:1298-1349](file://app/src/main/assets/index.html#L1298-L1349)

### Theme Configuration Structure
Each theme consists of:
- **Visual Colors**: Background colors, glow effects, and tube styling
- **Ball Colors**: 10 distinct colors with emoji representations
- **Tube Styling**: Border colors and glow effects specific to each theme
- **Name**: Human-readable theme identifier

Key implementation references:
- [Theme definitions:397-483](file://app/src/main/assets/index.html#L397-L483)
- [Theme state management:487-508](file://app/src/main/assets/index.html#L487-L508)
- [Theme application:1282-1296](file://app/src/main/assets/index.html#L1282-L1296)

**Section sources**
- [index.html:397-483](file://app/src/main/assets/index.html#L397-L483)
- [index.html:487-508](file://app/src/main/assets/index.html#L487-L508)
- [index.html:1282-1296](file://app/src/main/assets/index.html#L1282-L1296)

### Theme Grid Interface
The settings modal provides an intuitive grid interface for theme selection:
- 3-column grid layout with theme previews
- Mini ball previews showing theme colors
- Visual indicators for currently selected theme
- Smooth hover and selection animations

References:
- [Theme grid rendering:1298-1349](file://app/src/main/assets/index.html#L1298-L1349)
- [Theme selection handler:1341-1346](file://app/src/main/assets/index.html#L1341-L1346)

**Section sources**
- [index.html:1298-1349](file://app/src/main/assets/index.html#L1298-L1349)
- [index.html:1341-1346](file://app/src/main/assets/index.html#L1341-L1346)

### Theme Persistence and Application
Themes are persisted across sessions and applied consistently:
- Local storage integration for theme preferences
- Automatic theme application on game load
- Dynamic color mapping preserving ball order
- Real-time theme switching with instant visual updates

References:
- [Theme persistence:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)
- [Theme application:1282-1296](file://app/src/main/assets/index.html#L1282-L1296)
- [Color mapping preservation:1284-1289](file://app/src/main/assets/index.html#L1284-L1289)

**Section sources**
- [index.html:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)
- [index.html:1282-1296](file://app/src/main/assets/index.html#L1282-L1296)
- [index.html:1284-1289](file://app/src/main/assets/index.html#L1284-L1289)

## Enhanced 3D Sphere Rendering

### Realistic Lighting Effects
The ball rendering system has been enhanced with sophisticated 3D sphere effects:
- Radial gradient backgrounds creating depth perception
- Multiple highlight layers for realistic surface reflection
- Soft shadows for depth and dimensionality
- Subtle border effects for edge definition

```mermaid
graph LR
SPHERE["3D Sphere Effect"] --> GRADIENT["Radial Gradient<br/>Background"]
SPHERE --> HIGHLIGHT["Highlight Layer<br/>Soft Glow"]
SPHERE --> SHADOW["Shadow Layer<br/>Depth Effect"]
SPHERE --> BORDER["Border Layer<br/>Edge Definition"]
SPHERE --> EMOJI["Emoji Overlay<br/>Theme-specific"]
```

**Diagram sources**
- [index.html:64-93](file://app/src/main/assets/index.html#L64-L93)

### Advanced Ball Styling Implementation
The enhanced ball rendering includes:
- Multi-layered gradient backgrounds for realistic appearance
- Multiple highlight effects for surface reflection simulation
- Soft inner and outer shadows for depth perception
- Emoji overlays for themed visual representation
- Responsive sizing based on ball dimensions

Key implementation references:
- [Ball styling:64-93](file://app/src/main/assets/index.html#L64-L93)
- [Emoji overlay:780-789](file://app/src/main/assets/index.html#L780-L789)
- [Light/dark adjustment:822-834](file://app/src/main/assets/index.html#L822-L834)

**Section sources**
- [index.html:64-93](file://app/src/main/assets/index.html#L64-L93)
- [index.html:780-789](file://app/src/main/assets/index.html#L780-L789)
- [index.html:822-834](file://app/src/main/assets/index.html#L822-L834)

### Tube Styling with Glow Effects
Each theme provides distinct tube styling with glow effects:
- Theme-specific border colors for visual identity
- Custom glow effects for enhanced visual appeal
- Consistent styling across all tube elements
- Dynamic application based on current theme

References:
- [Tube styling:25-58](file://app/src/main/assets/index.html#L25-L58)
- [Theme tube styling:757-760](file://app/src/main/assets/index.html#L757-L760)
- [Preview tube styling:1129-1132](file://app/src/main/assets/index.html#L1129-L1132)

**Section sources**
- [index.html:25-58](file://app/src/main/assets/index.html#L25-L58)
- [index.html:757-760](file://app/src/main/assets/index.html#L757-L760)
- [index.html:1129-1132](file://app/src/main/assets/index.html#L1129-L1132)

## Dependency Analysis
- Android layer depends on WebView, AdMob SDK, and window insets for immersive UI
- WebView loads index.html and exposes a JavaScript interface to Android
- index.html depends on Tailwind CDN for utility classes and defines all game logic, styles, and theme system
- **New**: Theme system depends on localStorage for persistence and state management

```mermaid
graph LR
AND["Android MainActivity.kt"] --> WV["WebView"]
WV --> HTML["index.html"]
HTML --> CSS["Tailwind + Inline Styles"]
HTML --> JS["Game Logic + Animations"]
HTML --> THEME["Theme System<br/>5 Visual Themes"]
HTML --> CAN["Canvas Particles"]
AND --> ADS["AdMob SDK"]
THEME --> STORAGE["Local Storage<br/>Persistence"]
```

**Diagram sources**
- [MainActivity.kt:66-135](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L66-L135)
- [index.html:7](file://app/src/main/assets/index.html#L7)
- [AndroidManifest.xml:20-28](file://app/src/main/AndroidManifest.xml#L20-L28)
- [index.html:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)

**Section sources**
- [MainActivity.kt:66-135](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L66-L135)
- [index.html:7](file://app/src/main/assets/index.html#L7)
- [AndroidManifest.xml:20-28](file://app/src/main/AndroidManifest.xml#L20-L28)
- [index.html:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)

## Performance Considerations
- Minimize DOM writes: batch updates by calling renderTubes() once per interaction
- Debounce input: the delegated handler prevents rapid duplicate actions
- Resize throttling: a short timeout avoids excessive re-layouts during device rotation
- Limit particle count: particle decay and removal ensure steady-state performance
- CSS transforms and GPU acceleration: animations leverage hardware-accelerated properties
- Avoid heavy JS computations: dimension calculations are O(1) per render pass
- **New**: Theme switching performance: cached theme data and efficient DOM updates
- **New**: Emoji rendering optimization: conditional emoji overlays only when available

Practical tips:
- Disable animations and particles when not needed to reduce overhead
- Keep tube counts reasonable for older devices
- Use requestAnimationFrame for continuous particle updates
- **New**: Consider theme complexity for performance-critical devices

## Troubleshooting Guide
Common issues and resolutions:
- Overlapping tubes on small screens
  - Cause: insufficient gap or too many columns
  - Fix: verify gap calculation and column logic; ensure minimum ball size constraints
  - References: [gap calc](file://app/src/main/assets/index.html#L582), [columns:563-564](file://app/src/main/assets/index.html#L563-L564)

- Invalid move feedback not triggering
  - Cause: rapid clicks or event timing
  - Fix: confirm debounce logic and processing flag
  - References: [delegation:664-689](file://app/src/main/assets/index.html#L664-L689)

- Animations not playing
  - Cause: disabled animations setting or missing class toggles
  - Fix: check state.animEnabled and verify class additions/removals
  - References: [animations:744-749](file://app/src/main/assets/index.html#L744-L749), [settings:1032-1038](file://app/src/main/assets/index.html#L1032-L1038)

- Particles not visible
  - Cause: particles removed quickly or disabled
  - Fix: verify particleEnabled and decay logic
  - References: [particles:436-469](file://app/src/main/assets/index.html#L436-L469)

- Ads not showing
  - Cause: ad not ready or initialization issues
  - Fix: check interstitial preloading and callbacks
  - References: [AdMob setup:370-409](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L370-L409)

- **New**: Theme not persisting
  - Cause: localStorage not available or blocked
  - Fix: verify localStorage availability and check browser settings
  - References: [theme persistence:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)

- **New**: Theme switching issues
  - Cause: theme data corruption or invalid theme keys
  - Fix: validate theme configuration and check console for errors
  - References: [theme validation:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)

- **New**: Emoji not displaying
  - Cause: unsupported emoji or font rendering issues
  - Fix: verify emoji support and fallback to colored circles
  - References: [emoji fallback:780-789](file://app/src/main/assets/index.html#L780-L789)

**Section sources**
- [index.html:582](file://app/src/main/assets/index.html#L582)
- [index.html:563-564](file://app/src/main/assets/index.html#L563-L564)
- [index.html:664-689](file://app/src/main/assets/index.html#L664-L689)
- [index.html:744-749](file://app/src/main/assets/index.html#L744-L749)
- [index.html:1032-1038](file://app/src/main/assets/index.html#L1032-L1038)
- [index.html:436-469](file://app/src/main/assets/index.html#L436-L469)
- [MainActivity.kt:370-409](file://app/src/main/java/com/cktechhub/games/MainActivity.kt#L370-L409)
- [index.html:1276-1278](file://app/src/main/assets/index.html#L1276-L1278)
- [index.html:780-789](file://app/src/main/assets/index.html#L780-L789)

## Conclusion
The rendering and UI system combines a responsive layout algorithm, efficient event delegation, and a lightweight animation pipeline to deliver smooth gameplay across devices. The comprehensive theme system with 5 visual themes (fruits, veggies, gems, candy, neon) provides rich visual customization while maintaining performance. Enhanced 3D sphere rendering with realistic lighting effects and emoji overlays creates an immersive gaming experience. By leveraging CSS animations, a single delegated event handler, adaptive sizing, and theme persistence, the system remains performant while providing extensive visual customization. For large tube counts or older devices, consider disabling animations and particles, and monitor resize handling to maintain responsiveness. The theme system ensures consistent visual identity across all game elements while providing users with meaningful customization options.