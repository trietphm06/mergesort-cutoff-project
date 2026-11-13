#!/usr/bin/env python3
from pathlib import Path
import random

ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = ROOT / "data"
SIZES = [10_000, 50_000, 100_000, 250_000, 500_000]
SEED = 20261111
VALUE_LIMIT = 10_000_000

def make_data(pattern: str, n: int, seed: int) -> list[int]:
    rng = random.Random(seed)
    if pattern == "random":
        return [rng.randint(-VALUE_LIMIT, VALUE_LIMIT) for _ in range(n)]
    if pattern == "sorted":
        return list(range(n))
    if pattern == "reverse":
        return list(range(n, 0, -1))
    if pattern == "nearly_sorted":
        values = list(range(n))
        for _ in range(max(1, n // 100)):  # about 1% random swaps
            i, j = rng.randrange(n), rng.randrange(n)
            values[i], values[j] = values[j], values[i]
        return values
    raise ValueError(pattern)

def main():
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    for pattern in ["random", "sorted", "reverse", "nearly_sorted"]:
        for n in SIZES:
            values = make_data(pattern, n, SEED + n)
            path = DATA_DIR / f"{pattern}_n{n}.txt"
            with path.open("w", encoding="utf-8") as f:
                for start in range(0, n, 1000):
                    f.write(" ".join(map(str, values[start:start + 1000])) + "\n")
            print(f"Created {path.name} ({n:,} integers)")
    (DATA_DIR / "DATASET_INFO.txt").write_text(
        f"Seed base: {SEED}\nSizes: {SIZES}\n"
        "Patterns: random, sorted, reverse, nearly_sorted\n"
        "nearly_sorted: approximately 1% random swaps from ascending order\n",
        encoding="utf-8"
    )

if __name__ == "__main__":
    main()
