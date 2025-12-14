# JAVA_POO — Simulations GUI + Multi-Agents (Boids)


- un squelette pédagogique POO  avec une GUI fournie (`lib/gui.jar`) et des démos simples (ex: `src/TestInvader.java`),
- un projet  de simulation multi-agents (boids) avec interactions proies/prédateurs et un écosystème dynamique (énergie, vieillissement, reproduction, mort).

Documentation complète: voir `CONCEPTION.md` (conception et règles), `ARCHITECTURE.md` (diagrammes & flux), et la doc GUI (`doc/index.html`).



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

## multi-agents

- Boids (flocking de Craig Reynolds): cohésion, alignement, séparation
- Interactions multi-groupes: proies fuient, prédateurs chassent et capturent
- Écosystème dynamique: énergie, vieillissement, reproduction des proies, mort par famine
- Affichage: sprites orientés, couleur assombrie si énergie faible, boids morts non dessinés
- Espace de simulation adaptatif: rebond sur les bords et dimensions synchronisées avec la taille du panel GUI

### Tests   

- `TestBoids` (proies seules)
- `TestMultiGroupBoids` (proies + prédateurs)
- `TestEcosystem` (dynamique de populations façon Lotka–Volterra)


