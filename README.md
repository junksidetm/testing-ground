# 🧪 Testing Ground

A multi-application innovation and reverse-engineering hub for Android, featuring pure native Jetpack Compose architectures, Material 3 Expressive design, and advanced Android OS modding pipelines.

---

## 📱 App Directory & Direct Download Catalog

> [!TIP]
> **No Release Hunting Required:** You do not need to browse GitHub Releases or dig through tag assets. Every application hosted in this repository provides a direct, one-click download link to its latest verified, production-signed APK below.

| Application | Architecture / Stack | Current Version | Release Tag | Direct APK Download | CI / Build Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LongShotPDF** | Kotlin + Jetpack Compose + M3 Expressive | `v1.0.3` | [`v1.0.3`](https://github.com/junksidetm/testing-ground/releases/tag/v1.0.3) | [📥 Download LongShot-PDF APK](https://github.com/junksidetm/testing-ground/releases/download/v1.0.3/LongShot-PDF-v1.0.3.apk) | [![Long Shot CI](https://github.com/junksidetm/testing-ground/actions/workflows/verify-longshot-pdf.yml/badge.svg)](https://github.com/junksidetm/testing-ground/actions/workflows/verify-longshot-pdf.yml) |
| **Pix Material You Icon Pack** | Android Adaptive & Themed Icons + AAPT2 | `v6.1.7` | [`v6.1.7`](https://github.com/junksidetm/testing-ground/releases/tag/v6.1.7) | [📥 Download Pix Material You APK](https://github.com/junksidetm/testing-ground/releases/download/v6.1.7/Pix-Material-You-v6.1.7-Modded.apk) | [![Icon Pack Builder](https://github.com/junksidetm/testing-ground/actions/workflows/build-iconpack.yml/badge.svg)](https://github.com/junksidetm/testing-ground/actions/workflows/build-iconpack.yml) |

---

## 🏷️ Universal Multi-App Release Model

This repository operates as an **independent multi-application monorepo**:

* **Universal Release Tags (`v<Version>`):** When a global release is published, it bundles the production-signed APKs for all applications that received updates during that cycle.
* **Non-Destructive Coexistence:** Release workflows preserve all existing assets within a tag, allowing multiple distinct APK deliverables to live side-by-side.
* **Independent App Version Tracking:** If an application was **not** updated in the latest global release tag, the [App Directory Table](#-app-directory--direct-download-catalog) above always maintains the direct download link pointing to that application's latest stable release.
* **Zero Bloat & Direct Access:** Users can grab the exact APK they need without navigating through unrelated artifacts.

---

## 🛠️ Hosted Applications

### 1. LongShotPDF (`app/`)
* **Path:** [`app/src/main/kotlin/com/mrdarksidetm/longshotpdf`](file:///D:/code/testing-ground/app/src/main/kotlin/com/mrdarksidetm/longshotpdf)
* **Status:** Stable (100%)
* **Package / App ID:** `com.mrdarksidetm.longshotpdf`
* **Features:**
  * **Seamless Single-Page Canvas:** Ingests ultra-tall panoramic screenshots and renders a continuous dynamic-height PDF with **zero page cuts or seams**.
  * **Paginated A4 Canvas Mode:** Optional page slicing engine for clean physical print jobs.
  * **OOM-Safe Bitmap Pipeline:** Includes sample-size calculations to prevent memory exhaustion on low-memory (4 GB RAM) baseline hardware.
  * **Pure Native Jetpack Compose:** Material 3 Expressive 5-tier Surface Container hierarchy, tactile cards, and edge-to-edge insets (`enableEdgeToEdge()`, `statusBarsPadding()`, `navigationBarsPadding()`) preventing UI clipping.
  * **System Sharing Ingestion:** Directly accepts screenshots via Android `ACTION_SEND` and `ACTION_SEND_MULTIPLE` intent filters.

### 2. Pix Material You Icon Pack Mod (`tools/iconpack-mod/`)
* **Path:** [`tools/iconpack-mod`](file:///D:/code/testing-ground/tools/iconpack-mod)
* **Status:** Production Release (`v6.1.7`)
* **Features:**
  * **Monet Dynamic Theming:** Injects 7 handcrafted adaptive vector and monochrome icons for unsupported regional apps (*Wallet, Jeevan Pramaan, Suvidha Bihar, PixelLab, Bihar Bijli Smart Meter, Zave, June*).
  * **Multi-Tone Alpha Shading:** Utilizes 1-color black alpha masks recolored dynamically by the Android GPU.
  * **Apktool + AAPT2 Rebuild Pipeline:** Full resource recompilation and `resources.arsc` regeneration.
  * **Automated Version Bumping:** Seamless in-place updates on target devices.

---

## 🚀 GitHub Actions CI/CD Architecture

Every application maintains its own dedicated CI/CD suite under [`.github/workflows/`](file:///D:/code/testing-ground/.github/workflows):

| Workflow | Target Application | Purpose | Triggers |
| :--- | :--- | :--- | :--- |
| [`verify-longshot-pdf.yml`](file:///D:/code/testing-ground/.github/workflows/verify-longshot-pdf.yml) | Long Shot PDF | Android Lint check and Unit Test verification | Push / PR on `app/**`, `workflow_dispatch` |
| [`build-longshot-pdf.yml`](file:///D:/code/testing-ground/.github/workflows/build-longshot-pdf.yml) | Long Shot PDF | Builds release APK, 4-byte zipaligns, signs with v1/v2/v3 keys, and attaches to release | Release published, `workflow_dispatch`, reusable `workflow_call` |
| [`build-iconpack.yml`](file:///D:/code/testing-ground/.github/workflows/build-iconpack.yml) | Icon Pack Mod | Rebuilds resources via AAPT2, signs, and attaches modded APK to release | `workflow_dispatch`, reusable `workflow_call` |
| [`build-all-apps.yml`](file:///D:/code/testing-ground/.github/workflows/build-all-apps.yml) | Universal Hub | Orchestrates concurrent multi-app builds into a unified release tag | `workflow_dispatch` |

> [!IMPORTANT]
> **Host Resource Protection Mandate:** All binary compilation, resource assembling, and APK signing are executed **remotely via GitHub Actions**. Local execution of heavy build commands (`./gradlew assembleDebug`, `assembleRelease`) is strictly prohibited to safeguard local system memory (4 GB RAM constraint).

---

## 📜 Version History & Changelog
For the complete immutable history of updates, reverse-engineering deconstructions, and architectural milestones, consult [Version.md](file:///D:/code/testing-ground/Version.md).
