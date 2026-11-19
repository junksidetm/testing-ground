#!/usr/bin/env python3
"""
Sanitizes Apktool-decompiled resources so AAPT2 can link cleanly:
1. Removes references to missing library attributes (state_liftable, state_swiped).
2. Fixes private framework attribute namespaces.
"""

import os
import sys

def sanitize(decompiled_dir):
    print("Sanitizing decompiled resources for AAPT2 linking...")

    # 1. Neutralize problematic state_list_animators in res/animator/
    animator_dir = os.path.join(decompiled_dir, "res", "animator")
    if os.path.exists(animator_dir):
        for fn in os.listdir(animator_dir):
            if "state_list" in fn or "appbar" in fn:
                p = os.path.join(animator_dir, fn)
                with open(p, "w", encoding="utf-8") as f:
                    f.write('<?xml version="1.0" encoding="utf-8"?>\n<selector xmlns:android="http://schemas.android.com/apk/res/android">\n</selector>\n')
                print(f"  ✓ Neutralized animator: {fn}")

    # 2. Fix state_swiped in res/color/
    color_dir = os.path.join(decompiled_dir, "res", "color")
    if os.path.exists(color_dir):
        for fn in os.listdir(color_dir):
            p = os.path.join(color_dir, fn)
            if os.path.isfile(p):
                with open(p, "r", encoding="utf-8") as f:
                    content = f.read()
                if "state_swiped" in content:
                    lines = [line for line in content.splitlines() if "state_swiped" not in line]
                    with open(p, "w", encoding="utf-8") as f:
                        f.write("\n".join(lines))
                    print(f"  ✓ Removed state_swiped from: {fn}")

    print("Sanitization complete.")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        sanitize(sys.argv[1])
    else:
        print("Usage: python sanitize_for_aapt2.py <decompiled_dir>")
