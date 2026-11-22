#!/usr/bin/env python3
"""
Surgical APK Ingestion & Modding Pipeline for Pix Material You Icon Pack.
Injects custom PNG foregrounds, adaptive XML definitions, and launcher mappings
without decompressing the entire 56,000-file archive, preserving system memory.
"""

import sys
import os
import zipfile

def patch_apk(input_apk, output_apk, custom_icons_dir):
    print(f"Opening base APK: {input_apk}")
    print(f"Target output: {output_apk}")
    print(f"Asset source: {custom_icons_dir}")

    # Read addition snippets
    appfilter_additions_path = os.path.join(custom_icons_dir, "appfilter_additions.xml")
    drawable_additions_path = os.path.join(custom_icons_dir, "drawable_additions.xml")

    with open(appfilter_additions_path, "r", encoding="utf-8") as f:
        appfilter_add = f.read().strip()

    with open(drawable_additions_path, "r", encoding="utf-8") as f:
        drawable_add = f.read().strip()

    # Discover custom PNGs and XMLs to inject
    png_dir = os.path.join(custom_icons_dir, "res", "mipmap-xxxhdpi-v4")
    xml_dir = os.path.join(custom_icons_dir, "res", "drawable-anydpi-v26")

    custom_pngs = {}
    if os.path.exists(png_dir):
        for fn in os.listdir(png_dir):
            if fn.endswith(".png"):
                custom_pngs[f"res/mipmap-xxxhdpi-v4/{fn}"] = os.path.join(png_dir, fn)

    custom_xmls = {}
    if os.path.exists(xml_dir):
        for fn in os.listdir(xml_dir):
            if fn.endswith(".xml"):
                custom_xmls[f"res/drawable-anydpi-v26/{fn}"] = os.path.join(xml_dir, fn)

    injected_paths = set(list(custom_pngs.keys()) + list(custom_xmls.keys()))

    with zipfile.ZipFile(input_apk, "r") as zin, zipfile.ZipFile(output_apk, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        total = len(zin.infolist())
        print(f"Processing {total} archive entries...")

        for item in zin.infolist():
            # Skip old signature files
            if item.filename.startswith("META-INF/") and (
                item.filename.endswith(".SF") or
                item.filename.endswith(".RSA") or
                item.filename.endswith(".DSA") or
                item.filename.endswith(".EC") or
                item.filename.endswith("MANIFEST.MF")
            ):
                continue

            # Skip files that we are explicitly overriding
            if item.filename in injected_paths:
                continue

            # Modify assets/appfilter.xml
            if item.filename == "assets/appfilter.xml":
                content = zin.read(item.filename).decode("utf-8")
                # Insert additions before </resources>
                if "</resources>" in content:
                    content = content.replace("</resources>", f"\n\n\t{appfilter_add}\n</resources>")
                else:
                    content += f"\n\n{appfilter_add}\n"
                zout.writestr("assets/appfilter.xml", content.encode("utf-8"))
                print("  ✓ Patched assets/appfilter.xml")
                continue

            # Modify assets/drawable.xml
            if item.filename == "assets/drawable.xml":
                content = zin.read(item.filename).decode("utf-8")
                if "</resources>" in content:
                    content = content.replace("</resources>", f"\n\n\t{drawable_add}\n</resources>")
                else:
                    content += f"\n\n{drawable_add}\n"
                zout.writestr("assets/drawable.xml", content.encode("utf-8"))
                print("  ✓ Patched assets/drawable.xml")
                continue

            # Copy all other entries directly
            data = zin.read(item.filename)
            zout.writestr(item, data)

        # Inject custom PNGs
        for entry_name, file_path in custom_pngs.items():
            with open(file_path, "rb") as f:
                zout.writestr(entry_name, f.read())
            print(f"  + Injected {entry_name}")

        # Inject custom XMLs
        for entry_name, file_path in custom_xmls.items():
            with open(file_path, "rb") as f:
                zout.writestr(entry_name, f.read())
            print(f"  + Injected {entry_name}")

    print(f"\nSuccessfully generated patched APK: {output_apk}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python patch_apk.py <input_apk> <output_apk> [custom_icons_dir]")
        sys.exit(1)

    in_apk = sys.argv[1]
    out_apk = sys.argv[2]
    icons_dir = sys.argv[3] if len(sys.argv) > 3 else os.path.join(os.path.dirname(__file__), "custom_icons")

    patch_apk(in_apk, out_apk, icons_dir)
