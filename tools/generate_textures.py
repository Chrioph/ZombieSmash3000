#!/usr/bin/env python3
"""
Generates the world textures (terrain tiles and world objects) of ZombieSmash3000.

All textures are 32x32 pixel art (the tree is 32x64) that the game scales up 4x.
The generator is deterministic, so running it again produces the same images.

    python3 tools/generate_textures.py            # writes into res/textures
    python3 tools/generate_textures.py --preview out.png

Written cells in res/textures/Sheet2.png (column, row of the 32px grid):
    rock (0,0), log (1,0), dirt (2,0), grass (3,0), stone (0,1), finish (8,1),
    solid wood (11,0), wood (10,1), grass variants (12..14,0), dirt variants (12..13,1)
Separate file: res/textures/Tree.png (32x64)

Requires Pillow.
"""
import argparse
import math
import os
import random

from PIL import Image

T = 32
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TEXTURES = os.path.join(ROOT, "res", "textures")


def hexc(h, a=255):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


GRASS = [hexc(c) for c in ("#3f8a35", "#58ab45", "#6fc451", "#89d662", "#ace67f")]
DIRT = [hexc(c) for c in ("#6e4c2f", "#8e653b", "#a87a48", "#c19058", "#dbac70")]
STONE = [hexc(c) for c in ("#4f5464", "#737a8b", "#8e95a7", "#a8afc0", "#c6ccd9")]
ROCK = [hexc(c) for c in ("#4a443c", "#7a7266", "#9a9182", "#b5ac9a", "#cfc6b2", "#e6dfcd")]
LEAF = [hexc(c) for c in ("#245a2a", "#317a35", "#43973f", "#5ab34d", "#7dcb60", "#a5e27e")]
BARK = [hexc(c) for c in ("#3d2414", "#61391f", "#82522c", "#a06838", "#bf8248")]
WOOD = [hexc(c) for c in ("#3a2211", "#62391b", "#865027", "#a96733", "#c98546", "#e0a560")]
GOLD = [hexc(c) for c in ("#6b4a0c", "#a87a14", "#d9a922", "#f5d45a")]
SHADOW = (0, 0, 0, 50)
CLEAR = (0, 0, 0, 0)

# 4x4 Bayer matrix for ordered dithering, values in [0, 1)
BAYER = [[0, 8, 2, 10], [12, 4, 14, 6], [3, 11, 1, 9], [15, 7, 13, 5]]
BAYER = [[(v + 0.5) / 16 for v in row] for row in BAYER]


def new(w=T, h=T, fill=CLEAR):
    return Image.new("RGBA", (w, h), fill)


def tileable_noise(rng, size=T, octaves=((4, 1.0), (8, 0.5), (16, 0.25))):
    """Seamless value noise in [0, 1] (wraps around at the edges)."""
    out = [[0.0] * size for _ in range(size)]
    total = sum(a for _, a in octaves)
    for cells, amp in octaves:
        grid = [[rng.random() for _ in range(cells)] for _ in range(cells)]
        step = size / cells
        for y in range(size):
            for x in range(size):
                gx, gy = x / step, y / step
                x0, y0 = int(gx) % cells, int(gy) % cells
                x1, y1 = (x0 + 1) % cells, (y0 + 1) % cells
                fx, fy = gx - int(gx), gy - int(gy)
                # smoothstep
                fx, fy = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy)
                top = grid[y0][x0] * (1 - fx) + grid[y0][x1] * fx
                bot = grid[y1][x0] * (1 - fx) + grid[y1][x1] * fx
                out[y][x] += (top * (1 - fy) + bot * fy) * amp
    return [[v / total for v in row] for row in out]


def dither_pick(palette, value, x, y, lo=0, hi=None):
    """Maps value in [0, 1] onto palette[lo..hi] with ordered dithering."""
    hi = len(palette) - 1 if hi is None else hi
    v = lo + max(0.0, min(1.0, value)) * (hi - lo)
    base = int(v)
    if base >= hi:
        return palette[hi]
    return palette[base + 1] if (v - base) > BAYER[y % 4][x % 4] else palette[base]


def put(img, x, y, c, wrap=True):
    w, h = img.size
    if wrap:
        x, y = x % w, y % h
    elif not (0 <= x < w and 0 <= y < h):
        return
    img.putpixel((x, y), c)


def contrast(n, amount):
    """Stretches noise around 0.5."""
    return [[0.5 + (v - 0.5) * amount for v in row] for row in n]


# ---------------------------------------------------------------- terrain

def grass(seed, flowers=0):
    rng = random.Random(seed)
    img = new()
    n = contrast(tileable_noise(rng), 2.2)
    for y in range(T):
        for x in range(T):
            img.putpixel((x, y), dither_pick(GRASS, n[y][x], x, y, 1, 3))
    # small tufts of grass blades: dark root, light tip. Details stay away from the
    # edges, so they are not cut off next to dirt or stone tiles.
    for _ in range(9):
        x, y = rng.randint(1, T - 6), rng.randint(4, T - 3)
        for dx, h in ((0, 2), (2, 3), (4, 2)) if rng.random() < 0.5 else ((0, 3), (2, 2)):
            for i in range(h):
                put(img, x + dx, y - i, GRASS[4] if i == h - 1 else GRASS[3], wrap=False)
            put(img, x + dx, y + 1, GRASS[0], wrap=False)
    # a few darker specks for depth
    for _ in range(14):
        put(img, rng.randrange(T), rng.randrange(T), GRASS[1])
    for _ in range(flowers):
        x, y = rng.randint(4, T - 5), rng.randint(4, T - 5)
        petal = rng.choice([hexc("#f4f1e6"), hexc("#f6d84a"), hexc("#e46f8c")])
        for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
            put(img, x + dx, y + dy, petal)
        put(img, x, y, hexc("#f2a531") if petal != hexc("#f6d84a") else hexc("#c4701d"))
        put(img, x + 1, y + 2, GRASS[0])
    return img


def dirt(seed):
    rng = random.Random(seed)
    img = new()
    n = contrast(tileable_noise(rng), 2.0)
    for y in range(T):
        for x in range(T):
            img.putpixel((x, y), dither_pick(DIRT, n[y][x], x, y, 1, 3))
    # pebbles with a highlight and a shadow
    for _ in range(6):
        x, y = rng.randrange(T), rng.randrange(T)
        w = rng.choice((1, 2, 2, 3))
        for dx in range(w):
            put(img, x + dx, y, DIRT[4] if dx == 0 else DIRT[3])
            put(img, x + dx, y + 1, DIRT[0])
    for _ in range(18):
        put(img, rng.randrange(T), rng.randrange(T), DIRT[0])
    for _ in range(8):
        put(img, rng.randrange(T), rng.randrange(T), DIRT[4])
    return img


def wrapped_dist(ax, ay, bx, by, size=T):
    dx = min(abs(ax - bx), size - abs(ax - bx))
    dy = min(abs(ay - by), size - abs(ay - by))
    return math.hypot(dx, dy * 1.15)


def stone_wall(seed):
    """Cobblestone wall built from seamless Voronoi cells."""
    rng = random.Random(seed)
    img = new()
    seeds = []
    for gy in range(4):
        for gx in range(4):
            seeds.append((gx * 8 + rng.uniform(1, 7), gy * 8 + rng.uniform(1, 7)))
    shade = [rng.choice((2, 2, 3)) for _ in seeds]
    owner = [[0] * T for _ in range(T)]
    edge = [[False] * T for _ in range(T)]
    for y in range(T):
        for x in range(T):
            d = sorted((wrapped_dist(x + 0.5, y + 0.5, sx, sy), i) for i, (sx, sy) in enumerate(seeds))
            owner[y][x] = d[0][1]
            edge[y][x] = d[1][0] - d[0][0] < 1.1
    n = tileable_noise(rng, octaves=((16, 1.0),))
    for y in range(T):
        for x in range(T):
            if edge[y][x]:
                img.putpixel((x, y), STONE[0])
                continue
            o = owner[y][x]
            up_left = edge[(y - 1) % T][(x - 1) % T] or edge[(y - 1) % T][x] or edge[y][(x - 1) % T]
            down_right = edge[(y + 1) % T][(x + 1) % T] or edge[(y + 1) % T][x] or edge[y][(x + 1) % T]
            level = shade[o]
            if up_left and not down_right:
                level += 1
            elif down_right and not up_left:
                level -= 1
            elif n[y][x] > 0.72:
                level += 1 if level < 4 else 0
            img.putpixel((x, y), STONE[max(1, min(4, level))])
    return img


def finish():
    img = new()
    for y in range(T):
        for x in range(T):
            light = ((x // 6) + (y // 6)) % 2 == 0
            img.putpixel((x, y), hexc("#ecebe4") if light else hexc("#2b2b33"))
    # golden frame with bevel
    for i in range(T):
        for k, c in ((0, GOLD[0]), (1, GOLD[2]), (2, GOLD[1])):
            put(img, i, k, c)
            put(img, k, i, c)
            put(img, i, T - 1 - k, GOLD[0] if k == 0 else GOLD[1])
            put(img, T - 1 - k, i, GOLD[0] if k == 0 else GOLD[1])
    for i in range(1, T - 1):
        put(img, i, 1, GOLD[3])
        put(img, 1, i, GOLD[3])
    return img


# ---------------------------------------------------------------- objects

def shaded_blob(img, cx, cy, rx, ry, palette, rng, wobble=0.0, light=(-0.55, -0.7, 0.45), outline=True):
    """Fills an (irregular) ellipse with diffuse shading from the top left."""
    lx, ly, lz = light
    ln = math.sqrt(lx * lx + ly * ly + lz * lz)
    lx, ly, lz = lx / ln, ly / ln, lz / ln
    phases = [rng.uniform(0, 2 * math.pi) for _ in range(3)]
    w, h = img.size
    inside = set()
    for y in range(h):
        for x in range(w):
            dx, dy = (x + 0.5 - cx) / rx, (y + 0.5 - cy) / ry
            ang = math.atan2(dy, dx)
            r = 1 + wobble * (math.sin(3 * ang + phases[0]) * 0.5 + math.sin(5 * ang + phases[1]) * 0.3
                              + math.sin(7 * ang + phases[2]) * 0.2)
            d2 = dx * dx + dy * dy
            if d2 > r * r:
                continue
            inside.add((x, y))
            nz = math.sqrt(max(0.0, r * r - d2)) / r
            nx, ny = dx / r, dy / r
            lum = max(0.0, nx * lx + ny * ly + nz * lz)
            img.putpixel((x, y), dither_pick(palette, lum * 1.1, x, y, 1, len(palette) - 1))
    if outline:
        for (x, y) in list(inside):
            for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                if (nx, ny) not in inside and 0 <= nx < w and 0 <= ny < h:
                    img.putpixel((x, y), palette[0])
                    break
    return inside


def ground_shadow(img, cx, cy, rx, ry):
    w, h = img.size
    for y in range(h):
        for x in range(w):
            if ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2 <= 1 and img.getpixel((x, y))[3] == 0:
                img.putpixel((x, y), SHADOW)


def rock(seed=11):
    rng = random.Random(seed)
    img = new()
    ground_shadow(img, 16.5, 25.5, 14, 4.5)
    inside = shaded_blob(img, 16, 18, 13, 10, ROCK, rng, wobble=0.12)
    # a few cracks
    for _ in range(2):
        x, y = rng.randint(10, 20), rng.randint(14, 20)
        for _ in range(rng.randint(3, 5)):
            if (x, y) in inside:
                img.putpixel((x, y), ROCK[1])
            x += rng.choice((1, 1, 0))
            y += rng.choice((0, 1))
    # moss on top
    for _ in range(10):
        x, y = rng.randint(9, 22), rng.randint(9, 12)
        if (x, y) in inside and img.getpixel((x, y)) != ROCK[0]:
            img.putpixel((x, y), rng.choice((LEAF[3], LEAF[4])))
    return img


def tree(seed=21):
    rng = random.Random(seed)
    img = new(T, 64)
    ground_shadow(img, 16.5, 61, 12, 3)
    # trunk with roots
    for y in range(30, 62):
        flare = 2 if y > 58 else (1 if y > 55 else 0)
        x0, x1 = 13 - flare, 19 + flare
        for x in range(x0, x1 + 1):
            if x == x0 or x == x1:
                c = BARK[0]
            else:
                rel = (x - x0) / max(1, x1 - x0)
                c = BARK[3] if rel < 0.35 else BARK[2] if rel < 0.7 else BARK[1]
                if (y * 3 + x) % 7 == 0:
                    c = BARK[1]
            img.putpixel((x, y), c)
    # canopy from several shaded clusters, back to front
    canopy = new(T, 64)
    clusters = [(16, 22, 13, 11), (9, 17, 8, 8), (23, 16, 8, 8), (16, 10, 10, 9), (11, 25, 7, 6), (22, 25, 7, 6)]
    covered = set()
    for cx, cy, rx, ry in clusters:
        layer = new(T, 64)
        pts = shaded_blob(layer, cx, cy, rx, ry, LEAF, rng, wobble=0.15, outline=False)
        for p in pts:
            canopy.putpixel(p, layer.getpixel(p))
        covered |= pts
    # leaf texture and outline
    n = tileable_noise(rng, size=64, octaves=((16, 1.0), (32, 0.6)))
    for (x, y) in covered:
        c = canopy.getpixel((x, y))
        idx = LEAF.index(c) if c in LEAF else 3
        if n[y][x] > 0.68 and idx < 5:
            idx += 1
        elif n[y][x] < 0.3 and idx > 1:
            idx -= 1
        canopy.putpixel((x, y), LEAF[idx])
    for (x, y) in covered:
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if (nx, ny) not in covered:
                canopy.putpixel((x, y), LEAF[0])
                break
    img.alpha_composite(canopy)
    return img


def log():
    img = new()
    ground_shadow(img, 16, 24, 13, 3)
    # body
    for y in range(11, 23):
        for x in range(4, 25):
            rel = (y - 11) / 11
            c = BARK[3] if rel < 0.25 else BARK[2] if rel < 0.65 else BARK[1]
            if y in (11, 22):
                c = BARK[0]
            elif (x * 5 + y * 2) % 9 == 0:
                c = BARK[1]
            img.putpixel((x, y), c)
    for y in range(11, 23):
        img.putpixel((4, y), BARK[0])
    # cut end with rings
    cx, cy = 25, 16.5
    for y in range(10, 24):
        for x in range(20, 31):
            dx, dy = (x + 0.5 - cx) / 4.6, (y + 0.5 - cy) / 6.4
            d = math.sqrt(dx * dx + dy * dy)
            if d <= 1:
                if d > 0.82:
                    c = BARK[0]
                elif d > 0.62:
                    c = WOOD[4]
                elif d > 0.45:
                    c = WOOD[3]
                elif d > 0.25:
                    c = WOOD[4]
                else:
                    c = WOOD[3]
                img.putpixel((x, y), c)
    return img


def planks(seed, dark=False):
    """Placeable wood block: horizontal planks; the solid version is reinforced with iron."""
    rng = random.Random(seed)
    pal = WOOD[:5] if not dark else [hexc(c) for c in ("#24150a", "#3e2512", "#56331a", "#6f4423", "#8a5630")]
    img = new()
    n = tileable_noise(rng, octaves=((8, 1.0), (32, 0.5)))
    for y in range(T):
        plank = y // 8
        offset = (plank * 11) % T
        for x in range(T):
            yy = y % 8
            if yy == 7:
                c = pal[0]
            elif (x + offset) % 16 == 0:
                c = pal[0]
            else:
                grain = n[y][(x * 3) % T]
                base = 3 if yy < 2 else 2
                c = pal[base + 1] if grain > 0.7 and base < 4 else pal[base - 1] if grain < 0.28 else pal[base]
            img.putpixel((x, y), c)
    # nails
    for plank in range(4):
        for xn in (2, 13, 18, 29):
            if (xn + (plank * 11) % T) % 16 in (2, 13):
                put(img, xn, plank * 8 + 3, hexc("#b9bec7"))
                put(img, xn, plank * 8 + 4, hexc("#5a5f6a"))
    # frame
    for i in range(T):
        for k in (0, T - 1):
            put(img, i, k, pal[0])
            put(img, k, i, pal[0])
    if dark:
        iron, iron_light, iron_dark = hexc("#7d8494"), hexc("#b5bccb"), hexc("#3a3e48")
        for i in range(T):
            for band in (3, 4, T - 5, T - 4):
                put(img, i, band, iron if band in (4, T - 4) else iron_light)
                put(img, band, i, iron if band in (4, T - 4) else iron_light)
            put(img, i, 5, iron_dark)
            put(img, 5, i, iron_dark)
        for x, y in ((4, 4), (T - 5, 4), (4, T - 5), (T - 5, T - 5)):
            put(img, x, y, iron_light)
    return img


# ---------------------------------------------------------------- output

def build():
    return {
        "grass": [grass(101, flowers=0), grass(102, flowers=1), grass(103, flowers=0), grass(104, flowers=0)],
        "dirt": [dirt(201), dirt(202), dirt(203)],
        "stone": stone_wall(301),
        "finish": finish(),
        "rock": rock(),
        "log": log(),
        "wood": planks(401),
        "solidWood": planks(402, dark=True),
        "tree": tree(),
    }


def write(tex):
    sheet_path = os.path.join(TEXTURES, "Sheet2.png")
    sheet = Image.open(sheet_path).convert("RGBA")

    def place(img, cx, cy):
        sheet.paste(CLEAR_CELL, (cx * T, cy * T))
        sheet.paste(img, (cx * T, cy * T))

    place(tex["rock"], 0, 0)
    place(tex["log"], 1, 0)
    place(tex["dirt"][0], 2, 0)
    place(tex["grass"][0], 3, 0)
    place(tex["stone"], 0, 1)
    place(tex["finish"], 8, 1)
    place(tex["solidWood"], 11, 0)
    place(tex["wood"], 10, 1)
    for i, g in enumerate(tex["grass"][1:]):
        place(g, 12 + i, 0)
    for i, d in enumerate(tex["dirt"][1:]):
        place(d, 12 + i, 1)
    sheet.save(sheet_path)
    tex["tree"].save(os.path.join(TEXTURES, "Tree.png"))


CLEAR_CELL = Image.new("RGBA", (T, T), CLEAR)


def preview(tex, path):
    """Draws a small sample scene at the in-game 4x scale."""
    s = 4
    cols, rows = 10, 6
    scene = Image.new("RGBA", (cols * T, rows * T))
    layout = [
        "SSSSSSSSSS",
        "SgggDDgggS",
        "SggDDggFgS",
        "SgDDgggggS",
        "SgggggSSSS",
        "SSSSSSSSSS",
    ]
    rng = random.Random(5)
    for y, row in enumerate(layout):
        for x, c in enumerate(row):
            img = {"S": tex["stone"], "D": rng.choice(tex["dirt"]), "F": tex["finish"]}.get(c) or rng.choice(tex["grass"])
            scene.paste(img, (x * T, y * T))
    scene.alpha_composite(tex["tree"], (1 * T + 8, 0))
    scene.alpha_composite(tex["rock"], (5 * T, 3 * T))
    scene.alpha_composite(tex["log"], (4 * T, 1 * T))
    scene.alpha_composite(tex["wood"], (7 * T, 3 * T))
    scene.alpha_composite(tex["solidWood"], (8 * T, 3 * T))
    scene = scene.resize((cols * T * s, rows * T * s), Image.NEAREST)
    scene.save(path)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--preview", help="write a preview scene instead of the game textures")
    args = ap.parse_args()
    tex = build()
    if args.preview:
        preview(tex, args.preview)
    else:
        write(tex)


if __name__ == "__main__":
    main()
