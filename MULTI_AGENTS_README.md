# Multi-Agent System Architecture

## Overview

This is a modular, event-driven multi-agent simulation framework built using abstract classes. The architecture supports multiple boid systems (flocking agents) with different behaviors, update frequencies, and interactions.

## Architecture

The system is organized into four main packages:

### 1. `multi_agents.core` - Core Agent Logic

**Vecteur2D.java**
- 2D vector mathematics
- Operations: add, subtract, multiply, divide, normalize, limit, distance
- Used for position, velocity, and force calculations

**Boid.java**
- Individual agent representation
- Attributes: position, velocity, acceleration
- Methods: `applyForce()`, `update()`
- Handles wrap-around at boundaries

**AbstractBoidSystem.java** ✨
- Abstract base class for all boid systems
- Common attributes:
  - `boids`: List of agents
  - `rayonVision`, `distanceSep`, `Vmax`: behavior parameters
  - `cohesionWeight`, `alignmentWeight`, `separationWeight`: rule weights
- Abstract method: `step()` - must be implemented by subclasses
- Concrete method: `reInit()` - randomizes boid positions

**PreyBoidSystem.java**
- Implements standard Reynolds flocking rules:
  1. **Cohesion**: Move toward center of nearby boids
  2. **Alignment**: Match velocity of nearby boids
  3. **Separation**: Avoid crowding neighbors
- Suitable for prey, birds, fish, etc.

**PredatorBoidSystem.java**
- Extends flocking with predator behavior
- Additional feature: `addPreySystem()` to define targets
- Chases nearest prey using `seek()` rule
- Balances hunting with group cohesion

**Rules.java** 🛠️
- Static utility methods for flocking forces
- Core rules:
  - `cohesion(boid, boids, radius)` → returns normalized vector toward center of mass
  - `alignment(boid, boids, radius)` → returns normalized vector toward average velocity
  - `separation(boid, boids, distance)` → returns normalized vector away from neighbors
- Additional rules:
  - `seek(boid, target, radius)` → attraction to target
  - `flee(boid, threat, radius)` → repulsion from threat

### 2. `multi_agents.events` - Event System

**Event.java**
- Abstract base class for all events
- Attributes: `date` (execution time)
- Abstract method: `execute()`

**EventManager.java**
- Priority queue-based event scheduler
- Sorts events by date
- Methods:
  - `addEvent(event)` - schedule an event
  - `next()` - execute next event
  - `restart()` - clear all events and reset time

**BoidUpdateEvent.java**
- Executes one simulation step for a boid system
- Self-scheduling: creates next event after execution
- Parameters:
  - `system`: the AbstractBoidSystem to update
  - `simulator`: the BoidSimulator for drawing
  - `delay`: time until next update

### 3. `multi_agents.sim` - Simulation & Display

**AbstractSimulator.java** ✨
- Abstract base class unifying all simulators
- Implements `Simulable` interface for GUI integration
- Manages `EventManager` instance
- Abstract method: `draw()` - must be implemented
- Concrete methods: `next()`, `restart()`

**BoidSimulator.java**
- Handles one or multiple `AbstractBoidSystem` instances
- Each system has its own:
  - Color (for visualization)
  - Update frequency (via event delay)
- Key method: `addSystem(system, color, delay)`
- Draws all boids with rotation based on velocity

**RotatedImageElement.java**
- Custom `GraphicalElement` for boid visualization
- Rotates image based on boid velocity direction
- Uses `AffineTransform` for rotation
- Includes image caching for performance
- Fallback to colored circle if image not found

### 4. `multi_agents.tests` - Test Programs

**TestEventManager.java**
- Simple PING/PONG message test
- Demonstrates event scheduling and execution
- No GUI required

**TestBoids.java**
- Single group of prey boids
- 50 cyan boids with standard flocking
- Update frequency: every step

**TestMultiGroupBoids.java** 🎯
- Multiple systems with different behaviors:
  - **Prey** (60 cyan boids): fast updates (delay=1)
  - **Predators** (8 red boids): slower updates (delay=3)
- Predators chase prey while maintaining group cohesion
- Demonstrates asynchronous multi-agent simulation

## Key Design Patterns

### 1. **Template Method Pattern**
- `AbstractBoidSystem` defines structure, subclasses implement `step()`
- `AbstractSimulator` defines simulation flow, subclasses implement `draw()`

### 2. **Strategy Pattern**
- Different boid behaviors (prey, predator) via subclasses
- Flocking rules in `Rules` class can be combined differently

### 3. **Observer Pattern**
- `EventManager` notifies systems when events occur
- GUI updates automatically when simulation state changes

### 4. **Composition over Inheritance**
- Systems composed of `Boid` objects
- Simulator composed of multiple `AbstractBoidSystem` instances

## Compilation & Execution

### Compile all classes:
```bash
javac -d bin -classpath lib/gui.jar:bin \
  src/multi_agents/events/*.java \
  src/multi_agents/core/*.java \
  src/multi_agents/sim/*.java \
  src/multi_agents/tests/*.java
```

### Run tests:

**Event Manager Test (console):**
```bash
java -classpath bin:lib/gui.jar multi_agents.tests.TestEventManager
```

**Single Group Boids (GUI):**
```bash
java -classpath bin:lib/gui.jar multi_agents.tests.TestBoids
```

**Multi-Group Boids (GUI):**
```bash
java -classpath bin:lib/gui.jar multi_agents.tests.TestMultiGroupBoids
```

## Extension Guide

### Adding a New Boid Behavior

1. **Create new class extending `AbstractBoidSystem`:**
```java
public class CustomBoidSystem extends AbstractBoidSystem {
    public CustomBoidSystem(int nbBoids, int width, int height, ...) {
        super(nbBoids, width, height, ...);
    }
    
    @Override
    public void step() {
        for (Boid b : boids) {
            // Apply custom rules using Rules class
            Vecteur2D force = Rules.cohesion(b, boids, rayonVision);
            b.applyForce(force);
            b.update(Vmax, width, height);
        }
    }
}
```

2. **Use in a test:**
```java
CustomBoidSystem system = new CustomBoidSystem(...);
simulator.addSystem(system, Color.GREEN, 2);
```

### Adding a New Rule

Add static method to `Rules.java`:
```java
public static Vecteur2D myCustomRule(Boid b, List<Boid> boids, double radius) {
    Vecteur2D force = new Vecteur2D(0, 0);
    // Compute force based on neighbors
    return force.normalize();
}
```

### Creating Multi-Species Interactions

```java
PreyBoidSystem rabbits = new PreyBoidSystem(...);
PreyBoidSystem mice = new PreyBoidSystem(...);
PredatorBoidSystem foxes = new PredatorBoidSystem(...);

foxes.addPreySystem(rabbits);
foxes.addPreySystem(mice);

simulator.addSystem(rabbits, Color.WHITE, 1);
simulator.addSystem(mice, Color.GRAY, 1);
simulator.addSystem(foxes, Color.ORANGE, 2);
```

## Parameters Guide

### Flocking Behavior Tuning

| Parameter | Effect | Typical Range |
|-----------|--------|---------------|
| `rayonVision` | How far boids "see" | 50-150 |
| `distanceSep` | Personal space | 20-50 |
| `Vmax` | Maximum speed | 2-5 |
| `cohesionWeight` | Attraction to group | 0.5-2.0 |
| `alignmentWeight` | Velocity matching | 0.5-2.0 |
| `separationWeight` | Avoidance strength | 1.0-3.0 |

### Visual Examples

- **Tight flocking**: High cohesion (2.0), low separation (1.0)
- **Loose swarm**: Low cohesion (0.5), high separation (2.0)
- **Fast movement**: High Vmax (4.0), high alignment (2.0)
- **Chaotic**: All weights equal (1.0), small vision (50)

## Troubleshooting

### Boids leave screen
- Increase `cohesionWeight`
- Decrease `Vmax`

### Boids clump in center
- Increase `separationWeight`
- Decrease `cohesionWeight`

### Simulation too fast/slow
- Adjust `delay` parameter in `addSystem()`
- Lower delay = faster updates

### Image not found error
- Check path in `RotatedImageElement` constructor
- Default: `"doc/resources/glass.png"`
- Fallback: colored circles

## Architecture Benefits

✅ **Extensibility**: New behaviors via subclassing  
✅ **Modularity**: Clear separation of concerns  
✅ **Reusability**: Rules and systems can be combined  
✅ **Performance**: Event-based updates, image caching  
✅ **Flexibility**: Different update rates per system  
✅ **Maintainability**: Clean abstractions, no interfaces  

## Future Enhancements

- Obstacle avoidance
- Goal-seeking behavior
- Energy/health system for predator-prey dynamics
- Spatial partitioning (quadtree) for performance
- 3D visualization support
- Network-based multi-agent coordination
