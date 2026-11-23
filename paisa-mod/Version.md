# Project Version History - Paisa (Unrestricted Mod)

## [2026-11-23 19:47:41 IST] - Project Inception & Clean-Room Scaffolding
- **Action**: Initialized clean-room unrestricted testing-ground project in `D:\code\testing-ground\paisa-mod`.
- **Lineage & Architectural Foundations**:
  - Full reverse-engineered UI/UX design tokens and assets derived from Paisa (`dev.hemanths.paisa`).
  - Completely stripped of RevenueCat, Google Play Billing Client 8.3.0, and PairIP anti-tampering verification.
  - Zero artificial restrictions on Accounts, Budgets, Goals, Categories, Recurring payments, and Profiles.
- **Components Added**:
  - **Embedded Desktop Web Companion**: `WebCompanionService` (`lib/core/services/web_companion_service.dart`) serving the extracted Material 3 companion interface over local Wi-Fi on port 8080.
  - **Typography & Visual Tokens**: Authentic `GoogleSansFlex-Variable.ttf`, `MaterialSymbolsRounded.ttf`, and Material 3 Expressive surfaces.
  - **CI/CD Workflow**: Remote APK generation pipeline in `.github/workflows/build_apks.yml` (Universal, arm64-v8a, arm-v7a).
- **Status**: 100% (Clean Scaffolding & Unrestricted Architecture Established).
