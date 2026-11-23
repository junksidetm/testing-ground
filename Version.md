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


---

### [Feature & CI/CD Pipeline: Material You Icon Pack Modding & Cloud Signing Engine] - 2026-11-20 02:38:00 IST
- **Status:** Completed (100%)
- **Target Architecture:** Android Icon Pack (Adaptive Icons + Monochrome Themed Icons, API 26-35)
- **Target Under Analysis:** `Pix Material You Light Dark v6.1.6 (Patched).apk` (`com.pashapuma.pix.material.you.dark`)
- **Modding Analysis & Reverse Engineering Findings:**
  - Deconstructed 55,953-entry archive and reverse-engineered CandyBar dashboard integration, adaptive icon binary XML structures, and Monet dynamic tinting filters (`PorterDuff.SRC_IN`).
  - Identified multi-tone layered shading technique: 1-color black alpha masks (`#000000` with varying alpha levels) dynamically recolored by Android's GPU without hardcoded palette values.
  - Resolved launcher mapping schema across `assets/appfilter.xml` and `assets/drawable.xml`.
- **Custom Icon Assets Engineered:**
  - Engineered 7 custom icons across SVG vector masters, 432x432 px transparent PNG foregrounds, and `<adaptive-icon>` XML definitions:
    1. *Wallet*: `com.mrdarksidetm.wallet` -> `wallet_dym`
    2. *Jeevan Pramaan*: `com.aadhar.life` -> `jeevan_pramaan_dym`
    3. *Suvidha Bihar*: `com.Suvidha_Bihar` -> `suvidha_bihar_dym` (Sanitized white canvas background to pure alpha transparency while preserving 24% card opacity)
    4. *PixelLab*: `com.imaginstudio.imagetools.pixellab.morphe` -> `pixellab_dym`
    5. *Bihar Bijli Smart Meter*: `com.dew.scm.eesl` -> `bihar_bijli_dym` (Vectorized clean contour)
    6. *Zave*: `com.zave.penny` -> `zave_dym`
    7. *June*: `com.denser.june` -> `june_dym` (Extracted and scaled official monochrome vector from upstream repository `DenserMeerkat/June`)
- **Components & CI/CD Implementation:**
  - `tools/iconpack-mod/patch_apk.py`: In-memory archive stream patcher injecting PNGs, XMLs, and launcher bindings directly without decompressing 56,000 files, strictly respecting the 4 GB RAM host mandate.
  - `tools/iconpack-mod/custom_icons/`: Complete asset hierarchy housing master SVGs, 432x432 xxxhdpi PNGs, adaptive XMLs, and XML patch snippets.
  - `.github/workflows/build-iconpack.yml`: Production cloud build, 4-byte page zipalign (`zipalign -p -f -v 4`), and cryptographic multi-scheme signing pipeline (`apksigner` v1, v2, v3). Releases production-signed APK artifacts with zero local memory overhead.


---

### [Release: Material You Icon Pack v6.1.7] - 2026-11-20 09:44:00 IST
- **Status:** Release Prepared (100%)
- **Release Tag:** `v6.1.7`
- **Release Title:** `v6.1.7`
- **Architecture:** Android Icon Pack (Adaptive Icons + Monochrome Themed Icons)
- **Deliverables:**
  - `Pix-Material-You-v6.1.7-Modded.apk`: Single production-signed APK asset containing 7 verified custom icons with complete AAPT2 `resources.arsc` indexing.
  - Base APK inheritance: Integrated baseline pulling from `v6.1.6` with automated release promotion.
- **Workflow Improvements:**
  - Automated version bumping (`versionName: 6.1.7`, `versionCode: 77`) in `apktool.yml` to support in-place Android OS updates.
  - Enforced single-asset release policy: Purges any intermediate artifacts so users only ever see the final APK.


---

### [Architecture Evolution: Multi-App Hub & Universal Release Engine] - 2026-11-21 15:20:00 IST
- **Status:** Completed (100%)
- **Target Architecture:** Android Multi-Application Monorepo & Universal Release Hub
- **Core Principles & Mandates:**
  - **Multi-App Monorepo Standard:** `testing-ground` is designated as an Android multi-application innovation and reverse-engineering hub hosting multiple independent apps and toolchains.
  - **Independent CI/CD Pipelines per App:** Every application maintains its own dedicated GitHub Actions suite:
    1. *Analyzer & Tester*: Automated lint checks and unit tests triggered specifically on file path changes for that app.
    2. *Production APK Builder & Signer*: Remote-first builds producing 4-byte page-aligned, v1/v2/v3 cryptographically signed production APKs without exhausting local host memory (strictly adhering to the 4 GB RAM limit).
  - **Universal Multi-App Release & Asset Coexistence:**
    - Releases use strict semantic version tagging (`v<Version>`).
    - Multiple application APKs coexist within unified release tags without destructive cross-asset purging.
    - If an application is not updated in a newly cut global release tag, the repository distribution index preserves and displays the direct download link to that app's latest stable release.
  - **README-First Direct Distribution Hub:**
    - Replaced release-tab navigation friction with a centralized, user-centric **App Directory & Direct Download Matrix** in `README.md`.
    - Every app features a permanent, direct one-click APK download link, version badge, and CI status indicator.
- **Components & Workflows Implemented:**
  - `README.md`: Centralized app store showcase, direct APK download matrix, feature breakdowns, and architecture documentation.
  - `.github/workflows/verify-longshot-pdf.yml`: Dedicated Android Lint and Unit Test verification pipeline for the Long Shot PDF Engine (`app/`).
  - `.github/workflows/build-longshot-pdf.yml`: Production APK builder, keystore generation, zipalign, and multi-scheme signing pipeline for Long Shot PDF (`LongShot-PDF-<tag>.apk`).
  - `.github/workflows/build-all-apps.yml`: Universal multi-app orchestrator facilitating concurrent builds and release attachments.
  - `.github/workflows/build-iconpack.yml`: Refactored release publishing step to eliminate destructive asset purging, ensuring multi-app artifact coexistence.


---

### [CI/CD Fix: Gradle Wrapper Bootstrap in Long Shot PDF Builder] - 2026-11-21 15:32:00 IST
- **Status:** Completed (100%)
- **Problem Diagnosed:** Remote GitHub Actions builder failed (`ClassNotFoundException: :app:assembleRelease`) due to missing `gradle/wrapper/gradle-wrapper.jar` binary in repository.
- **Remediation:** Injected explicit `gradle wrapper --gradle-version 8.10.2` bootstrap generation step across `build-longshot-pdf.yml` and `verify-longshot-pdf.yml`, ensuring deterministic wrapper binaries prior to invoking `./gradlew`.


---

### [CI/CD Fix: Release Signing Fallback Decoupling] - 2026-11-21 15:36:00 IST
- **Status:** Completed (100%)
- **Problem Diagnosed:** `:app:validateSigningRelease` failed on remote runner due to `initWith(getByName("debug"))` referencing missing `debug.keystore`.
- **Remediation:** Surgically decoupled signing config validation in `app/build.gradle.kts`. When no explicit keystore file is provided to Gradle, release compilation outputs an unsigned APK, allowing downstream `apksigner` in GitHub Actions to handle page alignment and v1/v2/v3 signing cleanly.


---

### [Resource Fix: Adaptive Icon Vector Assets] - 2026-11-21 15:42:00 IST
- **Status:** Completed (100%)
- **Problem Diagnosed:** `:app:processReleaseResources` failed AAPT2 resource linking due to missing `drawable/ic_launcher_background` and `drawable/ic_launcher_foreground` referenced by `mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`.
- **Remediation:** Added modern Material 3 vector assets in `app/src/main/res/drawable/` providing clean dark-themed adaptive background and cyan/purple document foreground contours.


---

### [Bug Fix: FileProvider Configured Root for Generated PDFs] - 2026-11-21 16:02:00 IST
- **Status:** Completed (100%)
- **Target App:** Long Shot PDF Engine (`v1.0.1`, `versionCode: 2`)
- **Problem Diagnosed:** Runtime conversion crash with `IllegalArgumentException: Failed to find configured root that contains /data/user/0/com.testingground.longshotpdf/files/documents/...` when generating single-page seamless PDFs. `res/xml/file_paths.xml` omitted `<files-path>` for internal files storage.
- **Remediation:**
  - Injected `<files-path name="internal_files" path="." />` and `<root-path name="root" path="." />` in `app/src/main/res/xml/file_paths.xml`, providing universal root coverage for internal app storage, external storage, and vendor symlink mounts.
  - Bumped version to `v1.0.1` (`versionCode: 2`).
  - Updated `README.md` Direct Download link to point to `v1.0.1`.


---

### [Asset & Icon Integration: Custom Adaptive Icon Master] - 2026-11-21 16:36:00 IST
- **Status:** Completed (100%)
- **Target App:** Long Shot PDF Engine (`v1.0.2`, `versionCode: 3`)
- **Deliverables:**
  - Integrated official master SVG asset at `app/ic_launcher.svg`, engineered with strict 66px inner safe area constraints.
  - Deconstructed into Android Adaptive Icon VectorDrawables:
    - `app/src/main/res/drawable/ic_launcher_background.xml`: Precision linear gradient from cyan-blue `#2A9BF5` to deep cobalt `#005A9E`.
    - `app/src/main/res/drawable/ic_launcher_foreground.xml`: Rendered phone silhouette, thumbnail cards, typography paths, and white framing brackets matching the master artwork.
  - Bumped version to `v1.0.2` (`versionCode: 3`).
  - Updated `README.md` catalog to point to `v1.0.2`.


---

### [Rebranding & Application ID Update: LongShotPDF] - 2026-11-21 16:44:00 IST
- **Status:** Completed (100%)
- **Target App:** LongShotPDF (`v1.0.3`, `versionCode: 4`)
- **Deliverables:**
  - Updated application name string to `LongShotPDF` in `app/src/main/res/values/strings.xml`.
  - Updated Android `applicationId` and `namespace` to `com.mrdarksidetm.longshotpdf` in `app/build.gradle.kts`.
  - Refactored Kotlin package directory structure to `app/src/main/kotlin/com/mrdarksidetm/longshotpdf` across domain, UI, theme, and viewmodel layers.
  - Bumped version to `v1.0.3` (`versionCode: 4`).
  - Updated `README.md` catalog to reflect `LongShotPDF` (`com.mrdarksidetm.longshotpdf`, `v1.0.3`).


---

### [Asset & Icon Refinement: Improved Visual Hierarchy & Contrast] - 2026-11-21 17:08:00 IST
- **Status:** Completed (100%)
- **Target App:** LongShotPDF (`v1.0.4`, `versionCode: 5`)
- **Deliverables:**
  - Integrated improved master SVG icon at `app/ic_launcher.svg`.
  - Refined Android Adaptive Icon foreground (`app/src/main/res/drawable/ic_launcher_foreground.xml`):
    - Removed illegible micro-typography in favor of a bolder visual hierarchy.
    - Enhanced top image card with vibrant `#758AEF` fill and crisp white icon accents.
    - Inverted middle-left card to full `#758AEF` with white content lines for striking tactile contrast.
  - Bumped version to `v1.0.4` (`versionCode: 5`).
  - Updated `README.md` catalog to point to `v1.0.4`.


---

### [Feature Implementation: Universal Material 3 Expressive About Page] - 2026-11-21 17:35:00 IST
- **Status:** Completed (100%)
- **Target App:** LongShotPDF (`v1.0.5`, `versionCode: 6`)
- **Deliverables:**
  - Implemented the official Universal About Page (`app/src/main/kotlin/com/mrdarksidetm/longshotpdf/ui/screens/AboutScreen.kt`) matching the M3 Expressive architecture standard:
    - **Hero Visualizer:** 44dp squircle container (`RoundedCornerShape(44.dp)`) with outline border, app logo (`app_logo.xml`) filling exactly 75% of space, animated M3 Expressive "Bun" shape (top-left, ~14s reverse rotation), and animated M3 Expressive "9-Side Cookie" shape (bottom-right, ~32s rotation and scale).
    - **App Title & Dynamic Version Pill:** Displays `LongShotPDF` with version badge reading `BuildConfig.VERSION_NAME` and interactive 7-tap Easter egg triggering Developer Mode feedback.
    - **Developer Profile Card:** Features avatar (`developer.png`), name ("Abhijeet Yadav"), title ("Developer"), and tactile pill buttons for GitHub repository and Email.
    - **Community FOSS Connected Settings Group ("The Other's Way" from Pulse Studio):** 28dp rounded container, 2dp separation, position-aware adaptive corners, spring touch morphing, leading icons, and trailing chevron indicators for Update Check, GitHub Repository, M3 Guidelines, and In-App License Sheet.
    - **In-App License Modal Bottom Sheet:** Embedded Apache License 2.0 full text viewer.
    - **Footer:** Minimalist "Made with ❤️" attribution.
  - Connected navigation in `MainActivity.kt` and added About action icon to `HomeScreen.kt` TopAppBar with back-handler navigation support.
  - Bumped version to `v1.0.5` (`versionCode: 6`).
  - Updated `README.md` catalog to point to `v1.0.5`.


---

### [Visual Refinement: Official M3 Expressive Bun Shape] - 2026-11-22 19:50:00 IST
- **Status:** Completed (100%)
- **Target App:** LongShotPDF (`v1.0.5`, `versionCode: 6`)
- **Deliverables:**
  - Replaced the procedural Canvas approximation of the M3 Expressive "Bun" shape with the authentic official vector asset from `assests/Images/Expressive Shapes/android_drawable/shape_bun.xml`.
  - Added `app/src/main/res/drawable/shape_bun.xml` containing the exact Material 3 Expressive Bun path geometry.
  - Updated `AboutScreen.kt` hero visualizer to render `R.drawable.shape_bun` via `Icon` with dynamic tinting (`#0078D4`, 88% alpha), retaining ambient floating (`bunOffsetY`) and subtle rotation (`bunRotation`) motion physics.


---

### [Architecture Evolution: Folder-Specific Multi-App Testing Ground & Universal Release Pipeline] - 2026-11-22 20:30:00 IST
- **Status:** Completed (100%)
- **Target Scope:** Entire Repository / Multi-App Architecture (`LongShotPDF` & `Pix Material You Icon Pack Mod`)
- **Deliverables:**
  - **Folder-Specific App Isolation:** Restructured the repository into isolated, self-contained application workspaces:
    - `LongShotPDF/`: Contains all code, Android app module, assets, documentation (`docs/`), and standalone Gradle build wrappers for LongShotPDF. Root `settings.gradle.kts` dynamically points `:app` to `LongShotPDF/app`.
    - `Pix Material You Icon Pack Mod/`: Contains all modding scripts (`patch_apk.py`, `patch_decompiled.py`, `sanitize_for_aapt2.py`), custom adaptive vector drawables, mipmaps, and version tracking (`version.txt`). Retired legacy `tools/` folder.
  - **Repository Governance & Licensing:**
    - Created Apache License, Version 2.0 (`LICENSE`) at repository root.
    - Updated root `README.md` documenting testing-ground as an emerging application incubation lab where apps are tested and stabilized before graduating to their own independent repositories.
  - **Universal Multi-App Release Workflow (`build-all-apps.yml`):**
    - Implemented universal dual-app packaging: whenever a release is triggered, builds both `LongShot-PDF-v<version>.apk` and `Pix-Material-You-v<version>-Modded.apk`.
    - Supports dynamic release titling (e.g. `LongShotPDF updated to vX.X.X` or `Pix Material You Icon Pack updated to vX.X.X`).
    - Release notes explicitly identify which application was updated while bundling current stable builds of unchanged applications.
    - Standardized output artifact naming: `LongShot-PDF-v<version>.apk` and `Pix-Material-You-v<version>-Modded.apk`.
  - **CI/CD Alignment:** Updated `build-longshot-pdf.yml`, `verify-longshot-pdf.yml`, `build-iconpack.yml`, and `release.yml` with the new folder-specific paths.


---

### [Release v1.1.0: LongShotPDF Official Brand Logo, M3 Bun Shape & Universal About Screen] - 2026-11-23 20:40:00 IST
- **Status:** Completed (100% & Published Live)
- **Target Scope:** `LongShotPDF/`
- **Release:** `v1.1.0` (versionCode 7)
- **Release Portal:** [testing-ground v1.1.0 Release](https://github.com/junksidetm/testing-ground/releases/tag/v1.1.0)
- **Deliverables:**
  - `LongShot-PDF-v1.1.0.apk`: Production-signed release APK with v1, v2, v3 signature schemes and 4-byte zip alignment.
  - Ingested official brand logo `LongShotPdf - Logo.svg` in `LongShotPDF/app/` and `assests/Images/Logos/`.
  - Implemented the official Material 3 Expressive Bun shape (`shape_bun.xml`) and 9-side cookie shape (`shape_9_side_cookie.xml`) in `AboutScreen.kt`.
  - Bumped version to `1.1.0` in `LongShotPDF/app/build.gradle.kts`.
