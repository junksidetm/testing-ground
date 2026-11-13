# Version & Changelog History

## Project: Testing Ground
**Created:** 2026-11-13 13:58:00 IST
**Architecture:** Native Android (Kotlin + Jetpack Compose) & App Modding / Reverse Engineering Lab
**Design Language:** Material 3 Expressive
**Target Device Baseline:** Realme 11 5G (Android 13/14, ColorOS)

---

### [Initial Version] - 2026-11-13 13:58:00 IST
- **Status:** Initialized (100%)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 26 (Android 8.0)
- **Compile SDK:** 35
- **Libraries & Tools:**
  - Gradle: 8.9
  - Android Gradle Plugin: 8.7.2
  - Kotlin: 2.0.21
  - Jetpack Compose BOM: 2024.10.01
  - Material 3: 1.3.1
  - Coroutines: 1.9.0
- **Components & Modding Analysis:**
  - Repository Initialization: Initialized git workspace with cryptographic SSH commit signing (`id_ed25519_junksidetm`).
  - Target Under Analysis: `Long+Screenshot+Capture+&+Edit_1.7_APKPure.xapk` (`com.longscreenshotcapture.stitchingscreenshot.longshot.summerllc`).
  - Core Class Mapping & Forensic Deconstruction:
    - `LongScreenshotSplash`: Splash screen internet check lock identified (`ConnectivityManager.getActiveNetworkInfo().isConnected()`). Gated behind network check for AdMob initialization and Firebase Realtime Database.
    - `StartPageActivity`: Dashboard & permission orchestration (`READ_MEDIA_IMAGES`, `SYSTEM_ALERT_WINDOW`, `POST_NOTIFICATIONS`).
    - `ScreenshotViewService`: Foreground capture service utilizing `MediaProjection`, `VirtualDisplay`, `ImageReader`, and floating overlay (`WindowManager`).
    - `h3.i`: Canvas stitching algorithm compositing successive slice Bitmaps into a single master Bitmap.
    - `l3.e`: Pixel buffer acquisition and JPEG disk serialization.
    - `l3.f`: Floating overlay drag physics and touch event dispatch.
    - `ViewPageOnInternet`: Full web page DOM canvas screenshot capture via WebView.
    - `MyCreationActivity` & `MyScreenshotView`: Scoped storage gallery with PhotoView zoom and PDF export.
  - Architectural Modernization Blueprint: Designed complete Material 3 Expressive pure native Jetpack Compose architecture (100% offline, zero ads, zero telemetry, scoped MediaStore integration, M3 Expressive dynamic color palette for Realme 11 5G).


---

### [Documentation & Forensic Mapping] - 2026-11-13 14:02:00 IST
- **Status:** Completed (100%)
- **Components Documented:**
  - `docs/app-map-long-screenshot.md`: Complete architectural breakdown, opcode mapping, failure mode diagnosis, and modernization roadmap for Long Screenshot Capture & Edit (`com.longscreenshotcapture.stitchingscreenshot.longshot.summerllc`).
  - Identified hardcoded offline lockout mechanism inside `LongScreenshotSplash.onCreate`.
  - Mapped proprietary stitching logic in `h3.i` and frame pipeline in `l3.e` and `ScreenshotViewService`.
  - Formulated full Material 3 Expressive and offline-first replacement specification.


---

### [Feature Implementation: Long Shot PDF Engine] - 2026-11-13 14:30:00 IST
- **Status:** Completed (100%)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 26 (Android 8.0)
- **Compile SDK:** 35
- **Libraries & Architecture:**
  - Kotlin 2.0.21 + Jetpack Compose BOM 2024.10.01 + Material 3 1.3.1
  - StateFlow + collectAsStateWithLifecycle (UDF Architecture)
  - Pure Native Android Canvas & PdfDocument Pipeline (Zero third-party bloat)
- **Bloat Removal & Feature Pruning (Without Breaking Core Workflow):**
  - Completely eradicated: "My creation" gallery, separate "Share" activity, "Summer policy" & company tracking, Google Play referral icon/link, floating screen capture overlay service, and Web capture.
  - 100% Focused on the core workflow: **Long Screenshot -> Seamless Long PDF Conversion Engine**.
- **Display Insets & Scaling Rectification:**
  - Fixed status bar and gesture navigation bar clipping by implementing full edge-to-edge support with `enableEdgeToEdge()`, `Scaffold`, `statusBarsPadding()`, and `navigationBarsPadding()`.
  - Zero overlay on display cutout / camera punch hole on Realme 11 5G.
- **Components Implemented:**
  - `PdfConverterEngine`: Dual-mode conversion pipeline supporting:
    - *Seamless Single-Page Canvas*: Continuous dynamic height matching exact screenshot dimensions (zero page cuts, infinite scroll).
    - *Paginated A4 Canvas*: Clean margin slicing for standard physical prints.
    - OOM-safe bitmap decoding with sample size calculations for ultra-tall captures on 4GB RAM devices.
  - `PdfViewModel`: Reactive state manager handling single and batch screenshot ingestion, image dimension inspection, and asynchronous IO PDF rendering.
  - `HomeScreen`: M3 Expressive UI with 5-tier Surface Container hierarchy, tactile geometry, source image bento card, live progress feedback, and post-generation open/save sheet.
  - `MainActivity`: Single-activity entry point with incoming `ACTION_SEND` and `ACTION_SEND_MULTIPLE` intent ingestion.
  - CI/CD Pipelines: Added `.github/workflows/ci.yml` and `.github/workflows/release.yml` with semantic versioning tag alignment.
