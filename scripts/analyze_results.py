#!/usr/bin/env python3

from pathlib import Path
import argparse
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
try:
    from scipy import stats
except ImportError:
    stats = None

ap = argparse.ArgumentParser()
ap.add_argument("csv_file", type=Path)
args = ap.parse_args()
out = args.csv_file.parent
(out / "plots").mkdir(parents=True, exist_ok=True)

df = pd.read_csv(args.csv_file)
df["ms"] = df["time_ns"] / 1e6
g = df.groupby(["pattern", "n", "cutoff"])["ms"]
s = g.agg(runs="count", mean="mean", median="median", std="std", min="min").reset_index()
tcrit = stats.t.ppf(0.975, s["runs"] - 1) if stats else 2.045
s["ci95"] = tcrit * s["std"] / s["runs"] ** 0.5
s.round(4).to_csv(out / "summary.csv", index=False)

lines = ["# Cutoff tốt nhất (theo trung vị) cho từng (pattern, N)", ""]
rows = []
for (p, n), d in s.groupby(["pattern", "n"]):
    b = d.loc[d["median"].idxmin()]
    cmax = d["cutoff"].max()
    edge = "  ⚠ NẰM Ở BIÊN, cần thử cutoff lớn hơn" if b["cutoff"] == cmax else ""
    sub = df[(df.pattern == p) & (df.n == n)]
    msg = []
    for ref in (7, 10):
        if stats and ref in set(d["cutoff"]) and ref != b["cutoff"]:
            pv = stats.ttest_ind(sub[sub.cutoff == b["cutoff"]].ms, sub[sub.cutoff == ref].ms, equal_var=False).pvalue
            gap = 100 * (d[d.cutoff == ref]["median"].iloc[0] / b["median"] - 1)
            msg.append(f"vs {ref}: +{gap:.1f}% (p={pv:.3f}{'' if pv < .05 else ', không ý nghĩa'})")
    rows.append(f"- {p}, N={n:,}: cutoff={int(b['cutoff'])} ({b['median']:.2f} ms){edge}  " + "; ".join(msg))
lines += rows
(out / "best_cutoff.md").write_text("\n".join(lines), encoding="utf-8")
print("\n".join(lines))

for (p, n), d in s.groupby(["pattern", "n"]):
    d = d.sort_values("cutoff")
    plt.figure(figsize=(7, 4))
    plt.errorbar(d["cutoff"], d["mean"], yerr=d["ci95"], marker="o", capsize=3, label="mean ± 95% CI")
    plt.plot(d["cutoff"], d["median"], "s--", alpha=.6, label="median")
    for ref, c in ((7, "r"), (10, "orange")):
        plt.axvline(ref, ls=":", c=c, label=f"giáo trình {ref}")
    plt.xscale("log"); plt.xlabel("CUTOFF (thang log)"); plt.ylabel("ms")
    plt.title(f"{p}, N={n:,}"); plt.grid(alpha=.3); plt.legend(); plt.tight_layout()
    plt.savefig(out / "plots" / f"{p}_n{n}.png", dpi=150); plt.close()

for p in sorted(s.pattern.unique()):
    d = s[s.pattern == p].copy()
    d["rel"] = d["median"] / d.groupby("n")["median"].transform("min")
    plt.figure(figsize=(7, 4))
    for n, dn in d.groupby("n"):
        dn = dn.sort_values("cutoff"); plt.plot(dn["cutoff"], dn["rel"], marker="o", label=f"N={n:,}")
    plt.xscale("log"); plt.xlabel("CUTOFF"); plt.ylabel("median / median tốt nhất")
    plt.title(f"{p}: thời gian tương đối"); plt.grid(alpha=.3); plt.legend(); plt.tight_layout()
    plt.savefig(out / "plots" / f"relative_{p}.png", dpi=150); plt.close()
print("Đã lưu summary.csv, best_cutoff.md, plots/")