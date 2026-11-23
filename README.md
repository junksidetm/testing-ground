<div align="Center">
<img src="assets\Testing Ground.svg" height="120"><br><br>
<h1>🧪 Testing Ground</h1>
</div>

A multi-application incubation, testing, and reverse-engineering hub for Android.

`testing-ground` serves as an active testing lab where emerging applications undergo rigorous development, linting, UI refinement, and CI/CD verification before graduating into their own dedicated standalone repositories.

## 📁 Hosted Applications & Folder Architecture

Each application in this repository is maintained inside its own self-contained folder:

### 1. [LongShotPDF/](LongShotPDF/)
* **Type:** Native Android Application
* **Framework:** Kotlin + Jetpack Compose (Material 3 Expressive)
* **Package ID:** `com.mrdarksidetm.longshotpdf`
* **Current Version:** `v1.0.5`
* **Artifact:** `LongShot-PDF-v1.0.5.apk`
* **Description:** Continuous single-page canvas and paginated A4 screenshot-to-PDF generation with memory-safe downsampling and native system sharing ingestion.

<p align="center">
<a href=""><img src="https://raw.githubusercontent.com/junksidetm/assests/d8774837b8c8658389ea37193a77a9a100414bc5/Images/badges/SVG%20-%20Version/Android%20Direct%20Link%20Frame.svg
" width=250>
</p>

### 2. [Pix Material You Icon Pack Mod/](Pix%20Material%20You%20Icon%20Pack%20Mod/)
* **Type:** Android Themed Icon Mod & Builder
* **Framework:** Android Adaptive / Monochrome Icons + AAPT2 / Apktool
* **Current Version:** `v6.1.7`
* **Artifact:** `Pix-Material-You-v6.1.7-Modded.apk`
* **Description:** Dynamic theming modding suite injecting custom vector and monochrome icons for unsupported regional apps.

<p align="center">
<a href=""><img src="https://raw.githubusercontent.com/junksidetm/assests/d8774837b8c8658389ea37193a77a9a100414bc5/Images/badges/SVG%20-%20Version/Android%20Direct%20Link%20Frame.svg
" width=250>
</p>

## 🚀 Universal Multi-App Release Model

This repository employs a unified multi-application delivery model:

1. **All-Inclusive Release Bundles:** Whenever a release is published, release assets include APKs for all hosted applications (`LongShot-PDF-vX.X.X.apk` and `Pix-Material-You-vX.X.X-Modded.apk`).
2. **Semantic Versioning per Application:** When changes are made to an application, its version bumps semantically. The application that was not modified retains its current stable version.
3. **Explicit Release Changelog:** Release announcements highlight which application received updates (e.g. `LongShotPDF updated to v1.0.6`) while maintaining the current stable build of other applications in the release assets.
4. **Standardized APK Naming:** APK outputs follow the strict naming convention:
   - `LongShot-PDF-v<Version>.apk`
   - `Pix-Material-You-v<Version>-Modded.apk`

## ➕ Adding New Applications to Testing Ground

Whenever a new app or experiment is introduced:
* It is placed inside its own dedicated folder (e.g., `AppName/`).
* It maintains folder-specific isolation (code, assets, and build definitions).
* It integrates into the multi-app release pipeline so every global release packages the newest version alongside all existing lab apps.

## 📄 License
This project is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
