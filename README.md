# Multi-Agent-Simulation-Framework-Boids-Cellular-Automata-Predator-Prey-Dynamics

Projet pédagogique POO en deux volets :

- **Squelette GUI** : bibliothèque `lib/gui.jar` + démos simples (`src/TestInvader.java`)
- **Simulation multi-agents** : boids avec interactions proies/prédateurs et écosystème dynamique (énergie, vieillissement, reproduction, mort)

---

**Prérequis :** JDK 8+. macOS/Linux utilisent `:` dans le classpath ; Windows utilise `;`.

```bash
make run-boids       # proies (flocking seul)
make run-multi       # prédateurs vs proies (chasse/fuite, capture)
make run-ecosystem   # écosystème dynamique (énergie, repro, mort)
make run-events      # démonstration EventManager
```

Ou depuis `src/multi_agents` :

```bash
cd src/multi_agents
make              # compile
make run          # proies
make run-multi    # prédateurs vs proies
make run-ecosystem
```

Sans make :

```bash
javac -d bin -classpath lib/gui.jar \
  src/multi_agents/logic/*.java \
  src/multi_agents/EvenT/*.java \
  src/multi_agents/simulation/*.java \
  src/multi_agents/TestTest/*.java

java -classpath bin:lib/gui.jar multi_agents.TestTest.TestEcosystem
# Windows : bin;lib/gui.jar
```

---

## Conception multi-agents

### Objectifs

- Flocking réaliste (cohésion, alignement, séparation)
- Interactions proies ↔ prédateurs (fuite, poursuite, capture)
- Écosystème dynamique (énergie, âge, reproduction, mort)
- Architecture événementielle (pas de boucle bloquante, pas de threads)

### Modèle agent : Boid

État : position, vitesse, accélération + limites physiques (`Vmax`, `Fmax`).  
États internes : énergie (0–100), âge (frames), vivant (bool).

Invariants :
- `‖v‖ ≤ Vmax`, `‖a‖ ≤ Fmax`
- Énergie dans [0, 100] ; mort si énergie ≤ 0 → boid non dessiné

### Règles locales (`LaLoi`)

| Règle | Description |
|---|---|
| Cohésion | Attraction vers le centre des voisins |
| Alignement | Rapprochement des vitesses |
| Séparation | Évitement des collisions |
| Fuite / Poursuite | Selon rôle (proie / prédateur) |
| Vagabondage | Exploration légère si isolé |

Combinaison pondérée à chaque `step()` :  
`a = w_coh·coh + w_ali·ali + w_sep·sep + fuite|poursuite + vagabond`  
→ limiter `a` par `Fmax`, puis mettre à jour position/vitesse.

### Systèmes de boids

**ProieBoidSystem**
- Flocking + fuite des prédateurs + vagabondage si isolé
- Métabolisme : −0.1 énergie/frame
- Reproduction : énergie > 80, âge > 50, 2 %/frame, coût −30

**PredateurBoidSystem**
- Flocking + chasse de la proie la plus proche
- Métabolisme : −0.5 énergie/frame
- Capture si distance < 10 → +40 énergie

### Interactions et événements

Les systèmes se connaissent via `BoidSimulateur.linkSystems()` (appeler *après* tous les `addSystem()`).

`EventManager` pilote la boucle :
1. Synchronise les bornes avec la taille du panel GUI
2. `system.step()`
3. `simulator.draw()`
4. Replanifie l'événement

Cela permet des fréquences d'update distinctes par système et une simulation réactive aux redimensionnements.

### Paramétrage recommandé

| Paramètre | Proies | Prédateurs |
|---|---|---|
| Vmax | 3.0 | 4.0 |
| Fmax | 0.15–0.20 | 0.20 |
| Cohésion | 1.0 | 0.8 |
| Alignement | 1.2 | 0.9 |
| Séparation | 1.5 | 1.2 |
| Chasse | — | 2.5 |

Écosystème : métabolisme 0.1/0.5, reproduction seuils 80/50 chance 2 % coût 30, capture dist 10 gain +40.

> **Tuning** : proies disparaissent trop vite → réduire `Vmax` prédateurs ou augmenter `Fmax`/FOV proies. Simulation instable → baisser `Fmax`, augmenter séparation.

---

## JavaDoc

```bash
javadoc -private -d doc/javadoc \
        -classpath lib/gui.jar \
        -sourcepath src \
        -subpackages Koora:LKhalaya:multi_agents \
        -encoding UTF-8 \
        -charset UTF-8
```

This generates HTML documentation under `doc/javadoc/`. Open `doc/javadoc/index.html` in a browser to browse it.

---

## Tests

| Classe | Scénario |
|---|---|
| `TestBoids` | Proies seules (flocking) |
| `TestMultiGroupBoids` | Proies + prédateurs |
| `TestEcosystem` | Dynamique Lotka–Volterra |

---

## Étendre le projet

1. Créer une sous-classe de `BoidSystem`
2. Implémenter `step()` en combinant les règles de `LaLoi`
3. `simulateur.addSystem(system, color, delay)`
4. `simulateur.linkSystems()`

Idées : obstacles/attracteurs, super-prédateurs, ressources végétales, ajustement dynamique des poids.

---

## Références

- Reynolds, C. (1987) — *Flocks, herds and schools*
- Shiffman, D. — *The Nature of Code*, chap. 6
- Lotka–Volterra — dynamiques proies/prédateurs (modèle discret)
- `ARCHITECTURE.md` (diagrammes Mermaid + flux), `doc/index.html` (API GUI).
