# Multi-Agent System - Quick Reference

## Class Hierarchy

```
multi_agents.events
├── Event (abstract)
│   └── BoidUpdateEvent
└── EventManager

multi_agents.core
├── Vecteur2D
├── Boid
├── AbstractBoidSystem (abstract)
│   ├── PreyBoidSystem
│   └── PredatorBoidSystem
└── Rules (static utilities)

multi_agents.sim
├── AbstractSimulator (abstract, implements Simulable)
│   └── BoidSimulator
└── RotatedImageElement (implements GraphicalElement)

multi_agents.tests
├── TestEventManager
├── TestBoids
└── TestMultiGroupBoids
```

## Key Relationships

### Inheritance (extends)
- `BoidUpdateEvent extends Event`
- `PreyBoidSystem extends AbstractBoidSystem`
- `PredatorBoidSystem extends AbstractBoidSystem`
- `BoidSimulator extends AbstractSimulator`

### Composition (has-a)
- `AbstractBoidSystem` has List<`Boid`>
- `BoidSimulator` has Map<`AbstractBoidSystem`, Color>
- `AbstractSimulator` has `EventManager`
- `PredatorBoidSystem` has List<`AbstractBoidSystem`> (prey targets)

### Dependencies (uses)
- `PreyBoidSystem` uses `Rules` (cohesion, alignment, separation)
- `PredatorBoidSystem` uses `Rules` (+ seek for chasing)
- `BoidUpdateEvent` uses `AbstractBoidSystem` and `BoidSimulator`
- `BoidSimulator` uses `RotatedImageElement` for rendering

## Method Call Flow

### Initialization
```
main() 
  → new GUISimulator()
  → new BoidSimulator(gui)
    → new EventManager()
  → new PreyBoidSystem() / new PredatorBoidSystem()
    → creates List<Boid>
  → simulator.addSystem(system, color, delay)
    → new BoidUpdateEvent()
    → manager.addEvent()
```

### Simulation Step (when user clicks "Next")
```
GUI triggers next()
  → AbstractSimulator.next()
    → EventManager.next()
      → Event.execute()
        → BoidUpdateEvent.execute()
          → AbstractBoidSystem.step()
            → Rules.cohesion/alignment/separation()
            → Boid.applyForce()
            → Boid.update()
          → BoidSimulator.draw()
            → GUISimulator.reset()
            → for each boid: new RotatedImageElement()
          → manager.addEvent(new BoidUpdateEvent(...))
```

### Restart (when user clicks "Restart")
```
GUI triggers restart()
  → AbstractSimulator.restart()
    → EventManager.restart()
      → clears event queue
    → BoidSimulator.draw()
      → for each system: system.reInit()
      → creates new BoidUpdateEvents
```

## Common Code Patterns

### Creating a Basic Simulation
```java
// 1. Create GUI
GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

// 2. Create simulator
BoidSimulator sim = new BoidSimulator(gui);

// 3. Create boid system
PreyBoidSystem prey = new PreyBoidSystem(
    50,           // nbBoids
    800, 600,     // width, height
    100.0,        // rayonVision
    30.0,         // distanceSep
    3.0,          // Vmax
    1.0,          // cohesionWeight
    1.0,          // alignmentWeight
    1.5           // separationWeight
);

// 4. Add system to simulator
sim.addSystem(prey, Color.CYAN, 1);

// 5. Draw initial state
sim.draw();
```

### Creating Predator-Prey Simulation
```java
GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);
BoidSimulator sim = new BoidSimulator(gui);

// Create prey
PreyBoidSystem prey = new PreyBoidSystem(60, 800, 600, 80, 25, 2.5, 1.0, 1.0, 1.5);

// Create predators
PredatorBoidSystem predators = new PredatorBoidSystem(
    8, 800, 600, 120, 40, 3.5, 0.8, 0.8, 1.2, 2.0  // last param is chaseWeight
);

// Link predators to prey
predators.addPreySystem(prey);

// Add both systems with different update rates
sim.addSystem(prey, Color.CYAN, 1);       // fast
sim.addSystem(predators, Color.RED, 3);   // slower

sim.draw();
```

### Custom Rule Example
```java
// In Rules.java
public static Vecteur2D windForce(Boid b, Vecteur2D windDirection) {
    return windDirection.normalize().mult(0.5);
}

// In custom BoidSystem.step()
for (Boid b : boids) {
    Vecteur2D wind = Rules.windForce(b, new Vecteur2D(1, 0));
    b.applyForce(wind);
    b.update(Vmax, width, height);
}
```

## Parameter Tuning Cheat Sheet

### Behavior Types

**Tight flock** (birds, fish schools)
```java
cohesionWeight = 2.0
alignmentWeight = 1.5
separationWeight = 1.0
rayonVision = 100
distanceSep = 25
```

**Loose swarm** (bees, particles)
```java
cohesionWeight = 0.5
alignmentWeight = 0.5
separationWeight = 2.0
rayonVision = 80
distanceSep = 40
```

**Fast movers** (jets, fast animals)
```java
Vmax = 5.0
alignmentWeight = 2.0
cohesionWeight = 1.0
separationWeight = 1.0
```

**Predator setup**
```java
// Higher vision, faster speed, strong chase
rayonVision = 150
Vmax = 4.0
chaseWeight = 2.5
cohesionWeight = 0.7  // less flock cohesion
```

## Testing Checklist

- [x] Event system works (TestEventManager)
- [x] Single boid group displays and moves (TestBoids)
- [x] Multiple groups with different update rates (TestMultiGroupBoids)
- [x] Predators chase prey
- [x] Boids wrap around screen boundaries
- [x] Restart button reinitializes simulation
- [x] Next button advances simulation
- [x] Image rotation follows velocity direction

## Common Issues & Solutions

### Issue: Boids all move to corners
**Solution**: Increase cohesionWeight, ensure rayonVision > distanceSep

### Issue: Boids cluster in center and don't move
**Solution**: Increase separationWeight, add initial random velocities

### Issue: Simulation runs too fast/slow
**Solution**: Adjust delay parameter in addSystem()

### Issue: Predators don't chase prey
**Solution**: Verify addPreySystem() was called, increase chaseWeight

### Issue: Image not found error
**Solution**: Check path "doc/resources/glass.png" exists, or code falls back to circles

## File Organization

```
src/multi_agents/
  core/          - Agent logic (no GUI, no events)
  events/        - Event scheduling system
  sim/           - GUI integration and drawing
  tests/         - Executable test programs

bin/multi_agents/
  (mirror structure with .class files)

doc/resources/
  glass.png      - Boid sprite image
```

## Next Steps

1. ✅ Basic flocking implemented
2. ✅ Predator-prey dynamics
3. ⬜ Add obstacles
4. ⬜ Energy/health system
5. ⬜ Goal-seeking behavior
6. ⬜ 3D version
