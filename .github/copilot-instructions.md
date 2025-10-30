# Instructions for AI coding agents

This is a small Java teaching project (2A POO) built around a provided GUI library (`lib/gui.jar`). These notes capture only what’s needed to be productive quickly in THIS repo.

## Big picture
- Single repo with multiple small demos under `src/` using `gui.jar`:
  - Default package: `src/TestInvader.java` (minimal Simulable example)
  - Packages: `LKhalaya` (cellular automata), `Koora` (bouncing balls), `Multi_Agents` (boids)
- Bytecode goes to `bin/`. Always put `lib/gui.jar` on the classpath when compiling and running.

## Key files
- `README.md` — IDE setup tips and FAQ; confirms `lib/gui.jar` as the GUI dependency.
- `Makefile` — targets only for the invader demo; use it as a template for other packages.
- `doc/index.html` — API docs for `gui` (GraphicalElement, GUISimulator, Simulable, etc.).

## Build/run (terminal, macOS/Linux classpath uses `:`)
- Default package demo:
  - Compile: `javac -d bin -classpath lib/gui.jar src/TestInvader.java`
  - Run:     `java -classpath bin:lib/gui.jar TestInvader`
- Package demos (compile the package then run with FQN):
  - `LKhalaya` (Conway):  `javac -d bin -classpath lib/gui.jar src/LKhalaya/*.java` → `java -classpath bin:lib/gui.jar LKhalaya.TestConway`
  - `Koora` (balls):      `javac -d bin -classpath lib/gui.jar src/Koora/*.java`     → `java -classpath bin:lib/gui.jar Koora.TestBallsSimulator`
  - `Multi_Agents` (boids): `javac -d bin -classpath lib/gui.jar src/Multi_Agents/*.java` → `java -classpath bin:lib/gui.jar Multi_Agents.TestBoids`

## Patterns that matter here
- GUI usage is consistent:
  - Create `new GUISimulator(w,h,Color.BLACK)`.
  - Implement `gui.Simulable`; in ctor call `gui.setSimulable(this)`.
  - Draw each frame with `gui.reset()` + `gui.addGraphicalElement(...)`.
- Cellular automata factorization (see `LKhalaya/`):
  - `CellularGrid` (model: rows/cols, `step()`, `reInit()`).
  - `CellularSimulator` (controller/view: holds `GUISimulator`, implements `next()/restart()` by delegating to the grid, abstract `draw()`).
  - Example: `JeuVie` + `JeuVieSimulator` + `TestConway`.
- Boids (`Multi_Agents/`) reuses a similar simulator shell but is not grid-based:
  - `BoidsSimulator` overrides `next()`/`restart()` to drive `BoidS`; `getGrid()` is unused. Keep this override pattern for non-grid systems.
- One-off sprite helper: `Multi_Agents/RotatedImageElement` draws a rotated PNG. Note the image path is hard-coded; update it if the file isn’t found.

## Conventions and gotchas
- No build tool; compile to `bin/` with `-classpath lib/gui.jar`. On Windows use `;` instead of `:`.
- Keep package names aligned with folder names under `src/` (e.g., `src/LKhalaya/...` → `package LKhalaya;`). Running requires the fully qualified class name.
- Don’t modify `lib/gui.jar` or `doc/`.

## Handy recipes
- New demo in a package (example `Koora`):
  - Create `src/Koora/MyDemo.java` with `package Koora;` and a `main`.
  - Compile: `javac -d bin -classpath lib/gui.jar src/Koora/*.java`
  - Run:     `java -classpath bin:lib/gui.jar Koora.MyDemo`
- Add GUI logic to a model: implement `Simulable`, call `setSimulable(this)`, draw via `Rectangle`, `Oval`, `Text`, or custom `GraphicalElement` (see `doc/`).

## Troubleshooting
- Classpath issues: ensure you run commands from repo root and include `lib/gui.jar`.
- Blank window: verify `gui.setSimulable(...)` was called and `draw()` happens in `next()`/`restart()`.
- Resource path (boids): `RotatedImageElement` uses an absolute path; change it or make it relative to the project to avoid failures.

If anything is unclear or you want extra sections (e.g., more `gui` examples or Gradle setup), tell me and I’ll iterate.
