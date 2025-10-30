# Multi-Agent System Architecture Diagram

## Component Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         MULTI-AGENT SYSTEM                       │
│                    (Event-Driven Architecture)                   │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   GUI SIMULATOR  │────▶│  BoidSimulator   │────▶│  EventManager    │
│   (lib/gui.jar)  │     │  (extends        │     │  (Priority Queue)│
│                  │     │   AbstractSim)   │     │                  │
│  • Window        │     │                  │     │  • addEvent()    │
│  • Drawing       │     │  • draw()        │     │  • next()        │
│  • User Input    │     │  • addSystem()   │     │  • restart()     │
└──────────────────┘     └──────────────────┘     └──────────────────┘
         │                        │                         │
         │                        │                         │
         ▼                        ▼                         ▼
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ RotatedImage     │     │ AbstractBoid     │────▶│ BoidUpdateEvent  │
│ Element          │     │ System           │     │ (extends Event)  │
│                  │     │                  │     │                  │
│  • paint()       │     │  • step()        │     │  • execute()     │
│  • rotate image  │     │  • reInit()      │     │  • self-schedule │
└──────────────────┘     └──────────────────┘     └──────────────────┘
                                  │
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
           ┌──────────────────┐       ┌──────────────────┐
           │ PreyBoidSystem   │       │ PredatorBoid     │
           │                  │       │ System           │
           │  • step() impl   │       │                  │
           │  • flocking only │       │  • step() impl   │
           └──────────────────┘       │  • + chase prey  │
                    │                 └──────────────────┘
                    │                          │
                    └──────────┬───────────────┘
                               ▼
                    ┌──────────────────┐
                    │  List<Boid>      │
                    │                  │
                    │  • position      │
                    │  • velocity      │
                    │  • acceleration  │
                    └──────────────────┘
                               │
                               ▼
                    ┌──────────────────┐
                    │  Rules (static)  │
                    │                  │
                    │  • cohesion()    │
                    │  • alignment()   │
                    │  • separation()  │
                    │  • seek()        │
                    │  • flee()        │
                    └──────────────────┘
```

## Data Flow: Simulation Step

```
User Clicks "Next"
        │
        ▼
┌───────────────────┐
│ GUISimulator      │
│   next()          │
└───────────────────┘
        │
        ▼
┌───────────────────┐
│ BoidSimulator     │
│   next()          │
└───────────────────┘
        │
        ▼
┌───────────────────┐
│ EventManager      │
│   next()          │  ◄──── Gets next event from priority queue
└───────────────────┘
        │
        ▼
┌───────────────────┐
│ BoidUpdateEvent   │
│   execute()       │
└───────────────────┘
        │
        ├─────────────────────┐
        ▼                     ▼
┌───────────────────┐  ┌───────────────────┐
│ AbstractBoidSys   │  │ BoidSimulator     │
│   step()          │  │   draw()          │
└───────────────────┘  └───────────────────┘
        │                     │
        ▼                     ▼
┌───────────────────┐  ┌───────────────────┐
│ For each Boid:    │  │ GUISimulator      │
│  1. Rules.xxx()   │  │   reset()         │
│  2. applyForce()  │  │   addGraphical    │
│  3. update()      │  │      Element()    │
└───────────────────┘  └───────────────────┘
        │                     │
        └──────────┬──────────┘
                   ▼
        ┌───────────────────┐
        │ EventManager      │
        │   addEvent()      │  ◄──── Schedule next update
        └───────────────────┘
```

## Class Relationships (UML-style)

```
                    ┌──────────────┐
                    │   Simulable  │ (interface from gui.jar)
                    │              │
                    │  + next()    │
                    │  + restart() │
                    └──────────────┘
                           △
                           │ implements
                           │
                    ┌──────────────────┐
                    │ AbstractSimulator│ (abstract)
                    ├──────────────────┤
                    │ # gui            │
                    │ # manager        │
                    ├──────────────────┤
                    │ + next()         │
                    │ + restart()      │
                    │ + draw()*        │ (* = abstract)
                    └──────────────────┘
                           △
                           │ extends
                           │
                    ┌──────────────────┐
                    │  BoidSimulator   │
                    ├──────────────────┤
                    │ - systems        │ Map<AbstractBoidSystem, Color>
                    ├──────────────────┤
                    │ + addSystem()    │
                    │ + draw()         │
                    └──────────────────┘
                           │
                           │ uses
                           ▼
                    ┌──────────────────┐
                    │AbstractBoidSystem│ (abstract)
                    ├──────────────────┤
                    │ # boids          │ List<Boid>
                    │ # rayonVision    │
                    │ # distanceSep    │
                    │ # Vmax           │
                    │ # weights...     │
                    ├──────────────────┤
                    │ + step()*        │
                    │ + reInit()       │
                    │ + getBoids()     │
                    └──────────────────┘
                           △
                           │ extends
                ┌──────────┴──────────┐
                │                     │
        ┌───────────────┐     ┌───────────────────┐
        │PreyBoidSystem │     │PredatorBoidSystem │
        ├───────────────┤     ├───────────────────┤
        │               │     │ - preySystems     │
        ├───────────────┤     ├───────────────────┤
        │ + step()      │     │ + step()          │
        │               │     │ + addPreySystem() │
        └───────────────┘     └───────────────────┘
```

## Event System Architecture

```
                    ┌──────────────┐
                    │    Event     │ (abstract)
                    ├──────────────┤
                    │ - date       │
                    ├──────────────┤
                    │ + getDate()  │
                    │ + execute()* │
                    └──────────────┘
                           △
                           │ extends
                           │
                    ┌──────────────────┐
                    │ BoidUpdateEvent  │
                    ├──────────────────┤
                    │ - system         │
                    │ - simulator      │
                    │ - delay          │
                    ├──────────────────┤
                    │ + execute()      │
                    └──────────────────┘
                           │
                           │ managed by
                           ▼
                    ┌──────────────────┐
                    │  EventManager    │
                    ├──────────────────┤
                    │ - currentDate    │
                    │ - events         │ PriorityQueue<Event>
                    ├──────────────────┤
                    │ + addEvent()     │
                    │ + next()         │
                    │ + restart()      │
                    └──────────────────┘
```

## Package Dependencies

```
┌─────────────────────────────────────────────────────────┐
│                    multi_agents.tests                    │
│  • TestEventManager                                     │
│  • TestBoids                                            │
│  • TestMultiGroupBoids                                  │
└─────────────────────────────────────────────────────────┘
                           │
                           │ imports
                           ▼
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│ multi_agents   │  │ multi_agents   │  │ multi_agents   │
│    .core       │  │   .events      │  │    .sim        │
├────────────────┤  ├────────────────┤  ├────────────────┤
│ Vecteur2D      │  │ Event          │  │ AbstractSim    │
│ Boid           │  │ EventManager   │  │ BoidSimulator  │
│ AbstractBoid   │  │ BoidUpdate     │  │ RotatedImage   │
│   System       │  │   Event        │  │   Element      │
│ PreyBoid       │  └────────────────┘  └────────────────┘
│   System       │           │                   │
│ PredatorBoid   │           │                   │
│   System       │           │                   │
│ Rules          │           │                   │
└────────────────┘           │                   │
        │                    │                   │
        └────────────────────┴───────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │   gui.jar      │
                    │  (external)    │
                    └────────────────┘
```

## Execution Flow: Multi-Group Simulation

```
1. INITIALIZATION
   ═══════════════
   TestMultiGroupBoids.main()
        │
        ├─▶ new GUISimulator(800, 600, BLACK)
        │
        ├─▶ new BoidSimulator(gui)
        │       └─▶ new EventManager()
        │
        ├─▶ new PreyBoidSystem(60, ...)
        │       └─▶ creates 60 Boid objects
        │
        ├─▶ new PredatorBoidSystem(8, ...)
        │       ├─▶ creates 8 Boid objects
        │       └─▶ addPreySystem(preySystem)
        │
        ├─▶ simulator.addSystem(preySystem, CYAN, 1)
        │       └─▶ manager.addEvent(BoidUpdateEvent(0, prey, sim, 1))
        │
        ├─▶ simulator.addSystem(predatorSystem, RED, 3)
        │       └─▶ manager.addEvent(BoidUpdateEvent(0, pred, sim, 3))
        │
        └─▶ simulator.draw()

2. USER CLICKS "NEXT"
   ═══════════════════
   GUISimulator triggers next()
        │
        └─▶ BoidSimulator.next()
                │
                └─▶ EventManager.next()
                        │
                        ├─▶ Polls event with smallest date
                        │   (Could be prey or predator event)
                        │
                        └─▶ event.execute()
                                │
                                ├─▶ system.step()
                                │   ├─▶ For each boid:
                                │   │   ├─▶ Rules.cohesion()
                                │   │   ├─▶ Rules.alignment()
                                │   │   ├─▶ Rules.separation()
                                │   │   ├─▶ (Predator: Rules.seek())
                                │   │   ├─▶ boid.applyForce()
                                │   │   └─▶ boid.update()
                                │   │
                                ├─▶ simulator.draw()
                                │   ├─▶ gui.reset()
                                │   └─▶ For all systems:
                                │       └─▶ For each boid:
                                │           └─▶ gui.addGraphicalElement(
                                │               RotatedImageElement(...))
                                │
                                └─▶ manager.addEvent(
                                    new BoidUpdateEvent(
                                        date + delay, ...))

3. EVENT TIMELINE (example)
   ═════════════════════════
   Date 0:  Prey update    (next: date 1)
   Date 0:  Pred update    (next: date 3)
   Date 1:  Prey update    (next: date 2)
   Date 2:  Prey update    (next: date 3)
   Date 3:  Prey update    (next: date 4)
   Date 3:  Pred update    (next: date 6)
   Date 4:  Prey update    (next: date 5)
   Date 5:  Prey update    (next: date 6)
   Date 6:  Prey update    (next: date 7)
   Date 6:  Pred update    (next: date 9)
   ...
   
   ▲ Prey updates every step (fast)
   ▲ Predators update every 3 steps (slower)
```

## Key Architectural Benefits

```
┌──────────────────────────────────────────────────────────┐
│                    DESIGN PRINCIPLES                      │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  ✓ ABSTRACT CLASSES ONLY (no interfaces)                 │
│    └─▶ AbstractBoidSystem, AbstractSimulator, Event      │
│                                                           │
│  ✓ COMPOSITION OVER INHERITANCE                          │
│    └─▶ BoidSimulator contains Map<System, Color>         │
│                                                           │
│  ✓ SINGLE RESPONSIBILITY PRINCIPLE                       │
│    ├─▶ Rules: Flocking calculations                      │
│    ├─▶ EventManager: Time management                     │
│    ├─▶ BoidSimulator: Rendering                          │
│    └─▶ AbstractBoidSystem: Behavior                      │
│                                                           │
│  ✓ OPEN/CLOSED PRINCIPLE                                 │
│    └─▶ Add new boid types without modifying base         │
│                                                           │
│  ✓ DEPENDENCY INVERSION                                  │
│    └─▶ Depend on abstractions (AbstractBoidSystem)       │
│                                                           │
│  ✓ SEPARATION OF CONCERNS                                │
│    ├─▶ core/    : Logic (no GUI, no events)              │
│    ├─▶ events/  : Time management                        │
│    ├─▶ sim/     : Rendering & coordination                │
│    └─▶ tests/   : Application entry points                │
│                                                           │
└──────────────────────────────────────────────────────────┘
```

## Legend

```
Symbol      Meaning
────────    ─────────────────────────────────
  │         Dependency / Call
  ▼         Direction of flow
  △         Inheritance (extends/implements)
  ═         Process flow
  *         Abstract method
  #         Protected member
  +         Public member
  -         Private member
  ◄───      Data flow
  ┌─┐       Component boundary
```

---
**Architecture Status: ✅ COMPLETE & VERIFIED**
