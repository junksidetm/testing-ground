# Comprehensive Application Map & Forensic Analysis: Long Screenshot Capture & Edit

**Target Package:** `com.longscreenshotcapture.stitchingscreenshot.longshot.summerllc`  
**Version:** `1.7` (versionCode `8`)  
**SDK Targets:** minSdk `23` (Android 6.0), targetSdk `35` (Android 15)  
**Binary Analyzed:** `D:\code\temp\Long+Screenshot+Capture+&+Edit_1.7_APKPure.xapk`  
**Analysis Date:** 2026-11-13  

---

## 1. High-Level Architectural Anatomy

The application is structured into four primary layers:
1. **Foreground Screen Capture Pipeline (`MediaProjection` + `ImageReader` + `VirtualDisplay`)**
2. **Floating Overlay Interaction Engine (`WindowManager` + `SYSTEM_ALERT_WINDOW`)**
3. **Image Processing & Stitching Engine (`Canvas` + `Bitmap` composition in `h3.i`)**
4. **Intrusive Ad & Telemetry Subsystem (`AdMob` + `Firebase Realtime Database` + Connectivity Gatekeeper)**

Out of a 5.5 MB package and 5.1 MB `classes.dex`, only **13 classes** belong to the core application logic; over **90% of the binary** comprises third-party ad mediation SDKs (`com.google.android.gms.ads.*`), Firebase, AndroidX WorkManager, and legacy UI libraries.

---

## 2. Forensic Class & Component Map

### 2.1. Core Application Classes

| Class Name | Component Type | Responsibility & Implementation Details |
| :--- | :--- | :--- |
| `LongScreenshotApp` | `Application` | Subclasses `android.app.Application`. Initializes `MobileAds`, sets up Firebase app instance, binds shared preferences (`LongScreenshotInterstitialID`, `LongScreenshotBannerID`), and holds global static handlers `a()`, `b()`, `c()` to show fullscreen interstitial ads with a blocking `ProgressDialog` ("Ads Showing"). |
| `LongScreenshotSplash` | `Activity` (Launcher) | Entry splash screen. **Contains the critical offline lockout bug**. Executes network checks and halts execution if disconnected. Loads remote config from Firebase RTDB (`-default-rtdb.firebaseio.com`) and buffers interstitial ads before starting `StartPageActivity`. |
| `StartPageActivity` | `Activity` | Main dashboard. Manages runtime permission requests (`READ_MEDIA_IMAGES`, `POST_NOTIFICATIONS`, `SYSTEM_ALERT_WINDOW`, legacy `READ_EXTERNAL_STORAGE`). Renders dashboard with hardcoded image views for Long Shot, Web Capture, and My Creations. |
| `ScreenshotHomePage` | `Activity` | Configuration screen for the floating capture widget. Queries `ActivityManager.getRunningServices` to check if `ScreenshotViewService` is already active. Displays floating widget layout selector. |
| `ScreenshotActivity` | `Activity` | Headless trampoline activity. Requests screen capture token via `MediaProjectionManager.createScreenCaptureIntent()` and passes the resulting Intent to `ScreenshotViewService`. |
| `ScreenshotViewService` | `Service` (Foreground) | The heart of the capture subsystem. Starts a foreground service with notification, handles `MediaProjection`, creates a `VirtualDisplay`, attaches `ImageReader`, and adds floating bubble overlays to `WindowManager`. |
| `ScreenshotPage` | `Activity` | Stitching review activity. Reads temporary slice images saved by the service in cache directory `/Screenshot/`, displays the sequence in a `RecyclerView`, and triggers the stitching algorithm in `h3.i`. |
| `ScreenshotView` | `Activity` | Editor and preview screen for the final stitched image. Houses zoomable view (`PhotoView`), crop tools, drawing canvas, text overlay, and export to external storage (`/Summer Peaches LLC/Long Screenshot Capture Edit/`). |
| `MyCreationActivity` | `Activity` | Gallery screen. Scans the local filesystem directory for `.jpg`, `.png`, `.jpeg` files and displays them in a grid `RecyclerView`. |
| `MyScreenshotView` | `Activity` | Full-screen image viewer for previously saved creations. Provides Share, Delete, and "Convert to PDF" buttons. |
| `PdfCreationActivity` | `Activity` | Document generator. Converts stitched image bitmaps into multi-page or single-page PDF files stored in `/Summer Peaches LLC/Documents/`. |
| `ViewPageOnInternet` | `Activity` | Mini built-in browser (`WebView`). Loads URLs (default: `http://google.com`), injects JavaScript, and captures full-length webpage screenshots by drawing the entire WebView DOM into an offscreen canvas. |
| `SummerPolicyActivity` | `Activity` | Privacy Policy screen displaying remote HTML within a `WebView`. Also contains an offline toast check. |
| `SummerExitActivity` | `Activity` | Exit confirmation popup with a forced exit interstitial ad. |

---

### 2.2. Obfuscated Engine Classes (`h3`, `l3`, `c3`)

Through binary inspection of `classes.dex`, the proprietary engine classes were mapped as follows:

| Class | Functional Role | Detailed Mechanics |
| :--- | :--- | :--- |
| `h3.i` | **Stitching Engine** | Takes an `ArrayList<Bitmap>` of screen slices. Computes `totalHeight = sum(bmps.height)`. Allocates a master bitmap `Bitmap.createBitmap(width, totalHeight, ARGB_8888)`. Creates `Canvas(masterBitmap)`. Loops through slices and calls `canvas.drawBitmap(bmp, 0, currentY, paint)` with vertical offsets. Enforces rule: *"At least Add Two Photo"*. |
| `l3.e` | **Frame Acquisition Worker** | Implements `ImageReader.OnImageAvailableListener`. Extracts latest `Image`, reads pixel buffer from `Planes[0]`, handles row stride vs pixel stride alignment: `width + (rowStride - pixelStride * width) / pixelStride`, creates Bitmap, compresses to `tmp.jpg` (`dd_MM_yy_hh_mm_ss.jpg`) in internal storage, and calls `bitmap.recycle()`. |
| `l3.f` | **Floating Bubble Physics** | Implements `View.OnTouchListener` for the floating overlay. Manages `WindowManager.LayoutParams.x/y` coordinates during `ACTION_DOWN`, `ACTION_MOVE`, and `ACTION_UP` to allow free-form dragging on screen. |
| `l3.g` | **Orientation Observer** | Extends `OrientationEventListener`. Observes rotation changes; upon screen rotation, invalidates and re-creates the `VirtualDisplay` to match new screen dimensions. |
| `l3.d` | **Projection Lifecycle** | Extends `MediaProjection.Callback`. Safely releases the `VirtualDisplay` when screen capture is revoked or stopped. |
| `l3.a` | **PDF Storage Engine** | Handles writing PDF document streams to `/Summer Peaches LLC/Documents/`. |
| `c3.c` / `c3.a` | **RecyclerView Adapters** | Adapters for captured image strips and thumbnail previews. |

---

## 3. Why It Doesn't Work (Failure Modes & Flaws)

### 3.1. The Offline Lockout Bug (Dark Pattern / Anti-User Gate)
- **Code Path in `LongScreenshotSplash.onCreate`:**
  ```java
  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
  boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
  if (!isConnected) {
      Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
      // Execution halts here; does NOT start StartPageActivity!
      return;
  }
  ```
- **Consequence:** If mobile data or Wi-Fi is disabled, or if the user revokes network permission/blocks internet via a firewall, the application displays a toast and freezes indefinitely on a blank splash screen.
- **Root Cause:** The developers intentionally blocked app execution until AdMob can download ad units and Firebase can download tracking parameters. Local screenshot capture and stitching require **zero network access**, making this restriction artificial and anti-user.

### 3.2. Ad SDK Bloat & Intrusive Interstitials
- The app binds interstitial ads to:
  - App launch (`LongScreenshotSplash`)
  - Home screen navigation (`ScreenshotHomePage.onClick`)
  - Screenshot saving (`ScreenshotView`)
  - App exit (`SummerExitActivity`)
- It blocks the UI thread with a `ProgressDialog` displaying "Ads Showing".

### 3.3. Scoped Storage Breakdown on Android 11–15
- The app hardcodes paths like `/Summer Peaches LLC/Long Screenshot Capture Edit/` on external shared storage and relies on deprecated `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE`.
- On modern Android versions (Android 11 through 15), writing directly to arbitrary root folders on external storage throws `SecurityException` unless broad `MANAGE_EXTERNAL_STORAGE` is granted, which Google Play rejects for screenshot apps.
- The idiomatic approach must use `MediaStore.Images.Media` or the standard `Environment.DIRECTORY_SCREENSHOTS`.

### 3.4. Non-Responsive, Outdated UI
- Start page and dialogs use pre-baked static image assets (`ImageView` with hardcoded button graphics) rather than scalable vector drawables or Material 3 components.
- Lacks dark mode contrast adaptability, dynamic color support, and predictive back animations.

---

## 4. Modernization & Material 3 Expressive Blueprint

For the user's **Realme 11 5G (Android 13/14, ColorOS)**:

### 4.1. Core Architectural Replacement (Pure Native Compose)
1. **100% Offline Manifest:**
   - Remove `android.permission.INTERNET`, `ACCESS_NETWORK_STATE`, and all AdServices/AdMob permissions.
   - Keep only:
     - `android.permission.FOREGROUND_SERVICE`
     - `android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION`
     - `android.permission.SYSTEM_ALERT_WINDOW`
     - `android.permission.POST_NOTIFICATIONS`
2. **Material 3 Expressive UI:**
   - Full support for **Dynamic Color** (harmonizes with Realme ColorOS Material You wallpaper palette).
   - Expressive 5-tier Surface Container hierarchy (`surfaceContainerLowest` through `surfaceContainerHighest`).
   - Floating Action Buttons (Expressive Large FAB), Navigation Rails / Bottom Navigation Bar.
   - Fluid spring physics for the floating capture bubble.
3. **Modern MediaStore Storage:**
   - Write directly to `MediaStore.Images.Media.EXTERNAL_CONTENT_URI` under `RELATIVE_PATH = "Pictures/Screenshots"` or `"Pictures/LongScreenshots"`.
   - Zero storage permissions needed on Android 10+!
4. **Enhanced Stitching Engine:**
   - High-performance Kotlin Coroutine + Canvas stitching pipeline.
   - Dynamic memory management with `inBitmap` reuse to prevent OutOfMemory crashes on 4GB RAM devices when stitching 10+ sequential full-resolution screen slices.
