# Changelog

All notable changes to **Greews Races** are listed here. Minecraft versions are maintained on **separate Git branches** (`main` for the latest 1.21.11 line, `mc-1.21.10`, `mc-1.21.1`, `mc-1.20.1`); pick the branch / JAR that matches your game version.

---

## Java runtime (which JAR needs which Java)

| Minecraft | Branch        | Java (runtime) | Notes                                      |
|-----------|---------------|----------------|--------------------------------------------|
| **1.21.11** | `main`        | **21+**        | Matches `fabric.mod.json` `java: ">=21"`.  |
| **1.21.10** | `mc-1.21.10`  | **21+**        | Same as above.                             |
| **1.21.1**  | `mc-1.21.1`   | **21+**        | Same as above.                             |
| **1.20.1**  | `mc-1.20.1`   | **17+**        | Mixin compatibility `JAVA_17`; build targets Java 17. |

Use a **Java version equal or higher** than listed. The mod JAR declares requirements in `fabric.mod.json`; the launcher must use a compatible JVM.

---

## 1.21.11 (`main`)

**Java:** 21 or newer.

### Added / changed

- **Client config** (`config/greewsraces-client.json`): `version` field for future migrations; **`showRaceTabPrefix`** (default `true`) — toggles the `[Race]` prefix before player names in the **tab list** (client-side).
- **Evernight biome** datapack uses the **current (1.21.11+) biome JSON** format (including updated effects / attributes where applicable).
- Server-side race/language flows, networking sync, optional TerraBlender Evernight biome (gated by server config), commands, and related UI continue to evolve on this branch.

---

## 1.21.10 (`mc-1.21.10`)

**Java:** 21 or newer.

### Fixes / compatibility

- **Evernight biome** (`data/greewsraces/worldgen/biome/evernight.json`) uses a **legacy-compatible** structure (integer colors under `effects`, etc.) so the game does not fail registry load on this version. **Carvers** use the **1.21.10** style: top-level **array** (e.g. empty `[]`), not the pre–1.21.10 object form.
- **Client config** aligned with main: **`showRaceTabPrefix`** in `greewsraces-client.json`; tab list mixin respects it.
- Tab list mixin updated to respect **`showRaceTabPrefix`**.

---

## 1.21.1 (`mc-1.21.1`)

**Java:** 21 or newer.

### Fixes / compatibility

- **Evernight biome** uses the **legacy** `effects` / spawners layout suitable for 1.21.1.
- **Carvers** must be an **object** with an **`air`** list: `"carvers": { "air": [] }`. A bare `"carvers": []` **fails to parse** on 1.21.1 and causes “Failed to load registries” (while 1.21.10 expects the newer array form — **do not mix** biome JSON between those versions).
- **Client config:** **`showRaceTabPrefix`**; tab list mixin respects it.

---

## 1.20.1 (`mc-1.20.1`)

**Java:** 17 or newer (typical for Minecraft 1.20.1).

### Fixes / compatibility

- **Mixin compatibility** set to **`JAVA_17`** in `greewsraces.mixins.json` so the mod loads on a **Java 17** runtime (Java 21–only mixin level caused startup failure on Java 17).
- **Evernight biome** uses the same **`carvers`: `{ "air": [] }`** object shape as vanilla 1.20.1 (not a bare empty array).
- **Client config:** **`showRaceTabPrefix`**; tab list mixin respects it.
- Mod is compiled with **Java 17** (`release` 17).

---

## Earlier releases

Prior tagged versions (if any) are described in Git history per branch.
