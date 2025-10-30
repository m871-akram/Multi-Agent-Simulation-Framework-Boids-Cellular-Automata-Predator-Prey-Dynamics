# Multi-Agents Boids Simulation

## Description

Ce projet implémente une simulation avancée de **boids** (agents autonomes) basée sur les algorithmes de flocking de Craig Reynolds et les principes du livre "The Nature of Code". Il permet de simuler des comportements émergents complexes dans des systèmes multi-agents avec interactions prédateur-proie.

## Structure du Projet

```
multi_agents/
├── core/                          # Classes principales du modèle
│   ├── Boid.java                 # Agent individuel avec steering behaviors
│   ├── Vecteur2D.java           # Classe utilitaire pour calculs vectoriels 2D
│   ├── BoidSystem.java   # Classe abstraite pour systèmes de boids
│   ├── PreyBoidSystem.java      # Système de proies (fuient les prédateurs)
│   ├── PredatorBoidSystem.java  # Système de prédateurs (chassent les proies)
│   └── LaLoi.java               # Règles de flocking (cohésion, alignement, séparation)
│
├── events/                        # Gestion événementielle de la simulation
│   ├── Event.java               # Classe abstraite pour événements
│   ├── EventManager.java        # Gestionnaire de file d'événements (date-driven)
│   └── BoidmàjEvent.java     # Événement de mise à jour d'un système de boids
│
├── sim/                          # Simulateurs et interface graphique
│   ├── AbstractSimulator.java   # Simulateur abstrait avec GUI et EventManager
│   ├── BoidSimulator.java       # Simulateur multi-systèmes pour boids
│   └── RotatedImageElement.java # Élément graphique rotatif pour l'affichage
│
├── tests/                        # Programmes de test
│   ├── TestEventManager.java   # Test du gestionnaire d'événements
│   ├── TestBoids.java          # Test simple : un groupe de proies
│   └── TestMultiGroupBoids.java # Test avancé : prédateurs vs proies
│
├── Makefile                      # Compilation et exécution automatisées
├── README.md                     # Ce fichier
├── CONCEPTION.md                 # Document de conception (PDF)
└── MANUEL_UTILISATEUR.md         # Manuel utilisateur (PDF)
```

## Architecture Logicielle

### 1. **Modèle Multi-Agents** (`core/`)

#### Hiérarchie des Systèmes
```
BoidSystem (abstract)
  ├── abstract step()           # Comportement spécifique
  ├── abstract isPredator()     # Identification du type
  └── abstract isPrey()
      ├── PreyBoidSystem        # Implémente fuite des prédateurs
      └── PredatorBoidSystem    # Implémente chasse des proies
```

**Principes appliqués** :
- **Encapsulation** : Attributs privés/package-protected avec getters/setters
- **Héritage** : Factorisation du code commun (width, height, boids, etc.)
- **Polymorphisme** : Méthodes abstraites évitant `instanceof`
- **Délégation** : Méthode `màjBoids()` réutilisable

#### Boid (Agent Individuel)
Chaque boid possède :
- **État physique** : position, vitesse, accélération
- **Paramètres** : Vmax, Fmax (steering behaviors)
- **État interne** : énergie (diminue avec le temps), peur (augmente face aux prédateurs)

### 2. **Gestion Événementielle** (`events/`)

Architecture date-driven utilisant une **file de priorité** :
- Les événements sont triés par date
- Chaque événement s'auto-replanifie (boucle de simulation)
- Permet des fréquences de mise à jour différentes par système

```
EventManager
  └── PriorityQueue<Event>
        └── BoidmàjEvent (se replanifie automatiquement)
```

### 3. **Simulateurs** (`sim/`)

```
AbstractSimulator (abstract)
  ├── GUISimulator gui         # Interface graphique
  ├── EventManager manager     # Gestionnaire d'événements
  └── abstract draw()          # Rendu spécifique
      └── BoidSimulator         # Gère plusieurs systèmes simultanément
```

**Fonctionnalités** :
- Gestion multi-systèmes avec couleurs personnalisées
- Fréquences de mise à jour indépendantes
- Liaison automatique des systèmes (`linkSystems()`)
- Adaptation dynamique à la taille de la fenêtre

## Fonctionnalités Implémentées

### Comportements Nature of Code

1. **Steering Behaviors** (Craig Reynolds)
   - Cohésion : attraction vers le centre du groupe
   - Alignement : synchronisation des vitesses
   - Séparation : évitement des collisions

2. **Champ de Vision (Field of View)**
   - Angle de vision configurable
   - Les boids ne réagissent qu'aux voisins visibles

3. **poursuite & fuite**
   - poursuite : poursuite d'une cible (prédateurs)
   - fuite : fuite d'une menace (proies)

4. **Vagabond** (Vagabondage)
   - Exploration quand un boid est seul
   - Comportement aléatoire naturel

### États Émergents
   - Énergie : diminue avec le temps, augmente en chassant
   - Peur : influence l'intensité de la fuite

### Écosystème Dynamique (NEW!)

Un système complet de vie et mort simulant les dynamiques Lotka-Volterra :

1. **Métabolisme**
   - Proies : perte d'énergie de 0.1/frame
   - Prédateurs : perte d'énergie de 0.5/frame (plus gourmands)

2. **Reproduction (Proies)**
   - Conditions : énergie > 80, âge > 50 frames
   - Probabilité : 2% par frame
   - Coût : -30 énergie pour le parent
   - Effet : nouveau boid à proximité du parent

3. **Chasse (Prédateurs)**
   - Gain : +40 énergie en mangeant une proie
   - Distance de capture : < 10 pixels

4. **Mort**
   - Quand l'énergie atteint 0, le boid meurt
   - Les boids morts sont retirés de la simulation

5. **Oscillations de Population**
   - Beaucoup de proies → beaucoup de prédateurs
   - Beaucoup de prédateurs → peu de proies (surpâturage)
   - Peu de proies → peu de prédateurs (famine)
   - Peu de prédateurs → beaucoup de proies (récupération)
   - Le cycle recommence !

6. **Indicateurs Visuels**
   - Couleur normale : énergie > 30
   - Couleur sombre : énergie < 30 (affamé)
   - Les boids morts ne sont plus affichés

### Interactions Multi-Groupes

- **Prédateurs** chassent la proie la plus proche
- **Proies** fuient les prédateurs dans leur rayon de vision
- **Capture** : les proies disparaissent si attrapées
- **Récompense** : les prédateurs gagnent de l'énergie en capturant

### Bordures & Espace Adaptatif

- **Rebondissement** : les boids rebondissent sur les bords (avec amortissement)
- **Espace adaptatif** : la simulation s'adapte automatiquement à la taille de la fenêtre

## Compilation et Exécution

Voir le fichier `MANUEL_UTILISATEUR.md` pour les instructions détaillées.

**Compilation rapide** :
```bash
make
```

**Exécution** :
```bash
make run           # Test simple (proies seulement)
make run-multi     # Test avancé (prédateurs vs proies)
make run-ecosystem # Écosystème dynamique avec reproduction et mort ⭐
```

## Tests Disponibles

1. **TestEventManager** : Validation du système d'événements
2. **TestBoids** : 50 proies avec flocking classique
3. **TestMultiGroupBoids** : 60 proies + 8 prédateurs avec capture
4. **TestEcosystem** ⭐ : Écosystème dynamique avec reproduction, énergie, et mort
   - 30 proies (bleues)
   - 10 prédateurs (rouges)
   - Population oscillante (modèle Lotka-Volterra)
   - Indicateurs visuels d'énergie

## Dépendances

- **JDK 8+** (Java)
- **lib/gui.jar** : Bibliothèque graphique fournie (GUISimulator)

## Auteur & Contexte

Projet réalisé dans le cadre du cours **2A POO** (Programmation Orientée Objet).

**Concepts avancés appliqués** :
- Design patterns (Abstract Factory, Template Method)
- Architecture événementielle
- Simulations multi-agents
- Steering behaviors & intelligence artificielle
