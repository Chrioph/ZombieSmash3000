#!/usr/bin/env python3
"""
Generates the character and item sprites of ZombieSmash3000: the player, the zombie,
the item icons (sword, bow, arrows, heart, armor, ...), the gravestone and the chest.
The ogre is hand drawn and not touched.

Characters are put together from body parts (head, hair, face, torso, arms, belt, legs,
shoes) per view (front, back, side) and pose (idle, walking, attacking), then shaded with
light from the top left and outlined. Left facing frames are mirrored right facing ones.

    python3 tools/generate_sprites.py            # writes into res/textures/Sheet2.png
    python3 tools/generate_sprites.py --preview out.png

Sword and bow keep the geometry the attack animations rely on (see WeaponDrawing.java):
sword grip at (9, 24) with the blade pointing up-right, bow facing right with its string
in column 13 between rows 4 and 27.

Requires Pillow.
"""
import argparse
import math
import os
import sys

from PIL import Image, ImageOps

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from generate_textures import hexc, put, TEXTURES, CLEAR  # noqa: E402

T = 32


def pal(*colors):
    """Material palette: outline, dark, mid, light."""
    return [hexc(c) for c in colors]


PLAYER = {
    "skin": pal("#5a3424", "#d99a74", "#f2c09a", "#ffdcbd"),
    "hair": pal("#1f130c", "#3b2416", "#583720", "#7a5032"),
    "shirt": pal("#173a19", "#2f7a35", "#46a04b", "#6cc46a"),
    "pants": pal("#14254d", "#2a4ea3", "#3a68cc", "#5888e8"),
    "pants_back": pal("#101d3d", "#223f85", "#2f56ab", "#4570c8"),
    "shoe": pal("#1e120b", "#3a2416", "#553621", "#6e4a2f"),
    "belt": pal("#2a180c", "#4e321a", "#6b4724", "#8a5e31"),
    "buckle": pal("#5a3e08", "#c9961e", "#f0c43a", "#fff09a"),
    "eye": pal("#1d1720", "#1d1720", "#1d1720", "#1d1720"),
    "mouth": pal("#7a3a2a", "#a8604a", "#a8604a", "#a8604a"),
}

ZOMBIE = {
    "skin": pal("#16240f", "#4c7342", "#6a9459", "#8ab874"),
    "hair": pal("#0c100a", "#1f261b", "#30392b", "#45503e"),
    "shirt": pal("#300b0b", "#7d201c", "#a8302a", "#cc4a3d"),
    "pants": pal("#111a33", "#273a69", "#34508c", "#4a68a8"),
    "pants_back": pal("#0d1529", "#1f305a", "#2a4278", "#3c5894"),
    "shoe": pal("#121212", "#2a2a2a", "#3d3d3d", "#545454"),
    "belt": pal("#300b0b", "#7d201c", "#a8302a", "#cc4a3d"),
    "buckle": pal("#300b0b", "#7d201c", "#a8302a", "#cc4a3d"),
    "eye": pal("#ffd2a0", "#ff4a3d", "#ff4a3d", "#ffd2a0"),
    "mouth": pal("#1a0606", "#3a0c0c", "#3a0c0c", "#3a0c0c"),
    "tooth": pal("#d8d0b8", "#e8e2cc", "#e8e2cc", "#e8e2cc"),
    "wound": pal("#3a0606", "#5c1010", "#5c1010", "#7a1818"),
}

# ------------------------------------------------------------------ shapes


def rect(x0, y0, x1, y1):
    return {(x, y) for x in range(x0, x1 + 1) for y in range(y0, y1 + 1)}


def ellipse(cx, cy, rx, ry):
    return {(x, y) for x in range(T) for y in range(T)
            if ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2 <= 1}


class Sprite:
    """Material map that is shaded and outlined when rendered."""

    def __init__(self, palette):
        self.palette = palette
        self.mat = {}
        self.head = None  # (cx, cy, rx, ry) for spherical shading of the head
        self.flat = set()  # pixels drawn with their mid colour only (face details)

    def fill(self, pixels, material, flat=False):
        for p in pixels:
            if 0 <= p[0] < T and 0 <= p[1] < T:
                self.mat[p] = material
                if flat:
                    self.flat.add(p)
                else:
                    self.flat.discard(p)

    def erase(self, pixels):
        for p in pixels:
            self.mat.pop(p, None)
            self.flat.discard(p)

    def render(self):
        img = Image.new("RGBA", (T, T), CLEAR)
        for (x, y), m in self.mat.items():
            p = self.palette[m]
            if (x, y) in self.flat:
                img.putpixel((x, y), p[2] if m != "eye" else p[1])
                continue
            if self.head and m in ("skin", "hair") and self._in_head(x, y):
                img.putpixel((x, y), p[self._sphere(x, y)])
                continue
            up, left = self.mat.get((x, y - 1)), self.mat.get((x - 1, y))
            down, right = self.mat.get((x, y + 1)), self.mat.get((x + 1, y))
            if right != m or down != m:
                c = p[1]
            elif up != m or left != m:
                c = p[3]
            else:
                c = p[2]
            img.putpixel((x, y), c)
        # selective outline: transparent pixels next to the sprite take the darkest
        # colour of the neighbouring material
        out = img.copy()
        for x in range(T):
            for y in range(T):
                if (x, y) in self.mat:
                    continue
                for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                    m = self.mat.get((nx, ny))
                    if m:
                        out.putpixel((x, y), self.palette[m][0])
                        break
        return out

    def _in_head(self, x, y):
        cx, cy, rx, ry = self.head
        return ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2 <= 1.05

    def _sphere(self, x, y):
        cx, cy, rx, ry = self.head
        nx, ny = (x + 0.5 - cx) / rx, (y + 0.5 - cy) / ry
        nz = math.sqrt(max(0.0, 1 - nx * nx - ny * ny))
        lum = -0.5 * nx - 0.55 * ny + 0.67 * nz
        return 3 if lum > 0.62 else 2 if lum > 0.2 else 1


# ------------------------------------------------------------------ humanoids

def humanoid(palette, view, pose, zombie=False):
    """
    view: front, back or side (facing right)
    pose: idle, walk0, walk1, attack, aggro0, aggro1 (zombie only)
    """
    s = Sprite(palette)
    walk = pose in ("walk0", "walk1")
    aggro = pose.startswith("aggro")
    if view == "side":
        side(s, pose, walk, aggro, zombie)
    else:
        front_back(s, view, pose, walk, aggro, zombie)
    return s.render()


def front_back(s, view, pose, walk, aggro, zombie):
    # legs and shoes, lifting one leg while walking
    lift_left = pose == "walk0"
    lift_right = pose == "walk1"
    for x0, x1, lifted in ((12, 14, lift_left), (17, 19, lift_right)):
        top = 23
        bottom = 27 if lifted else 28
        s.fill(rect(x0, top, x1, bottom), "pants")
        sx0, sx1 = (x0 - 1, x1) if x0 < 16 else (x0, x1 + 1)
        s.fill(rect(sx0, bottom + 1, sx1, bottom + 2), "shoe")
        if zombie:
            # ragged trousers show a bit of ankle
            s.fill({(x0 + 1, bottom)}, "skin")
    s.fill(rect(15, 23, 16, 24), "pants")

    # torso and belt
    s.fill(rect(11, 16, 20, 22), "shirt")
    if zombie:
        # torn hem instead of a belt
        s.erase({(11, 22), (13, 22), (16, 22), (19, 22)})
        s.fill({(18, 18), (19, 19), (18, 19)}, "wound", flat=True)
    else:
        s.fill(rect(11, 22, 20, 22), "belt")
        if view == "front":
            s.fill(rect(15, 22, 16, 22), "buckle")

    # arms
    if zombie and view == "front" and not aggro:
        reach_forward(s, hand_y=18 + (1 if pose == "walk1" else 0))
    elif zombie and view == "back" and not aggro:
        s.fill(rect(9, 17, 10, 18), "shirt")
        s.fill(rect(21, 17, 22, 18), "shirt")
    elif aggro or (pose == "attack" and view == "back"):
        # arms raised to the head
        wiggle = 1 if pose == "aggro1" else 0
        for x0 in (8, 22):
            s.fill(rect(x0, 12 + wiggle, x0 + 1, 17), "shirt")
            s.fill(rect(x0, 9 + wiggle, x0 + 1, 11 + wiggle), "skin")
            if zombie:
                s.fill({(x0 + (1 if x0 < 16 else 0), 8 + wiggle)}, "skin")
    elif pose == "attack":
        reach_forward(s, hand_y=19)
    else:
        swing_l = 1 if pose == "walk1" else 0
        swing_r = 1 if pose == "walk0" else 0
        s.fill(rect(9, 17, 10, 19 + swing_l), "shirt")
        s.fill(rect(9, 20 + swing_l, 10, 21 + swing_l), "skin")
        s.fill(rect(21, 17, 22, 19 + swing_r), "shirt")
        s.fill(rect(21, 20 + swing_r, 22, 21 + swing_r), "skin")

    # head
    cx, cy, rx, ry = 15.5 + (0.5 if zombie and walk and pose == "walk1" else 0), 9.2, 7.3, 6.8
    head = ellipse(cx, cy, rx, ry)
    s.head = (cx, cy, rx, ry)
    s.fill(head, "skin")
    if view == "back":
        s.fill({p for p in head if p[1] <= 13}, "hair")
    else:
        hair = {p for p in head if p[1] <= 5 or (p[1] <= 8 and (p[0] <= 9 or p[0] >= 22))}
        hair |= {(x, 6) for x in range(9, 23) if x % 3 != 1}
        if zombie:
            hair = {p for p in hair if not (p[1] >= 6 and p[0] % 2 == 0)} | {(10, 2), (20, 3)}
        s.fill({p for p in hair if p in head or p[1] < 4}, "hair")
        face(s, cx, aggro, zombie)


def reach_forward(s, hand_y):
    """Both arms reaching forward towards the viewer, hands in front of the body."""
    s.fill({(10, 17), (11, 17), (11, 18), (12, 18), (12, hand_y), (11, hand_y)}, "shirt")
    s.fill({(21, 17), (20, 17), (20, 18), (19, 18), (19, hand_y), (20, hand_y)}, "shirt")
    s.fill(rect(13, hand_y, 14, hand_y + 1), "skin")
    s.fill(rect(17, hand_y, 18, hand_y + 1), "skin")


def face(s, cx, aggro, zombie):
    ex = (int(cx - 3.5), int(cx + 3.5))
    for x in ex:
        if zombie:
            s.fill({(x, 9)}, "eye", flat=True)
            s.fill({(x, 10)}, "mouth", flat=True)
        else:
            s.fill({(x, 9), (x, 10)}, "eye", flat=True)
    mx = int(cx)
    if zombie:
        if aggro:
            s.fill(rect(mx - 1, 12, mx + 2, 14), "mouth", flat=True)
            s.fill({(mx - 1, 12), (mx + 1, 12), (mx, 14)}, "tooth", flat=True)
        else:
            s.fill({(mx - 1, 13), (mx, 13), (mx + 1, 13), (mx + 2, 12)}, "mouth", flat=True)
            s.fill({(mx, 13)}, "tooth", flat=True)
    else:
        s.fill({(mx - 1, 13), (mx, 13), (mx + 1, 13), (mx + 2, 13)}, "mouth", flat=True)
        s.fill({(mx - 2, 12), (mx + 3, 12)}, "mouth", flat=True)


def side(s, pose, walk, aggro, zombie):
    # legs: the back leg is drawn darker behind the front leg
    if pose == "walk0":
        s.fill(rect(12, 23, 14, 28), "pants_back")
        s.fill(rect(11, 29, 14, 30), "shoe")
        s.fill(rect(17, 23, 19, 28), "pants")
        s.fill(rect(18, 29, 21, 30), "shoe")
    else:
        s.fill(rect(15, 23, 18, 28), "pants")
        s.fill(rect(15, 29, 19, 30), "shoe")
        if pose == "walk1":
            s.fill(rect(14, 27, 15, 28), "pants_back")
    if zombie:
        s.fill({(16, 28)}, "skin")

    s.fill(rect(13, 16, 19, 22), "shirt")
    if zombie:
        s.erase({(13, 22), (16, 22)})
        s.fill({(18, 19), (17, 20)}, "wound", flat=True)
    else:
        s.fill(rect(13, 22, 19, 22), "belt")

    # near arm
    if pose == "attack" or (zombie and not aggro):
        y = 18 if pose == "attack" else 18 + (1 if pose == "walk1" else 0)
        s.fill(rect(16, y, 23, y + 1), "shirt")
        s.fill(rect(24, y, 25, y + 1), "skin")
        if zombie:
            s.fill({(26, y + 1)}, "skin")
    elif aggro:
        pass  # the raised arm is drawn after the head, see below
    else:
        dx = 1 if pose == "walk0" else -1 if pose == "walk1" else 0
        s.fill(rect(15, 17, 17, 19), "shirt")
        s.fill(rect(15 + dx + (1 if dx > 0 else 0), 20, 16 + dx + (1 if dx > 0 else 0), 21), "skin")

    # head facing right
    cx, cy, rx, ry = 16.2, 9.2, 7.0, 6.8
    head = ellipse(cx, cy, rx, ry)
    s.head = (cx, cy, rx, ry)
    s.fill(head, "skin")
    s.fill({(23, 10)}, "skin")  # nose
    hair = {p for p in head if p[1] <= 4 or (p[0] <= 15 and p[1] <= 11) or (p[1] <= 6 and p[0] <= 20)}
    if zombie:
        hair = {p for p in hair if not (p[0] % 3 == 0 and p[1] >= 9)}
    s.fill(hair, "hair")
    s.fill({(14, 9), (14, 10)}, "skin")  # ear
    if zombie:
        s.fill({(20, 8)}, "eye", flat=True)
        s.fill({(20, 9)}, "mouth", flat=True)
        if aggro:
            s.fill(rect(19, 12, 22, 14), "mouth", flat=True)
            s.fill({(20, 12), (22, 12), (21, 14)}, "tooth", flat=True)
        else:
            s.fill({(20, 13), (21, 13), (22, 12)}, "mouth", flat=True)
    else:
        s.fill({(20, 8), (20, 9)}, "eye", flat=True)
        s.fill({(20, 13), (21, 13), (22, 12)}, "mouth", flat=True)

    if aggro:
        # claw raised in front of the face
        up = 1 if pose == "aggro1" else 0
        s.fill({(17, 17), (18, 17), (18, 16), (19, 16), (20, 16), (20, 15), (21, 15), (22, 15), (22, 14), (23, 14)}, "shirt")
        s.fill(rect(24, 12 - up, 25, 14 - up), "skin")
        s.fill({(26, 11 - up), (26, 13 - up), (24, 11 - up)}, "skin")


def mirror(img):
    return ImageOps.mirror(img)


# ------------------------------------------------------------------ items

def shade_shape(pixels, palette, light_dir=(-1, -1)):
    """Shades a set of pixels like Sprite does (light edge top-left, dark edge bottom-right)."""
    s = Sprite({"m": palette})
    s.fill(pixels, "m")
    return s


def render_materials(layers, palettes):
    """layers: list of (pixels, material) drawn in order."""
    s = Sprite(palettes)
    for pixels, m, *flat in layers:
        s.fill(pixels, m, flat=bool(flat and flat[0]))
    return s.render()


HEART_SHAPE = [
    "..###...###..",
    ".#####.#####.",
    "#############",
    "#############",
    "#############",
    ".###########.",
    "..#########..",
    "...#######...",
    "....#####....",
    ".....###.....",
    "......#......",
]


def heart_pixels(ox, oy):
    return {(ox + x, oy + y) for y, row in enumerate(HEART_SHAPE) for x, c in enumerate(row) if c == "#"}


HEART = pal("#7a1020", "#d42c40", "#f2475b", "#ff8796")
GOLD = pal("#5a3e08", "#c9961e", "#f0c43a", "#fff09a")
STEEL = pal("#23262e", "#7d8494", "#a9b0bf", "#e4e8f0")
LEATHER = pal("#2a170c", "#6a3d1f", "#8c5529", "#b07035")
WOODP = pal("#2a170c", "#6a3d1f", "#8c5529", "#b8783c")
STONE = pal("#3d4150", "#7d8396", "#9aa0b2", "#c0c5d2")
GREEN = pal("#2a5a26", "#46a043", "#62bd52", "#86d468")
WHITE = pal("#555555", "#cfcfcf", "#ececec", "#ffffff")
RED = pal("#3a0606", "#a8302a", "#cc4a3d", "#ee7a6a")
SEED = pal("#3a240f", "#8a5a2a", "#b07a3c", "#d8a868")


def heart_icon(ox, oy):
    """Small classic pixel heart (13x11) with a glossy highlight."""
    img = render_materials([(heart_pixels(ox, oy), "h")], {"h": HEART})
    for dx, dy in ((2, 1), (1, 2), (2, 2), (3, 1)):
        img.putpixel((ox + dx, oy + dy), HEART[3])
    img.putpixel((ox + 2, oy + 1), (255, 255, 255, 255))
    return img


def heart():
    return heart_icon(9, 10)


def health_plus():
    img = heart_icon(7, 9)
    plus = rect(20, 17, 22, 25) | rect(17, 20, 25, 22)
    img.alpha_composite(render_materials([(plus, "p")], {"p": GOLD}))
    return img


def armor():
    """Steel breastplate with shoulder guards and a gold trim."""
    plate = set()
    for y in range(9, 27):
        # widest at the chest, rounded towards the bottom
        half = 7.5 if y < 20 else 7.5 - (y - 19) * 0.75
        for x in range(T):
            if abs(x + 0.5 - 16) <= half:
                plate.add((x, y))
    neck = ellipse(16, 8.5, 4, 3.5)
    plate -= neck
    shoulders = set()
    for x0, x1 in ((4, 10), (21, 27)):
        shoulders |= {(x, y) for x in range(x0, x1 + 1) for y in range(8, 14)
                      if abs(x + 0.5 - (x0 + x1 + 1) / 2) <= 3.5 - max(0, y - 11)}
    trim = {p for p in plate if (p[0], p[1] - 1) in neck or p[1] == 9 and p not in neck}
    ridge = {(15, y) for y in range(12, 25)} | {(16, y) for y in range(12, 25)}
    layers = [(plate, "s"), (shoulders, "s"), (trim, "g"), (ridge, "r", True)]
    img = render_materials(layers, {"s": STEEL, "g": GOLD, "r": pal("#23262e", "#8a91a1", "#8a91a1", "#8a91a1")})
    for p in ((11, 14), (20, 14), (11, 19), (20, 19), (6, 10), (25, 10)):
        img.putpixel(p, GOLD[2])
    return img


def seeds():
    layers = []
    for cx, cy, a in ((11, 18, 0.5), (19, 13, -0.6), (20, 21, 0.2)):
        pts = set()
        for x in range(T):
            for y in range(T):
                dx, dy = x + 0.5 - cx, y + 0.5 - cy
                u = dx * math.cos(a) + dy * math.sin(a)
                v = -dx * math.sin(a) + dy * math.cos(a)
                if (u / 4.2) ** 2 + (v / 2.8) ** 2 <= 1:
                    pts.add((x, y))
        layers.append((pts, "s"))
    img = render_materials(layers, {"s": SEED})
    return img


def gravestone():
    stone = rect(9, 10, 22, 27) | ellipse(15.5, 10.5, 6.5, 6)
    ground = rect(6, 27, 25, 29)
    letters = {
        "R": ["110", "101", "110", "101", "101"],
        "I": ["1", "1", "1", "1", "1"],
        "P": ["110", "101", "110", "100", "100"],
    }
    text, x = set(), 11
    for ch in "RIP":
        rows = letters[ch]
        for dy, row in enumerate(rows):
            for dx, c in enumerate(row):
                if c == "1":
                    text.add((x + dx, 11 + dy))
        x += len(rows[0]) + 1
    crack = {(19, 19), (18, 20), (19, 21), (18, 22)}
    img = render_materials([(ground, "g"), (stone, "s"), (text, "t", True), (crack, "t", True)],
                           {"s": STONE, "g": GREEN, "t": pal("#22242e", "#3d4150", "#3d4150", "#3d4150")})
    for x in (7, 10, 21, 24):
        put(img, x, 26, GREEN[3], wrap=False)
    return img


def sword():
    """Blade from the grip (9, 24) up-right to the tip (27, 7), like the old sprite."""
    d = (1 / math.sqrt(2), -1 / math.sqrt(2))      # along the blade
    n = (1 / math.sqrt(2), 1 / math.sqrt(2))       # across the blade
    blade, edge, guard, grip, pommel = set(), set(), set(), set(), set()
    for x in range(T):
        for y in range(T):
            px, py = x + 0.5, y + 0.5
            t = (px - 9.5) * d[0] + (py - 24.5) * d[1]   # distance along the blade from the grip
            w = (px - 9.5) * n[0] + (py - 24.5) * n[1]   # distance across
            if 3.2 <= t <= 24.5 - max(0, (t - 21.5)) * 0 and abs(w) <= (1.6 if t < 21 else 1.6 * (24.5 - t) / 3.5):
                (edge if w < -0.2 else blade).add((x, y))
            elif 1.6 <= t < 3.2 and abs(w) <= 4.6:
                guard.add((x, y))
            elif -3.5 <= t < 1.6 and abs(w) <= 0.9:
                grip.add((x, y))
            elif -5.3 <= t < -3.5 and abs(w) <= 1.5:
                pommel.add((x, y))
    img = render_materials([(blade, "b"), (edge, "e"), (guard, "g"), (grip, "l"), (pommel, "g")],
                           {"b": STEEL, "e": pal("#23262e", "#c3c9d6", "#d8dde8", "#f4f6fa"), "g": GOLD,
                            "l": LEATHER})
    return img


def bow():
    """Bow facing right, string in column 13 from row 2 to row 29, grip around (19, 15)."""
    limb = set()
    for y in range(2, 30):
        t = (y - 15.5) / 13.5
        x = 13.5 + 6.5 * (1 - t * t) ** 0.8
        for dx in (-0.8, 0.0, 0.8):
            limb.add((int(x + dx), y))
    grip = rect(18, 13, 21, 18)
    tips = {(13, 2), (14, 2), (13, 29), (14, 29), (14, 3), (14, 28)}
    string = {(13, y) for y in range(3, 29)}
    img = render_materials([(limb, "w"), (grip, "l"), (tips, "g"), (string, "s", True)],
                           {"w": WOODP, "l": LEATHER, "g": GOLD, "s": pal("#d8d2c0", "#e8e2d0", "#e8e2d0", "#e8e2d0")})
    return img


def arrow_right():
    shaft = rect(6, 15, 22, 16)
    head = {(x, y) for x in range(22, 29) for y in range(11, 21) if abs(y + 0.5 - 16) <= (28.5 - x) * 0.75}
    feathers = {(x, y) for x in range(3, 11) for y in range(11, 21)
                if abs(y + 0.5 - 16) <= 1 + (10.5 - x) * 0.55 and abs(y + 0.5 - 16) >= 1}
    return render_materials([(shaft, "w"), (feathers, "f"), (head, "s")], {"w": WOODP, "f": RED, "s": STEEL})


def arrow_icon():
    """Diagonal arrow for the inventory and HUD, head pointing up-right."""
    big = arrow_right().resize((64, 64), Image.NEAREST).rotate(45, resample=Image.NEAREST, center=(32, 32))
    small = big.resize((32, 32), Image.NEAREST)
    return small


def chest():
    W = 64
    img = Image.new("RGBA", (W, W), CLEAR)

    def block(x0, y0, x1, y1, palette, glossy=False):
        for x in range(x0, x1 + 1):
            for y in range(y0, y1 + 1):
                if x in (x0, x1) or y in (y0, y1):
                    c = palette[0]
                elif x == x0 + 1 or y == y0 + 1:
                    c = palette[3]
                elif x == x1 - 1 or y == y1 - 1:
                    c = palette[1]
                else:
                    c = palette[2]
                img.putpixel((x, y), c)

    # shadow
    for x in range(6, 58):
        for y in range(54, 60):
            if ((x + 0.5 - 32) / 26) ** 2 + ((y + 0.5 - 56) / 3) ** 2 <= 1:
                img.putpixel((x, y), (0, 0, 0, 70))
    block(8, 30, 55, 56, WOODP)            # body
    block(8, 14, 55, 31, WOODP)            # lid
    for y in range(17, 29, 4):             # planks on the lid
        for x in range(10, 54):
            img.putpixel((x, y), WOODP[1])
    for y in range(34, 54, 5):
        for x in range(10, 54):
            img.putpixel((x, y), WOODP[1])
    block(8, 14, 13, 56, GOLD)             # metal bands
    block(50, 14, 55, 56, GOLD)
    block(8, 29, 55, 33, GOLD)
    block(27, 27, 36, 40, GOLD)            # lock
    for x, y in ((31, 32), (32, 32), (31, 33), (32, 33), (31, 34), (32, 35), (31, 35), (32, 36), (31, 36)):
        img.putpixel((x, y), hexc("#2a1a05"))
    for x, y in ((10, 16), (52, 16), (10, 54), (52, 54), (10, 31), (52, 31)):
        img.putpixel((x, y), GOLD[3])
    return img


# ------------------------------------------------------------------ output

def build():
    p, z = PLAYER, ZOMBIE
    f = {pose: humanoid(p, "front", pose) for pose in ("idle", "walk0", "walk1", "attack")}
    b = {pose: humanoid(p, "back", pose) for pose in ("idle", "walk0", "walk1", "attack")}
    r = {pose: humanoid(p, "side", pose) for pose in ("idle", "walk0", "walk1", "attack")}
    zf = {pose: humanoid(z, "front", pose, True) for pose in ("idle", "walk0", "walk1", "aggro0", "aggro1")}
    zb = {pose: humanoid(z, "back", pose, True) for pose in ("walk0", "walk1", "aggro0", "aggro1")}
    zr = {pose: humanoid(z, "side", pose, True) for pose in ("walk0", "walk1", "aggro0", "aggro1")}
    ar = arrow_right()
    cells = {
        # player
        (2, 1): f["idle"], (0, 5): f["idle"],
        (3, 1): f["walk0"], (0, 2): f["walk1"], (0, 4): f["attack"],
        (3, 5): b["idle"], (1, 2): b["walk0"], (2, 2): b["walk1"], (3, 4): b["attack"],
        (1, 5): r["idle"], (3, 2): r["walk0"], (0, 3): r["walk1"], (1, 4): r["attack"],
        (2, 5): mirror(r["idle"]), (1, 3): mirror(r["walk0"]), (2, 3): mirror(r["walk1"]), (2, 4): mirror(r["attack"]),
        # zombie
        (6, 1): zf["idle"], (7, 1): zf["walk0"], (4, 2): zf["walk1"],
        (5, 2): zb["walk0"], (6, 2): zb["walk1"],
        (7, 2): zr["walk0"], (4, 3): zr["walk1"],
        (5, 3): mirror(zr["walk0"]), (6, 3): mirror(zr["walk1"]),
        (8, 4): zf["aggro0"], (11, 4): zf["aggro1"],
        (8, 5): zr["aggro0"], (11, 5): zr["aggro1"],
        (9, 5): mirror(zr["aggro0"]), (10, 5): mirror(zr["aggro1"]),
        (8, 7): zb["aggro0"], (9, 7): zb["aggro1"],
        # items
        (3, 3): sword(), (2, 6): bow(), (7, 3): heart(), (8, 0): armor(), (9, 0): health_plus(),
        (8, 2): seeds(), (9, 1): gravestone(),
        (3, 6): arrow_icon(), (4, 6): ar, (5, 6): ar.rotate(-90), (6, 6): mirror(ar), (7, 6): ar.rotate(90),
    }
    return cells, chest()


def write(cells, chest_img):
    path = os.path.join(TEXTURES, "Sheet2.png")
    sheet = Image.open(path).convert("RGBA")
    blank = Image.new("RGBA", (T, T), CLEAR)
    for (cx, cy), img in cells.items():
        sheet.paste(blank, (cx * T, cy * T))
        sheet.paste(img, (cx * T, cy * T))
    sheet.paste(Image.new("RGBA", (64, 64), CLEAR), (9 * T, 2 * T))
    sheet.paste(chest_img, (9 * T, 2 * T))
    sheet.save(path)


def preview(cells, chest_img, path):
    order = [
        [(2, 1), (3, 1), (0, 2), (0, 4), (3, 5), (1, 2), (2, 2), (3, 4)],
        [(1, 5), (3, 2), (0, 3), (1, 4), (2, 5), (1, 3), (2, 3), (2, 4)],
        [(6, 1), (7, 1), (4, 2), (8, 4), (11, 4), (5, 2), (6, 2), (8, 7)],
        [(7, 2), (4, 3), (8, 5), (11, 5), (5, 3), (6, 3), (9, 5), (10, 5)],
        [(3, 3), (2, 6), (7, 3), (9, 0), (8, 0), (8, 2), (9, 1), (3, 6)],
        [(4, 6), (5, 6), (6, 6), (7, 6)],
    ]
    s = 4
    out = Image.new("RGBA", (8 * T * s + 64 * s, len(order) * T * s), hexc("#6aa84f"))
    for row, cs in enumerate(order):
        for col, c in enumerate(cs):
            out.alpha_composite(cells[c].resize((T * s, T * s), Image.NEAREST), (col * T * s, row * T * s))
    out.alpha_composite(chest_img.resize((64 * s, 64 * s), Image.NEAREST), (8 * T * s, 4 * T * s))
    out.save(path)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--preview")
    args = ap.parse_args()
    cells, chest_img = build()
    if args.preview:
        preview(cells, chest_img, args.preview)
    else:
        write(cells, chest_img)


if __name__ == "__main__":
    main()
