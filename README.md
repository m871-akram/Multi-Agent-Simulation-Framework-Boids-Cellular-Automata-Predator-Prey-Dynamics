# JAVA_POO — Simulations GUI + Multi-Agents (Boids)

Ce dépôt regroupe:
- un squelette pédagogique POO  avec une GUI fournie (`lib/gui.jar`) et des démos simples (ex: `src/TestInvader.java`),
- un projet  de simulation multi-agents (boids) avec interactions proies/prédateurs et un écosystème dynamique (énergie, vieillissement, reproduction, mort).

Documentation complète: voir `CONCEPTION.md` (conception et règles), `ARCHITECTURE.md` (diagrammes & flux), et la doc GUI (`doc/index.html`).

## Lancer rapidement

Prérequis: JDK 8+.

macOS/Linux utilisent `:` dans le classpath; Windows utilise `;`.

Exécuter les démos multi-agents depuis la racine (wrappers Makefile):

```bash
make run-boids       # proies (flocking seul)
make run-multi       # prédateurs vs proies (chasse/fuite, capture)
make run-ecosystem   # écosystème dynamique (énergie, repro, mort)
make run-events      # démonstration EventManager
```

Ou via le Makefile du dossier `src/multi_agents`:

```bash
cd src/multi_agents
make              # compile
make run          # proies (flocking seul)
make run-multi    # prédateurs vs proies (chasse/fuite, capture)
make run-ecosystem# écosystème dynamique (énergie, repro, mort)
```

Exécution directe (sans make):

```bash
# Depuis la racine du dépôt
javac -d bin -classpath lib/gui.jar src/multi_agents/logic/*.java src/multi_agents/EvenT/*.java src/multi_agents/simulation/*.java src/multi_agents/TestTest/*.java
java -classpath bin:lib/gui.jar multi_agents.TestTest.TestEcosystem

# Sous Windows, utilisez ; au lieu de :
java -classpath bin;lib/gui.jar multi_agents.TestTest.TestEcosystem
```

## Points clés (multi-agents)

- Boids (flocking de Craig Reynolds): cohésion, alignement, séparation
- Interactions multi-groupes: proies fuient, prédateurs chassent et capturent
- Écosystème dynamique: énergie, vieillissement, reproduction des proies, mort par famine
- Affichage: sprites orientés, couleur assombrie si énergie faible, boids morts non dessinés
- Espace de simulation adaptatif: rebond sur les bords et dimensions synchronisées avec la taille du panel GUI

### Tests inclus

- `TestBoids` (proies seules)
- `TestMultiGroupBoids` (proies + prédateurs)
- `TestEcosystem` (dynamique de populations façon Lotka–Volterra)


## GUI fournie (cours POO)

La librairie graphique `lib/gui.jar` (doc: `doc/index.html`) permet de créer une fenêtre (`GUISimulator`), dessiner (`addGraphicalElement`) et brancher un simulateur (`Simulable`).

## Conseils IDE

### IntelliJ IDEA

- File > New Project
- Ajoutez `lib/gui.jar` via File > Project Structure > Modules > Dependencies (JARs & Directories)

### VS Code

- Ouvrez le dossier et installez les extensions Java
- Si la lib n’est pas détectée, configurez un projet Java et ajoutez `lib/gui.jar`
- 

## Dépannage

- Fenêtre vide: vérifier `gui.setSimulable(this)` et que `draw()` est appelé dans `next()/restart()`
- Classpath: lancez les commandes depuis la racine et incluez `lib/gui.jar` (Windows: `;`)
- Interactions manquantes: appelez `linkSystems()` après tous les `addSystem()`
- Ressources: `RotatedImageElement` utilise `doc/resources/glass.png` par défaut; fallback en cercle si l’image manque
 
