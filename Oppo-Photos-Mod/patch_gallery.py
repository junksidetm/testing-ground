#!/usr/bin/env python3
"""
Surgical Morphe Cloner for OPPO Photos (com.coloros.gallery3d 17.10.10 -> morphe.coloros.gallery3d).

Patches:
1. Binary AndroidManifest.xml (preserves resources.arsc untouched so ColorOS private resources never break AAPT2):
   - Rewrites package="com.coloros.gallery3d" -> "morphe.coloros.gallery3d"
   - Expands relative leading-dot component names (e.g. ".app.App") to "com.coloros.gallery3d.*"
   - Suffixes all 38 ContentProvider authorities and 8 declared permissions (plus meta-data content:// URIs)
     with ".gallery.morphe" WITHOUT mutating overlapping provider class names (TrackContentProvider, CrossProcessFileProvider).
2. Smali files under smali*/**/*.smali:
   - Rewrites string literals for the 38 authorities, 8 declared permissions, content://<authority> URIs,
     exact package references ("com.coloros.gallery3d"), and /Android/data/com.coloros.gallery3d paths.
"""

from __future__ import annotations

import os
import re
import struct
import sys
from pathlib import Path

ORIG_PKG = "com.coloros.gallery3d"
NEW_PKG = "morphe.coloros.gallery3d"
AUTH_SUFFIX = ".gallery.morphe"

GALLERY_AUTHORITIES: tuple[str, ...] = (
    "com.oplus.gallery.foundation.util.multiprocess.CrossProcessFileProvider",
    "com.oplus.gallery.photoeditor.seedling.AiRetouchSeedlingCardProvider",
    "com.oplus.gallery.searchpage.provider.searchsuggestionprovider",
    "com.coloros.gallery3d.Track.MultiProcessSharedPreferencesProvider",
    "com.coloros.gallery3d.CloudAcrossProcDataProvider",
    "com.oplus.gallery.database.provider.watermark_master",
    "com.oplus.gallery.database.provider.gallery",
    "com.oplus.gallery.database.provider.config",
    "com.oplus.gallery.database.provider.cloud",
    "com.oplus.gallery.database.provider.cache",
    "com.oplus.gallery.multiprocess.sharepreference",
    "com.oppo.gallery3d.multiprocess.sharepreference",
    "com.oplus.gallery.settingpage.gallerysearch",
    "com.coloros.gallery3d.Track.TrackCommonProvider",
    "com.coloros.gallery3d.Track.BalanceEventProvider",
    "com.coloros.gallery3d.Track.TrackEventProvider",
    "com.oplus.dcc.provider.TrackContentProvider",
    "com.coloros.gallery3d.hlog.HLOG_SDK_PROVIDER",
    "com.coloros.gallery3d.EventContentProvider",
    "com.coloros.gallery3d.androidx-startup",
    "com.coloros.gallery3d.hlog.FILEPROVIDER",
    "com.coloros.gallery3d.custom.DictProvider",
    "com.coloros.gallery3d.FileShareProvider",
    "com.oplus.gallery.settingvisibility",
    "com.oppo.gallery3d.memoriesprovider",
    "com.oplus.gallery.quick.app.card",
    "com.oplus.gallery.albumsprovider",
    "com.oppo.gallery3d.albumsprovider",
    "com.coloros.gallery3d.oplus.epona",
    "com.coloros.gallery3d.fileProvider",
    "com.coloros.gallery3d.inner.init",
    "com.coloros.gallery3d.nas.backup",
    "com.oppo.gallery3d.open.provider",
    "com.open.gallery.smart.provider",
    "com.oplus.gallery.FileProvider",
    "com.coloros.gallery3d.tingle",
    "com.coloros.gallery3d.epona",
    "com.oplus.gallery.widget",
)

GALLERY_PERMISSIONS: tuple[str, ...] = (
    "com.oplus.gallery.openabilitypage.permission.GALLERY_KEEP_ALIVE_SERVICE",
    "com.oplus.gallery.MultiProcessSharePreferenceProvider.permission.write",
    "com.oplus.gallery.MultiProcessSharePreferenceProvider.permission.read",
    "com.oppo.gallery3d.MultiProcessSharePreferenceProvider.permission.write",
    "com.oppo.gallery3d.MultiProcessSharePreferenceProvider.permission.read",
    "com.coloros.gallery3d.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
    "com.coloros.gallery3d.permission.READ_NAS_BACKUP_SWITCH",
    "com.oppo.gallery3d.self.component.safe.permission",
)

AUTH_SET = set(GALLERY_AUTHORITIES)
PERM_SET = set(GALLERY_PERMISSIONS)
AUTHS_DESC = sorted(GALLERY_AUTHORITIES, key=len, reverse=True)
AUTH_URI_RE = re.compile(
    r"(content://)(" + "|".join(re.escape(a) for a in AUTHS_DESC) + r")(?=$|[/?#;])"
)
AUTH_SLASH_RE = re.compile(
    r"^(" + "|".join(re.escape(a) for a in AUTHS_DESC) + r")(/.*)$"
)

ANDROID_NS = "http://schemas.android.com/apk/res/android"
COMPONENT_TAGS = {"application", "activity", "activity-alias", "service", "receiver", "provider"}
COMPONENT_NAME_ATTRS = {"name", "targetActivity", "backupAgent", "manageSpaceActivity", "zygotePreloadName"}
PERM_REF_ATTRS = {"permission", "readPermission", "writePermission"}
QUOTED_STR_RE = re.compile(r'"((?:[^"\\]|\\.)*)"')


def _read_u16_len(buf: bytes, pos: int) -> tuple[int, int]:
    first = struct.unpack_from("<H", buf, pos)[0]
    if first & 0x8000:
        second = struct.unpack_from("<H", buf, pos + 2)[0]
        return ((first & 0x7FFF) << 16) | second, pos + 4
    return first, pos + 2


def _read_u8_len(buf: bytes, pos: int) -> tuple[int, int]:
    first = buf[pos]
    if first & 0x80:
        second = buf[pos + 1]
        return ((first & 0x7F) << 8) | second, pos + 2
    return first, pos + 1


def _encode_utf16_entry(s: str) -> bytes:
    raw = s.encode("utf-16-le")
    char_len = len(raw) // 2
    if char_len >= 0x8000:
        hdr = struct.pack("<HH", 0x8000 | (char_len >> 16), char_len & 0xFFFF)
    else:
        hdr = struct.pack("<H", char_len)
    return hdr + raw + b"\x00\x00"


def _encode_utf8_entry(s: str) -> bytes:
    raw = s.encode("utf-8")
    char_len = len(s)
    byte_len = len(raw)
    parts = []
    for n in (char_len, byte_len):
        if n >= 0x80:
            parts.append(bytes([(n >> 8) | 0x80, n & 0xFF]))
        else:
            parts.append(bytes([n]))
    return b"".join(parts) + raw + b"\x00"


def decode_string_pool(chunk: bytes) -> tuple[list[str], int, bytes, int]:
    _, hdr_size, _chunk_size = struct.unpack_from("<HHI", chunk, 0)
    str_count, sty_count, flags, str_start, sty_start = struct.unpack_from("<IIIII", chunk, 8)
    is_utf8 = bool(flags & 0x100)
    offsets = struct.unpack_from(f"<{str_count}I", chunk, hdr_size)

    strings: list[str] = []
    for off in offsets:
        p = str_start + off
        if is_utf8:
            _, p = _read_u8_len(chunk, p)
            blen, p = _read_u8_len(chunk, p)
            strings.append(chunk[p : p + blen].decode("utf-8", errors="replace"))
        else:
            clen, p = _read_u16_len(chunk, p)
            strings.append(chunk[p : p + clen * 2].decode("utf-16-le", errors="replace"))

    styles_blob = chunk[sty_start:] if sty_count > 0 and sty_start > 0 else b""
    return strings, sty_count, styles_blob, flags


def build_string_pool(strings: list[str], sty_count: int, style_offsets_bytes: bytes, styles_blob: bytes, flags: int, hdr_size: int = 28) -> bytes:
    is_utf8 = bool(flags & 0x100)
    offsets: list[int] = []
    data_parts: list[bytes] = []
    cur_off = 0
    for s in strings:
        offsets.append(cur_off)
        enc = _encode_utf8_entry(s) if is_utf8 else _encode_utf16_entry(s)
        data_parts.append(enc)
        cur_off += len(enc)

    str_data = b"".join(data_parts)
    # Pad string data to 4-byte boundary so styles/next chunk stays 4-byte aligned
    pad = (-len(str_data)) & 3
    str_data_padded = str_data + (b"\x00" * pad)

    str_start = hdr_size + 4 * len(strings) + 4 * sty_count
    sty_start = (str_start + len(str_data_padded)) if sty_count > 0 else 0
    total_size = hdr_size + 4 * len(strings) + len(style_offsets_bytes) + len(str_data_padded) + len(styles_blob)
    if total_size & 3:
        extra_pad = 4 - (total_size & 3)
        styles_blob = styles_blob + (b"\x00" * extra_pad)
        total_size += extra_pad

    header = struct.pack(
        "<HHIIIIII",
        0x0001,
        hdr_size,
        total_size,
        len(strings),
        sty_count,
        flags,
        str_start,
        sty_start,
    )
    offsets_bytes = struct.pack(f"<{len(strings)}I", *offsets)
    return header + offsets_bytes + style_offsets_bytes + str_data_padded + styles_blob


def patch_android_manifest(manifest_path: Path) -> dict[str, int]:
    raw = bytearray(manifest_path.read_bytes())
    magic, top_hdr, file_size = struct.unpack_from("<HHI", raw, 0)
    if magic != 0x0003:
        raise ValueError(f"Expected binary AXML (0x0003), got 0x{magic:04x}")
    if file_size != len(raw):
        raise ValueError(f"Manifest header size mismatch: {file_size} vs {len(raw)}")

    sp_type, sp_hdr, sp_size = struct.unpack_from("<HHI", raw, top_hdr)
    if sp_type != 0x0001:
        raise ValueError(f"Expected StringPool chunk after header, got 0x{sp_type:04x}")

    sp_chunk = bytes(raw[top_hdr : top_hdr + sp_size])
    strings, sty_count, _, flags = decode_string_pool(sp_chunk)
    style_offsets_start = sp_hdr + 4 * len(strings)
    style_offsets_bytes = sp_chunk[style_offsets_start : style_offsets_start + 4 * sty_count]
    sty_start_in_chunk = struct.unpack_from("<I", sp_chunk, 28)[0]
    styles_blob = sp_chunk[sty_start_in_chunk:] if sty_count > 0 and sty_start_in_chunk > 0 else b""

    str_to_idx: dict[str, int] = {}
    for i, s in enumerate(strings):
        str_to_idx.setdefault(s, i)

    def intern(val: str) -> int:
        if val in str_to_idx:
            return str_to_idx[val]
        idx = len(strings)
        strings.append(val)
        str_to_idx[val] = idx
        return idx

    def get_str(idx: int) -> str | None:
        if 0 <= idx < len(strings):
            return strings[idx]
        return None

    stats = {"package": 0, "components": 0, "authorities": 0, "permissions": 0, "metadata_uris": 0}
    pos = top_hdr + sp_size

    while pos < len(raw):
        ctype, chdr, csize = struct.unpack_from("<HHI", raw, pos)
        if ctype == 0x0102:  # RES_XML_START_ELEMENT_TYPE
            el_name = get_str(struct.unpack_from("<i", raw, pos + 20)[0]) or ""
            attr_start, attr_size, attr_count = struct.unpack_from("<HHH", raw, pos + 24)
            attr_base = pos + 16 + attr_start

            for k in range(attr_count):
                aoff = attr_base + k * attr_size
                ans_idx, aname_idx, araw_idx = struct.unpack_from("<iii", raw, aoff)
                dtype = raw[aoff + 15]
                adata = struct.unpack_from("<I", raw, aoff + 16)[0]

                ans = get_str(ans_idx) or ""
                aname = get_str(aname_idx) or ""
                cur_val = get_str(araw_idx) if araw_idx >= 0 else (get_str(adata) if dtype == 0x03 else None)
                if cur_val is None:
                    continue

                new_val: str | None = None

                if el_name == "manifest" and aname == "package" and ans == "":
                    if cur_val == ORIG_PKG:
                        new_val = NEW_PKG
                        stats["package"] += 1

                elif ans == ANDROID_NS:
                    if el_name in COMPONENT_TAGS and aname in COMPONENT_NAME_ATTRS and cur_val.startswith("."):
                        new_val = ORIG_PKG + cur_val
                        stats["components"] += 1

                    elif el_name == "provider" and aname == "authorities":
                        parts = [
                            (a + AUTH_SUFFIX if a in AUTH_SET else a)
                            for a in cur_val.split(";")
                        ]
                        candidate = ";".join(parts)
                        if candidate != cur_val:
                            new_val = candidate
                            stats["authorities"] += 1

                    elif (
                        (el_name in {"permission", "uses-permission", "uses-permission-sdk-23"} and aname == "name")
                        or aname in PERM_REF_ATTRS
                    ):
                        if cur_val in PERM_SET:
                            new_val = cur_val + AUTH_SUFFIX
                            stats["permissions"] += 1

                    elif el_name == "meta-data" and aname == "value" and "content://" in cur_val:
                        candidate = AUTH_URI_RE.sub(lambda m: f"{m.group(1)}{m.group(2)}{AUTH_SUFFIX}", cur_val)
                        if candidate != cur_val:
                            new_val = candidate
                            stats["metadata_uris"] += 1

                if new_val is not None and new_val != cur_val:
                    new_idx = intern(new_val)
                    struct.pack_into("<i", raw, aoff + 8, new_idx)
                    if dtype == 0x03:
                        struct.pack_into("<I", raw, aoff + 16, new_idx)

        pos += csize

    new_sp = build_string_pool(strings, sty_count, style_offsets_bytes, styles_blob, flags, hdr_size=sp_hdr)
    rest = bytes(raw[top_hdr + sp_size :])
    new_total = top_hdr + len(new_sp) + len(rest)
    out = struct.pack("<HHI", magic, top_hdr, new_total) + new_sp + rest
    manifest_path.write_bytes(out)
    return stats


def rewrite_smali_string(s: str) -> str:
    if s.startswith("L") and s.endswith(";"):
        return s
    if s == ORIG_PKG:
        return NEW_PKG
    if s in PERM_SET or s in AUTH_SET:
        return s + AUTH_SUFFIX

    changed = s
    if "/Android/data/com.coloros.gallery3d/" in changed:
        changed = changed.replace("/Android/data/com.coloros.gallery3d/", f"/Android/data/{NEW_PKG}/")
    elif changed.endswith("/Android/data/com.coloros.gallery3d"):
        changed = changed[:-len(ORIG_PKG)] + NEW_PKG

    if "content://" in changed:
        changed = AUTH_URI_RE.sub(lambda m: f"{m.group(1)}{m.group(2)}{AUTH_SUFFIX}", changed)
    else:
        m = AUTH_SLASH_RE.match(changed)
        if m:
            changed = f"{m.group(1)}{AUTH_SUFFIX}{m.group(2)}"

    return changed


def patch_smali_tree(root: Path) -> dict[str, int]:
    smali_dirs = sorted(p for p in root.iterdir() if p.is_dir() and p.name.startswith("smali"))
    files_scanned = 0
    files_modified = 0
    strings_rewritten = 0

    for sdir in smali_dirs:
        for dirpath, _, filenames in os.walk(sdir):
            for fname in filenames:
                if not fname.endswith(".smali"):
                    continue
                files_scanned += 1
                fpath = Path(dirpath) / fname
                text = fpath.read_text(encoding="utf-8", errors="surrogateescape")

                # Fast substring prefilter before regex work
                if (
                    "gallery" not in text
                    and "com.oplus.dcc.provider.TrackContentProvider" not in text
                ):
                    continue

                out_lines: list[str] = []
                file_changed = False
                for line in text.splitlines(keepends=True):
                    stripped = line.lstrip()
                    if stripped.startswith(("const-string", ".field ")):
                        def _sub(m: re.Match[str]) -> str:
                            nonlocal strings_rewritten, file_changed
                            orig_lit = m.group(1)
                            new_lit = rewrite_smali_string(orig_lit)
                            if new_lit != orig_lit:
                                strings_rewritten += 1
                                file_changed = True
                                return f'"{new_lit}"'
                            return m.group(0)

                        line = QUOTED_STR_RE.sub(_sub, line)
                    out_lines.append(line)

                if file_changed:
                    fpath.write_text("".join(out_lines), encoding="utf-8", errors="surrogateescape")
                    files_modified += 1

    return {
        "smali_dirs": len(smali_dirs),
        "files_scanned": files_scanned,
        "files_modified": files_modified,
        "strings_rewritten": strings_rewritten,
    }


def main() -> int:
    if len(sys.argv) < 2:
        print("Usage: patch_gallery.py <decompiled_apk_dir_or_manifest.bin>", file=sys.stderr)
        return 2

    target = Path(sys.argv[1])
    if target.is_file():
        stats = patch_android_manifest(target)
        print("Manifest patched:", stats)
        return 0

    manifest_path = target / "AndroidManifest.xml"
    if not manifest_path.is_file():
        print(f"AndroidManifest.xml not found at {manifest_path}", file=sys.stderr)
        return 1

    m_stats = patch_android_manifest(manifest_path)
    print("Manifest patched:", m_stats)
    s_stats = patch_smali_tree(target)
    print("Smali patched:", s_stats)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
