## Instructions for AI coding agents

This repo is a compact Java teaching project (2A POO) built on a small GUI library (`lib/gui.jar`) with three families of demos under `src/`: cellular automata, bouncing balls, and a multi-agents “boids” ecosystem. These notes capture only what you need to be productive quickly here.

## Big picture and layout
- Bytecode goes to `bin/`. Always include `lib/gui.jar` on the classpath.
- Demos under `src/` using `gui.jar`:
  - Default package: `src/TestInvader.java` (minimal `Simulable` example)
  - `LKhalaya/` (cellular automata): `CellularGrid`, `CellularSimulator`, `JeuVie*`, tests like `TestConway`
  - `Koora/` (bouncing balls): `Balls`, `BallsSimulator`, `BallsUpdateEvent`, `TestBallsSimulator`
  - `multi_agents/` (boids ecosystem): `logic/`, `EvenT/`, `simulation/`, `TestTest/`

## Core patterns that matter
- GUI loop (all demos):
  - Create `new GUISimulator(w,h,Color.BLACK)`.
  - Implement `gui.Simulable`; call `gui.setSimulable(this)` in constructor.
  - Redraw each frame via `gui.reset()` + `gui.addGraphicalElement(...)`.
- Cellular automata factorization (`LKhalaya/`):
  - `CellularGrid` is the model (`step()`, `reInit()`).
  - `CellularSimulator` owns the `GUISimulator` and implements `next()/restart()` by delegating to the grid; subclasses implement `draw()`.
- Multi-agents architecture (`src/multi_agents/`):
  - Event-driven simulation: `EvenT/EventManager` schedules `BoidmàjEvent` for each `logic/BoidSystem`.
  - `simulation/BoidSimulateur` implements `Simulable`, holds a `GUISimulator` and `EventManager`, manages multiple systems with colors, and links inter-group interactions via `linkSystems()`.
  - `logic/BoidSystem` is the base model (cohésion/alignement/séparation; FOV; Vmax/Fmax); `ProieBoidSystem` and `PredateurBoidSystem` supply behavior; boids carry energy and can die. Rendering uses `RotatedImageElement` and darkens color when energy < 30; image path defaults to `doc/resources/glass.png`.

## Build and run (macOS/Linux use `:` in classpath; Windows `;`)
- Default demo:
  - Compile: `javac -d bin -classpath lib/gui.jar src/TestInvader.java`
  - Run:     `java -classpath bin:lib/gui.jar TestInvader`
- Cellular automata and balls (examples):
  - Conway: `javac -d bin -classpath lib/gui.jar src/LKhalaya/*.java` → `java -classpath bin:lib/gui.jar LKhalaya.TestConway`
  - Balls:  `javac -d bin -classpath lib/gui.jar src/Koora/*.java`     → `java -classpath bin:lib/gui.jar Koora.TestBallsSimulator`
- Multi-agents (preferred: Makefile in `src/multi_agents`):
  - From repo root: `make run-boids` | `make run-multi` | `make run-ecosystem` (delegates to `src/multi_agents/Makefile`).
  - Or inside `src/multi_agents`: `make` (compile), then `make run`, `make run-multi`, `make run-ecosystem`.
  - Direct run without Makefile: `javac -d bin -classpath lib/gui.jar src/multi_agents/logic/*.java src/multi_agents/EvenT/*.java src/multi_agents/simulation/*.java src/multi_agents/TestTest/*.java` → `java -classpath bin:lib/gui.jar multi_agents.TestTest.TestEcosystem`

## Conventions and gotchas
- Keep package names aligned with folder names (e.g., `src/LKhalaya/...` → `package LKhalaya;`; `src/multi_agents/...` → `package multi_agents.*;`). Run using fully qualified class names (FQCN).
- Don’t modify `lib/gui.jar` or `doc/` (GUI docs are in `doc/index.html`).
- Multi-agents rendering uses a PNG path; if missing, `RotatedImageElement` falls back to a circle. Prefer a relative path (default is `doc/resources/glass.png`).
- Blank window? Ensure `gui.setSimulable(this)` is called and that `draw()` happens in `next()/restart()`.

## Handy recipes
- New demo in a package (e.g., `Koora`): create `src/Koora/MyDemo.java` with `package Koora;` and a `main` that builds a `GUISimulator`, implements `Simulable`, and draws via `Oval`/`Rectangle`/`Text`.
- Non-grid simulators can follow the `multi_agents/simulation/BoidSimulateur` pattern: drive updates via the event manager and keep rendering in a single `draw()`.

Key references: `README.md` (quick runs), repo `Makefile` (root wrappers), `src/multi_agents/Makefile` (detailed targets), and `doc/gui/*` for GUI APIs.

If anything is unclear or seems inconsistent (e.g., class or folder names), tell me and I’ll tighten the docs with examples from the exact files you’re editing.
