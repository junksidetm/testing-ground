# Pix Material You Icon Pack Mod

Modding engine and build pipeline for generating custom adaptive and themed icons for regional applications.

## Architecture & Tools
- **Decompilation & Recompilation:** `Apktool` + `AAPT2`
- **Dynamic Theming:** Monochrome and adaptive vector assets matching Android Material You GPU color extraction
- **Patch Pipeline:** Python scripts (`patch_decompiled.py`, `sanitize_for_aapt2.py`, `patch_apk.py`)
- **Version Tracking:** `version.txt`
