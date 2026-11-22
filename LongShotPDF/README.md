# LongShotPDF

Seamless long screenshot to continuous dynamic-height and paginated A4 PDF generator for Android.

## Architecture
- **Language:** Kotlin
- **UI Framework:** Pure native Jetpack Compose adhering to Material 3 Expressive
- **State Management:** Android ViewModel + StateFlow
- **Core Engine:** Custom bitmap memory-safe downsampling pipeline and Android `PdfDocument` rendering
- **Sharing Ingestion:** Android `ACTION_SEND` and `ACTION_SEND_MULTIPLE` intent handlers
