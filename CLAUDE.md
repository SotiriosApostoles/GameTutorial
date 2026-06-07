# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A [libGDX](https://libgdx.com/) Kotlin game project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff), following along with the "Mystic Woods" tutorial. Uses [KTX](https://libktx.github.io/) idiomatic Kotlin wrappers, [Fleks](https://github.com/Quillraven/Fleks) ECS, and gdx-box2d.

## Build & Run

Gradle wrapper is checked in. `org.gradle.daemon=false` in `gradle.properties`, so commands are non-daemon by default.

- `./gradlew lwjgl3:run` — launches the desktop game (the only runnable platform).
- `./gradlew lwjgl3:jar` — builds runnable JAR at `lwjgl3/build/libs/MysticWoodsTutorial-<version>.jar`. Per-OS variants: `jarMac`, `jarLinux`, `jarWin`.
- `./gradlew build` — full build of all subprojects.
- `./gradlew core:test` — runs core unit tests (none yet).
- `./gradlew generateAssetList` — regenerates `assets/assets.txt` (an index of every file under `assets/`); already wired as a `processResources` dependency, so it runs as part of normal builds. Re-run manually after adding/removing asset files if you need the txt up to date outside a build.
- Native image builds via GraalVM are gated behind `enableGraalNative=true` in `gradle.properties` (default false); enabling adds `lwjgl3/nativeimage.gradle` and the `construo` plugin produces per-platform distributions.

JVM target is **11** for both Java and Kotlin compilation. Kotlin version, libGDX version, KTX version, etc. are centralized in `gradle.properties`.

## Architecture

### Module layout

- `core/` — platform-agnostic game logic. All gameplay code lives here.
- `lwjgl3/` — desktop launcher. `Lwjgl3Launcher.kt` wires up `MysticWoodsTutorial` (the `KtxGame`) and configures the window. `StartupHelper` handles the macOS `-XstartOnFirstThread` restart dance.
- `assets/` — runtime assets bundled into the LWJGL3 jar via `sourceSets.main.resources.srcDirs += rootProject.file('assets')`. The `run` task's `workingDir` is `assets/`, so asset paths in code are relative to that (note that `GameScreen` currently uses `"assets/graphics/player.png"`, which only resolves because it's loaded as a classpath resource).
- `assets_raw/` — source art not packed/shipped.

### Game loop & ECS

The render pipeline is **Fleks ECS driving a scene2d `Stage`**, not a vanilla scene2d render. The key pattern, established in `GameScreen` + `RenderSystem` + `ImageComponent`:

1. `GameScreen` owns a `Stage` (with `ExtendViewport(16f, 9f)` — units are **world tiles**, not pixels) and a Fleks `World`. The stage is registered as an injectable under the string key `"GameStage"`.
2. Entities carry `ImageComponent`, whose `onAdd`/`onRemove` lifecycle hooks add/remove a scene2d `Image` actor to/from the stage. This means **creating an entity with an `ImageComponent` is what puts an actor on the stage** — don't add actors directly.
3. `RenderSystem` is an `IteratingSystem` sorted by `compareEntityBy(ImageComponent)`. Each tick it calls `stage.act` + `stage.draw` once, then iterates entities in sorted order calling `image.toFront()`. The `ImageComponent.compareTo` implementation sorts by `layer` first, then by descending `y` (so things lower on screen draw in front — classic 2D depth), then by descending `x`. **To control draw order, set `ImageComponent.layer` and position the image; don't reorder the stage's actor list directly.**
4. `GameScreen.render` just calls `world.update(delta)`. The Stage's update/draw happens inside `RenderSystem.onTick`, not in the screen.

### When adding systems / components

- Register Fleks systems and injectables inside the `configureWorld { ... }` block in `GameScreen`. Order in `systems { ... }` is execution order per tick.
- New components go under `core/src/main/kotlin/io/github/eknekron/mysticwoodstutorial/component/`. Follow the `ImageComponent` pattern: implement `Component<T>` with a `companion object : ComponentType<T>()`.
- New systems go under `core/src/main/kotlin/io/github/eknekron/mysticwoodstutorial/` (no `system/` subfolder yet — match existing layout when one is established).
- Anything that needs the stage should `inject<Stage>("GameStage")` rather than receive it through a different mechanism.

## Conventions

- Kotlin source uses 4-space indent (`.editorconfig`). Gradle files use 2-space indent.
- Use KTX wrappers where available (`ktx.app.KtxGame`/`KtxScreen`, `ktx.assets.disposeSafely`, `ktx.log.logger`) instead of raw libGDX equivalents.
- `Gdx.app.logLevel` is set to `LOG_DEBUG` in `MysticWoodsTutorial.create()`; use `ktx.log.logger<T>()` companions for logging.
