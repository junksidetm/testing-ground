#!/usr/bin/env python3
"""
Patch appfilter.xml and drawable.xml in a decompiled Apktool tree.
"""

import sys
import os

def patch_file(file_path, additions):
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    if "</resources>" in content:
        new_content = content.replace("</resources>", f"\n\n\t{additions}\n</resources>")
    else:
        new_content = content + f"\n\n{additions}\n"

    with open(file_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print(f"  ✓ Patched: {file_path}")

def main():
    if len(sys.argv) < 3:
        print("Usage: python patch_decompiled.py <decompiled_dir> <custom_icons_dir>")
        sys.exit(1)

    decompiled_dir = sys.argv[1]
    custom_dir = sys.argv[2]

    with open(os.path.join(custom_dir, "appfilter_additions.xml"), "r", encoding="utf-8") as f:
        appfilter_add = f.read().strip()

    with open(os.path.join(custom_dir, "drawable_additions.xml"), "r", encoding="utf-8") as f:
        drawable_add = f.read().strip()

    # Patch in assets/
    patch_file(os.path.join(decompiled_dir, "assets", "appfilter.xml"), appfilter_add)
    patch_file(os.path.join(decompiled_dir, "assets", "drawable.xml"), drawable_add)

    # Patch in res/xml/ if present
    patch_file(os.path.join(decompiled_dir, "res", "xml", "appfilter.xml"), appfilter_add)
    patch_file(os.path.join(decompiled_dir, "res", "xml", "drawable.xml"), drawable_add)

if __name__ == "__main__":
    main()
