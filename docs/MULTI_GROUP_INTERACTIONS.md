# Multi-Group Dynamic Interactions

Ce document décrit le système d'interactions dynamiques entre plusieurs groupes de boids, permettant aux prédateurs de chasser et capturer les proies.

## 🎯 Vue d'ensemble

Le système permet maintenant à plusieurs groupes de boids d'interagir de manière dynamique :

- **Prédateurs** chassent les proies les plus proches
- **Proies** fuient les prédateurs détectés
- **Capture** : les proies disparaissent quand attrapées
- **Flocking** : chaque groupe maintient ses propres règles de cohésion/alignement/séparation

## 🏗️ Architecture

### 1. AbstractBoidSystem - Système de base

Ajout d'un système de connexion inter-groupes :

```java
protected List<AbstractBoidSystem> otherSystems;

public void setOtherSystems(List<AbstractBoidSystem> others)
public List<AbstractBoidSystem> getOtherSystems()
```

**Méthode utilitaire** pour éviter la duplication de code :

```java
protected void updateBoids(List<Vecteur2D> accelerations)
```

Cette méthode applique les accélérations et met à jour tous les boids en une seule fois.

### 2. PreyBoidSystem - Comportement des proies

#### Fuite des prédateurs

```java
// Détection des prédateurs via otherSystems
for (AbstractBoidSystem sys : otherSystems) {
    if (sys instanceof PredatorBoidSystem) {
        for (Boid predator : sys.getBoids()) {
            double dist = boid.position.distance(predator.position);
            if (dist < rayonVision * 1.2) {
                Vecteur2D fleeForce = Rules.flee(boid, predator.position, boid.maxSpeed);
                flee = flee.add(fleeForce);
                boid.increaseFear(0.1); // La peur augmente
            }
        }
    }
}
```

#### Caractéristiques

- **Rayon de détection** : `rayonVision * 1.2` (20% plus large que la vision normale)
- **Amplification par la peur** : `flee.mult(fleeWeight * (1.0 + fear))`
- **Accumulation** : Multiple prédateurs créent une force de fuite cumulée

### 3. PredatorBoidSystem - Comportement des prédateurs

#### Chasse de la proie la plus proche

```java
Boid closestPrey = null;
double minDist = Double.MAX_VALUE;

// Recherche dans tous les systèmes de proies
for (AbstractBoidSystem sys : otherSystems) {
    if (sys instanceof PreyBoidSystem) {
        for (Boid prey : sys.getBoids()) {
            double dist = predator.position.distance(prey.position);
            if (dist < minDist) {
                minDist = dist;
                closestPrey = prey;
            }
        }
    }
}
```

#### Capture des proies

```java
if (minDist < 10.0) {
    preysToCatch.add(closestPrey);
    predator.increaseEnergy(0.2); // Récupération d'énergie
}

// Après la boucle : retrait des proies capturées
for (Boid prey : preysToCatch) {
    for (AbstractBoidSystem sys : otherSystems) {
        if (sys instanceof PreyBoidSystem) {
            sys.getBoids().remove(prey);
        }
    }
}
```

#### Caractéristiques

- **Rayon de chasse** : `rayonVision * 2` (deux fois la vision normale)
- **Distance de capture** : `< 10 pixels`
- **Motivation** : Facteur de faim `1.0 + (1.0 - energy)` amplifie la poursuite
- **Récompense** : `+0.2` énergie par capture

### 4. BoidSimulator - Connexion des systèmes

#### Méthode linkSystems()

```java
public void linkSystems() {
    List<AbstractBoidSystem> allSystems = new ArrayList<>(systems.keySet());
    
    for (AbstractBoidSystem system : allSystems) {
        List<AbstractBoidSystem> others = new ArrayList<>(allSystems);
        others.remove(system); // Chaque système connaît tous les autres sauf lui-même
        system.setOtherSystems(others);
    }
}
```

**IMPORTANT** : Appeler **après** avoir ajouté tous les systèmes avec `addSystem()`.

## 🎮 Utilisation

### Code minimal

```java
BoidSimulator simulator = new BoidSimulator(gui);

// Créer les systèmes
PreyBoidSystem prey = new PreyBoidSystem(100, width, height, ...);
PredatorBoidSystem predator = new PredatorBoidSystem(10, width, height, ...);

// Ajouter au simulateur
simulator.addSystem(prey, Color.CYAN, 1);
simulator.addSystem(predator, Color.RED, 2);

// Lier les systèmes (étape cruciale !)
simulator.linkSystems();

// Dessiner
simulator.draw();
```

### Exemple complet (TestMultiGroupBoids)

```java
// 60 proies rapides et réactives
PreyBoidSystem preySystem = new PreyBoidSystem(
    60, 800, 600,
    80.0,  // rayonVision
    25.0,  // distanceSep
    2.5,   // Vmax (rapides)
    0.2,   // maxForce (très réactives)
    Math.PI * 0.8, // fieldOfView large
    1.0, 1.0, 1.5  // weights
);

// 8 prédateurs puissants mais moins fréquents
PredatorBoidSystem predatorSystem = new PredatorBoidSystem(
    8, 800, 600,
    120.0, // rayonVision plus large
    40.0,  // distanceSep
    3.5,   // Vmax (plus rapides)
    0.15,  // maxForce (moins maniables)
    Math.PI * 0.6, // fieldOfView focalisé
    0.8, 0.8, 1.2, // weights
    2.0    // chaseWeight
);

simulator.addSystem(preySystem, Color.CYAN, 1);   // Mises à jour rapides
simulator.addSystem(predatorSystem, Color.RED, 3); // Mises à jour lentes
simulator.linkSystems(); // ← NE PAS OUBLIER !
```

## 📊 Comportements émergents

### Dynamiques observables

1. **Groupes de fuite** - Les proies forment des bancs qui fuient ensemble
2. **Chasse coordonnée** - Plusieurs prédateurs peuvent encercler un groupe
3. **Déplétion progressive** - Le nombre de proies diminue au fil du temps
4. **Équilibre prédateur-proie** - La simulation trouve un équilibre naturel

### Stratégies des proies

- **Force en nombre** : Rester en groupe dilue le risque individuel
- **Vitesse** : Proies configurées plus rapides que prédateurs individuellement
- **Réactivité** : `maxForce` élevée permet des changements de direction rapides

### Stratégies des prédateurs

- **Puissance** : Vitesse maximale supérieure
- **Patience** : Mises à jour moins fréquentes économisent l'énergie
- **Vision large** : `rayonVision = 120` vs `80` pour les proies

## ⚙️ Paramètres recommandés

### Équilibre optimal

| Paramètre | Proies | Prédateurs | Ratio |
|-----------|--------|------------|-------|
| Nombre | 60 | 8 | 7.5:1 |
| Vmax | 2.5 | 3.5 | 0.71:1 |
| maxForce | 0.2 | 0.15 | 1.33:1 |
| rayonVision | 80 | 120 | 0.67:1 |
| updateDelay | 1 | 3 | 0.33:1 |

### Ajustements

**Pour favoriser les proies** :
- Augmenter `preyVmax`
- Augmenter `preyMaxForce`
- Diminuer `chaseWeight`
- Augmenter capture distance

**Pour favoriser les prédateurs** :
- Augmenter `chaseWeight`
- Diminuer predator `updateDelay`
- Augmenter `predatorVisionRadius`
- Diminuer capture distance

## 🔧 Extensibilité

### Ajouter un 3ème groupe

```java
// Groupe neutre (ne chasse ni ne fuit)
NeutralBoidSystem neutral = new NeutralBoidSystem(...);
simulator.addSystem(neutral, Color.GREEN, 2);
simulator.linkSystems(); // Relie automatiquement tous les groupes
```

### Créer un nouveau type

```java
public class SuperPredatorBoidSystem extends AbstractBoidSystem {
    @Override
    public void step() {
        // Peut chasser à la fois les proies ET les prédateurs normaux
        for (AbstractBoidSystem sys : otherSystems) {
            if (sys instanceof PreyBoidSystem || 
                sys instanceof PredatorBoidSystem) {
                // Logique de chasse...
            }
        }
    }
}
```

### Interactions complexes

Le système `otherSystems` permet des interactions arbitraires :

- **Symbiose** : Un groupe protège un autre
- **Parasitisme** : Un groupe ralentit un autre
- **Compétition** : Plusieurs prédateurs pour mêmes proies
- **Hiérarchies** : Chaînes alimentaires à 3+ niveaux

## 🐛 Debugging

### Proies ne fuient pas

```java
// Vérifier que linkSystems() a été appelé
simulator.linkSystems();

// Vérifier le rayon de détection
if (dist < rayonVision * 1.2) { // Doit être > rayonVision
```

### Prédateurs ne chassent pas

```java
// Vérifier instanceof
if (sys instanceof PreyBoidSystem) { // Attention au nom de classe

// Vérifier le rayon de chasse
if (closestPrey != null && minDist < rayonVision * 2) {
```

### Capture ne fonctionne pas

```java
// Distance de capture
if (minDist < 10.0) { // Ajuster si nécessaire

// Vérifier que remove() est appelé APRÈS la boucle de step()
```

### Performance

Avec N proies et M prédateurs, la complexité est O(N*M) à chaque step.

**Optimisations possibles** :
- Spatial hashing pour recherche de voisins
- Limiter la distance de détection
- Paralléliser les calculs par système

## 📈 Statistiques en temps réel (extension future)

```java
// Idée pour suivre la population
public class PopulationTracker {
    private Map<AbstractBoidSystem, List<Integer>> history;
    
    public void record() {
        for (AbstractBoidSystem sys : systems) {
            history.get(sys).add(sys.size());
        }
    }
    
    public void plot() {
        // Afficher graphe population vs temps
    }
}
```

## 🎓 Concepts théoriques

### Modèle Lotka-Volterra

Le système implémente une version discrète des équations prédateur-proie :

```
dP/dt = αP - βPQ  (Proies)
dQ/dt = δPQ - γQ  (Prédateurs)

Où:
- P = population proies
- Q = population prédateurs
- α = taux de reproduction proies
- β = taux de prédation
- γ = mortalité prédateurs
- δ = efficacité conversion
```

### Émergence

Les comportements complexes émergent de règles simples :

1. **Règles locales** : Chaque boid suit ses voisins immédiats
2. **Interactions** : Proies évitent prédateurs, prédateurs poursuivent proies
3. **Patterns globaux** : Groupes dynamiques, vagues de fuite, chasse coordonnée

---

**Documentation complète** : Voir aussi `ENHANCEMENTS.md` et `QUICK_GUIDE.md`
