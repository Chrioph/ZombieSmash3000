#!/usr/bin/env python3
"""
Adds walking and attack frames to the hand drawn ogre without changing its design.

res/textures/OgreSpreadsheet.png holds the original frames in the first row (64x64 each,
columns: right, left, down, up). This script derives from them:

    row 1, 2: walking, the left and the right leg lifted
    row 3:    attack wind-up, the club raised back
    row 4:    attack hit, the club swung down with a few impact lines

The club is cut out by its colours and rotated around its handle; where it covered the
body the gap is filled with the surrounding skin. The original row is never modified, so
the script can be run again at any time.

    python3 tools/generate_ogre.py
    python3 tools/generate_ogre.py --preview out.png

Requires Pillow.
"""
import argparse
import math
import os
from collections import Counter

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SHEET = os.path.join(ROOT, "res", "textures", "OgreSpreadsheet.png")
F = 64
ROWS = 5

CLUB = {(0x31, 0x16, 0x0d), (0x45, 0x23, 0x1a), (0x2e, 0x18, 0x13), (0x35, 0x1c, 0x13)}
LOINCLOTH = {(0x7c, 0x51, 0x2c), (0x74, 0x30, 0x13), (0x70, 0x4a, 0x29), (0x60, 0x28, 0x10)}
SKIN = (0x4e, 0x87, 0x72, 255)
OUTLINE = (0x28, 0x58, 0x48, 255)
CLUB_DARK = (0x31, 0x16, 0x0d, 255)
CLUB_LIGHT = (0x45, 0x23, 0x1a, 255)
CLUB_OUTLINE = (0x1c, 0x0c, 0x07, 255)
LEG_TOP = 44
LIFT = 3

# per column: club direction for the wind-up and for the hit (screen degrees, 0 = right,
# 90 = down) and, for the back view which has no club, where the handle is held
ATTACK = {
    0: {"windup": -80, "hit": 40, "scale": 0.9},
    1: {"windup": -70, "hit": 150},
    2: {"windup": -80, "hit": 125},
    # back view: raised behind the shoulder, then swung over the head in front of the back
    3: {"windup": -60, "hit": -105, "pivot": (44, 28), "behind": True, "scale": 0.85},
}


def rgb(p):
    return p[:3]


def frames(sheet):
    return [sheet.crop((i * F, 0, i * F + F, F)) for i in range(4)]


def opaque(img):
    return {(x, y): img.getpixel((x, y)) for x in range(F) for y in range(F) if img.getpixel((x, y))[3] > 0}


# ------------------------------------------------------------------ walking

def walk(frame, lift_left):
    px = opaque(frame)
    xs = [x for x, _ in px]
    center = (min(xs) + max(xs)) / 2
    leg = {p: c for p, c in px.items()
           if p[1] >= LEG_TOP and rgb(c) not in LOINCLOTH and ((p[0] < center) == lift_left)}
    # the body rises a little with every step
    rest = Image.new("RGBA", (F, F))
    for (x, y), c in px.items():
        if (x, y) not in leg and y >= 1:
            rest.putpixel((x, y - 1), c)
    out = Image.new("RGBA", (F, F))
    for (x, y), c in leg.items():
        if y - LIFT >= 0:
            out.putpixel((x, y - LIFT), c)
    # the loincloth and the body stay in front of the lifted leg
    out.alpha_composite(rest)
    return out


# ------------------------------------------------------------------ attack

def split_club(frame):
    px = opaque(frame)
    club = {p: c for p, c in px.items() if rgb(c) in CLUB}
    body = {p: c for p, c in px.items() if p not in club}
    return club, body


def fill_holes(body, holes):
    """Fills club pixels that covered the body with the colours around them."""
    rows = {}
    for (x, y) in body:
        rows.setdefault(y, []).append(x)
    inside = {(x, y) for (x, y) in holes
              if y in rows and min(rows[y]) < x < max(rows[y])}
    body = dict(body)
    todo = set(inside)
    for _ in range(20):
        if not todo:
            break
        done = set()
        for (x, y) in todo:
            around = [body[(x + dx, y + dy)] for dx in (-1, 0, 1) for dy in (-1, 0, 1)
                      if (x + dx, y + dy) in body and (dx or dy)]
            if len(around) >= 3:
                body[(x, y)] = Counter(around).most_common(1)[0][0]
                done.add((x, y))
        todo -= done
    for p in todo:
        body[p] = SKIN
    # restore the outline where the club covered the edge of the body
    for (x, y) in inside:
        if any((x + dx, y + dy) not in body for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
            body[(x, y)] = OUTLINE
    return body


def draw_club(img, pivot, angle, length):
    """Draws the club from the handle (pivot) towards angle: thin handle, thick round head."""
    a = math.radians(angle)
    d = (math.cos(a), math.sin(a))
    n = (-d[1], d[0])
    inside = {}
    for x in range(F):
        for y in range(F):
            vx, vy = x + 0.5 - pivot[0], y + 0.5 - pivot[1]
            t = vx * d[0] + vy * d[1]
            w = vx * n[0] + vy * n[1]
            if t < -1 or t > length + 1:
                continue
            if t < length * 0.45:
                r = 1.3
            else:
                k = (t - length * 0.45) / (length * 0.55)
                r = 1.3 + 3.4 * math.sin(min(1.0, k * 1.15) * math.pi * 0.5)
                if t > length - 3.5:  # rounded end
                    r = min(r, math.sqrt(max(0.0, 4.7 ** 2 - (t - (length - 4.2)) ** 2)))
            if abs(w) <= r:
                inside[(x, y)] = w
    for (x, y), w in inside.items():
        edge = any((x + dx, y + dy) not in inside for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        img.putpixel((x, y), CLUB_OUTLINE if edge else CLUB_LIGHT if w < -0.5 else CLUB_DARK)


def impact_lines(img, pivot, angle, length):
    """A few short white strokes around the head of the club."""
    a = math.radians(angle)
    hx, hy = pivot[0] + math.cos(a) * length, pivot[1] + math.sin(a) * length
    for spread in (-50, 0, 50):
        b = a + math.radians(spread)
        for r in range(4, 8):
            x, y = int(round(hx + math.cos(b) * r)), int(round(hy + math.sin(b) * r))
            if 0 <= x < F and 0 <= y < F and img.getpixel((x, y))[3] == 0:
                img.putpixel((x, y), (255, 255, 255, 200))


def attack(frames_, col, hit):
    cfg = ATTACK[col]
    target = cfg["hit"] if hit else cfg["windup"]
    if col == 3:
        # the back view shows no club, borrow the one from the front view
        club, _ = split_club(frames_[2])
        src_pivot = max(club, key=lambda p: (p[1], p[0]))
        dx, dy = cfg["pivot"][0] - src_pivot[0], cfg["pivot"][1] - src_pivot[1]
        club = {(x + dx, y + dy): c for (x, y), c in club.items()}
        pivot = cfg["pivot"]
        body = opaque(frames_[3])
    else:
        club, body = split_club(frames_[col])
        pivot = max(club, key=lambda p: (p[1], p[0]))  # the handle is the lowest club pixel
        body = fill_holes(body, club.keys())
    # the club has to stay inside the 64x64 frame
    length = max(math.hypot(x - pivot[0], y - pivot[1]) for x, y in club) * cfg.get("scale", 1.0)
    club_img = Image.new("RGBA", (F, F))
    draw_club(club_img, pivot, target, length)

    body_img = Image.new("RGBA", (F, F))
    for p, c in body.items():
        body_img.putpixel(p, c)
    if cfg.get("behind") and not hit:
        out = club_img
        out.alpha_composite(body_img)
    else:
        out = body_img
        out.alpha_composite(club_img)
    if hit:
        impact_lines(out, pivot, target, length)
    return out


# ------------------------------------------------------------------ output

def build(sheet):
    fr = frames(sheet)
    out = Image.new("RGBA", (4 * F, ROWS * F))
    for col in range(4):
        out.paste(fr[col], (col * F, 0))
        out.paste(walk(fr[col], True), (col * F, F))
        out.paste(walk(fr[col], False), (col * F, 2 * F))
        out.paste(attack(fr, col, False), (col * F, 3 * F))
        out.paste(attack(fr, col, True), (col * F, 4 * F))
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--preview")
    args = ap.parse_args()
    sheet = Image.open(SHEET).convert("RGBA")
    out = build(sheet)
    if args.preview:
        bg = Image.new("RGBA", out.size, (106, 168, 79, 255))
        bg.alpha_composite(out)
        bg.resize((out.width * 3, out.height * 3), Image.NEAREST).save(args.preview)
    else:
        out.save(SHEET)


if __name__ == "__main__":
    main()
