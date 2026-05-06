# HitBoxPlus Rebuild Plan

## Goals

- Recreate the mod in this repository as a Fabric client mod.
- Use Minecraft `1.21.11` as the base version.
- Use Stonecutter for multi-version support from the start.
- Use YACL for both the config GUI and the config storage layer where practical.
- Improve the original config design.
- Guarantee that hitbox lookup during rendering is effectively instant: no per-frame map merging, parsing, or fallback reconstruction on the hot path.

## Non-goals for the first pass

- Broad version support before the base version is stable.
- Publish automation before the mod compiles, runs, and configures correctly.
- Premature abstraction for loaders other than Fabric.

## High-level approach

Build the newest supported Fabric version first, make the render/config pipeline correct and fast there, then expand to additional Minecraft versions with Stonecutter only where API drift forces code differences.

The old project mixed config persistence, normalization, UI construction, and runtime lookup too tightly. The rebuild should separate them into:

1. Authoritative persisted config data.
2. A normalization/validation step after load.
3. A precomputed runtime cache for instant lookup.
4. A YACL screen builder that edits the authoritative config.

## Proposed project phases

### 1. Scaffold the project

- Create the Gradle wrapper and shared Gradle build files.
- Set up `settings.gradle.kts` with Stonecutter project/version nodes.
- Set up `stonecutter.gradle.kts` for active version selection and version parameters.
- Add root `gradle.properties` for shared mod/plugin properties.
- Add `versions/<mc>/gradle.properties` for version-specific Minecraft, Yarn, Fabric API, YACL, and ModMenu coordinates.
- Keep the initial supported version list intentionally small:
  - `1.21.11` required
  - optionally one adjacent version after the base target works cleanly

### 2. Configure the Fabric build

- Add Fabric Loom and Fabric repositories.
- Add Stonecutter-compatible shared build logic.
- Add YACL and ModMenu repositories:
  - `https://maven.isxander.dev/releases`
  - `https://maven.terraformersmc.com/`
- Put YACL and ModMenu versions in per-version properties, not hardcoded in the build script.
- Expand `fabric.mod.json` from Gradle properties.

### 3. Establish the mod skeleton

- Add the Fabric client entrypoint.
- Add the mixin config and mixin package.
- Add the ModMenu entrypoint.
- Add a minimal package layout with clear responsibilities:
  - `mod/` or root package for entrypoint/runtime wiring
  - `mixin/` for rendering hooks
  - `config/` for persisted config and YACL integration
  - `runtime/` or `service/` for precomputed lookup/cache logic
  - `data/` for static entity grouping/default color definitions

### 4. Design a better config model

Use YACL Config API as the primary config persistence mechanism instead of recreating the old custom Jackson stack unless a hard limitation appears.

The config should be designed around clarity and runtime efficiency:

- Global enable toggle.
- Global default hitbox style.
- Group-level defaults.
- Entity-level overrides.
- Player-specific policy separated from generic entity policy.
- Optional named rule sets for future extensibility if that does not complicate the first implementation too much.

Recommended config shape:

- `general`
  - `enabled`
- `players`
  - `self`
  - `friend`
  - `enemy`
  - explicit player name classification lists or sets
- `groups`
  - group -> style
- `entities`
  - entity id -> override

Implementation rules:

- Persist stable identifiers, not runtime objects.
- Normalize all names and ids on load.
- Reject or drop invalid entity ids safely.
- Ensure missing sections are reconstructed with defaults after load.
- Keep migration/version fields explicit if YACL serializer support does not already cover the needed evolution path.

### 5. Make hitbox lookup instant

This is the main runtime requirement.

The renderer-facing API should never do expensive work such as:

- string parsing of entity ids
- registry lookups from raw ids on every query
- repeated fallback resolution chains with allocation
- config normalization on demand
- rebuilding merged maps when the mixin requests a hitbox

Instead, the plan is:

1. Load persisted config.
2. Normalize and validate it once.
3. Compile it into immutable runtime caches.
4. Replace the active cache atomically whenever config changes.

Recommended runtime cache design:

- `EntityType<?> -> ResolvedHitboxStyle` map for all non-player entities.
- Direct resolved values for:
  - self player
  - friend player
  - enemy player
  - default player fallback
- Normalized `String -> PlayerRelation` map or set-based classifier for named players.
- Optional prebuilt `int`/packed color representation if the render API benefits from that.

Important constraint:

When the mixin asks for a hitbox, the answer should be a direct lookup plus at most a tiny fixed amount of branching for player/self/friend/enemy handling.

The runtime resolver should expose something like:

- `ResolvedHitboxStyle forEntity(Entity entity)`
- `ResolvedHitboxStyle forEntityType(EntityType<?> type)`
- `ResolvedHitboxStyle forPlayerName(String name)`

Each of these should be `O(1)` in normal usage.

### 6. Rebuild the render hook on 1.21.11 first

- Re-find the correct `EntityRenderer` hitbox creation hook for `1.21.11`.
- Avoid fragile injection targeting where possible.
- Confirm the injection is stable under current mappings.
- Keep version-specific injection differences behind Stonecutter comments only if necessary.

### 7. Build the YACL GUI around the config model

Follow the current YACL guidance:

- Generate a new YACL screen instance each time the config screen is opened.
- Use a dedicated `createConfigScreen(parent)` entrypoint.
- Keep ModMenu integration as a thin adapter that delegates to that method.

GUI structure should be more intentional than the original:

- General
- Players
- Groups
- Entities

Player editing should be redesigned to avoid stale state:

- If using friend/enemy name collections, edits must reconcile removals and moves, not only append.
- The saved data should always match the visible UI state exactly.

### 8. Recreate static data and defaults

- Port entity grouping data from the old mod.
- Review it against the base target version’s registry.
- Define sensible default colors by group.
- Ensure every non-player entity has a resolved group/default path.

### 9. Add validation and tests

Minimum useful test coverage:

- Every non-player entity type is classified.
- Invalid config entries are handled safely.
- Config normalization reconstructs missing sections.
- Runtime cache compilation produces complete lookup tables.
- Player relation edits reconcile correctly.
- Version-specific source generation still compiles.

If practical, add a focused test that proves the runtime resolver does not perform fallback rebuilding after initialization.

### 10. Expand version support carefully

Only after `1.21.11` is stable:

- Add additional supported versions one by one.
- Put dependency/version drift into `versions/<mc>/gradle.properties`.
- Use Stonecutter conditional code only where APIs differ.
- Re-test the mixin injection and config screen on every added version.

### 11. Developer ergonomics and release prep

- Add shared run directory configuration if useful.
- Add a `buildAndCollect`-style task only after the main build works.
- Add README/changelog placeholders.
- Add a clear note on which Minecraft versions are actually verified, not just declared.

## Suggested implementation order

1. Stonecutter + Loom scaffold.
2. Base Fabric mod metadata and entrypoints.
3. YACL dependency wiring.
4. New config model and serializer.
5. Runtime cache compiler for instant lookup.
6. YACL screen generation.
7. Render mixin on `1.21.11`.
8. Validation tests.
9. Additional versions.

## Architectural constraints to preserve

- Rendering path must use precomputed resolved data only.
- Config save/load must be independent from render-time lookups.
- UI edits must update authoritative config, then rebuild runtime caches.
- Version-specific code should remain minimal and localized.
- No hidden fallback behavior that silently mutates config during rendering.

## Open decisions for review

- Exact additional versions to support beyond `1.21.11`.
- Whether player classification should stay name-based initially or move to a more explicit rule model.
- Whether entity config should support only color in v1, or leave room for additional hitbox style properties.
- Whether to use YACL Config API fully, or fall back to a custom serializer only if a concrete limitation appears.
