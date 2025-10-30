# Guide Rapide - Enhancements Nature of Code

## 🎯 Changements Principaux

### Avant vs Après

#### 1. Mouvement des Boids

**AVANT** (mouvement instantané) :
```java
// Changements de direction brusques
velocity = velocity.add(acceleration).limit(maxSpeed);
position = position.add(velocity);
```

**APRÈS** (steering fluide) :
```java
// Virages progressifs et naturels
force = force.limit(maxForce);  // ← Limite la force de steering
velocity = velocity.add(force).limit(maxSpeed);
position = position.add(velocity);
```

#### 2. Perception des Voisins

**AVANT** (vision à 360°) :
```java
// Le boid voit dans toutes les directions
if (distance < visionRadius) {
    // Réagir au voisin
}
```

**APRÈS** (champ de vision réaliste) :
```java
// Le boid ne voit que devant lui
if (distance < visionRadius) {
    double cosAngle = velocity.dot(toNeighbor);
    if (cosAngle > Math.cos(fieldOfView / 2)) {
        // Réagir au voisin (seulement s'il est devant)
    }
}
```

#### 3. Comportement des Proies

**AVANT** (flocking simple) :
```java
// Seulement cohésion + alignement + séparation
applyForce(cohesion);
applyForce(alignment);
applyForce(separation);
```

**APRÈS** (comportement riche) :
```java
// Flocking + fuite + vagabondage
applyForce(cohesion);
applyForce(alignment);
applyForce(separation);
applyForce(flee * (1.0 + fear));  // ← Amplifié par la peur
applyForce(wander);               // ← Si isolée
```

#### 4. Comportement des Prédateurs

**AVANT** (chasse simple) :
```java
// Poursuite basique
Vecteur2D chase = Rules.seek(predator, prey.position, visionRadius);
chase = chase.mult(chaseWeight);
```

**APRÈS** (chasse intelligente) :
```java
// Poursuite avec faim et énergie
Vecteur2D chase = Rules.seek(predator, prey.position, maxSpeed);
double hungerFactor = 1.0 + (1.0 - energy);  // ← Plus affamé = plus agressif
chase = chase.mult(chaseWeight * hungerFactor);

// Manger pour récupérer de l'énergie
if (distance < 10.0) {
    predator.increaseEnergy(0.05);
}
```

## 📊 Paramètres Recommandés

### Proies (Rapides et Réactives)
```
maxSpeed:       2.5
maxForce:       0.2      // Virages serrés
fieldOfView:    162°     // Vision large (détection prédateurs)
fleeWeight:     3.0      // Fuite prioritaire
wanderWeight:   0.3      // Exploration modérée
```

### Prédateurs (Puissants mais Moins Agiles)
```
maxSpeed:       3.5      // Plus rapides
maxForce:       0.15     // Virages larges (moins maniables)
fieldOfView:    108°     // Vision focalisée
chaseWeight:    2.0      // Chasse importante
```

## 🔑 Nouvelles Méthodes Clés

### Dans `Vecteur2D.java`
```java
double heading()           // Angle du vecteur
double dot(Vecteur2D v)    // Produit scalaire (calcul d'angle)
```

### Dans `Boid.java`
```java
// États internes
double energy              // 0.0 → 1.0 (diminue au fil du temps)
double fear                // 0.0 → 1.0 (augmente près des prédateurs)

// Méthodes
void increaseEnergy(double amount)
void increaseFear(double amount)
void update(int width, int height)  // Nouvelle signature sans maxSpeed
```

### Dans `Rules.java`
```java
// Nouvelles signatures avec field-of-view
Vecteur2D cohesion(Boid b, List<Boid> boids, double visionRadius, double fieldOfView)
Vecteur2D alignment(Boid b, List<Boid> boids, double visionRadius, double fieldOfView)
Vecteur2D separation(Boid b, List<Boid> boids, double separationDistance, double fieldOfView)

// Comportements améliorés (steering-based)
Vecteur2D seek(Boid b, Vecteur2D target, double maxSpeed)
Vecteur2D flee(Boid b, Vecteur2D threat, double maxSpeed)

// Nouveau comportement
Vecteur2D wander(Boid b, double wanderStrength)
```

### Dans `PreyBoidSystem.java`
```java
void addPredatorSystem(AbstractBoidSystem predatorSystem)
```

## 🎬 Comportements Émergents à Observer

Lancez `TestMultiGroupBoids` et observez :

1. ✅ **Trajectoires courbes naturelles** - Les boids ne font plus de virages à 90°
2. ✅ **Proies ignorant les prédateurs derrière elles** - Vision limitée réaliste
3. ✅ **Groupes de proies paniquées** - Amplification de la peur quand prédateur proche
4. ✅ **Prédateurs plus agressifs quand affamés** - Énergie basse → chasse intense
5. ✅ **Boids isolés explorant** - Comportement de vagabondage quand seul
6. ✅ **Mouvements fluides sans saccades** - Force de steering limitée

## 🚀 Compilation et Exécution

```bash
# Compilation
javac -d bin -classpath lib/gui.jar:bin src/multi_agents/**/*.java

# Test groupe unique (recommandé pour débuter)
java -classpath bin:lib/gui.jar multi_agents.tests.TestBoids

# Test prédateur-proie (comportements complets)
java -classpath bin:lib/gui.jar multi_agents.tests.TestMultiGroupBoids
```

## 📈 Ajustement des Paramètres

Pour expérimenter, modifiez dans `TestMultiGroupBoids.java` :

- **maxForce** ↑ = virages plus serrés
- **maxForce** ↓ = virages plus larges
- **fieldOfView** ↑ = vision plus large
- **fieldOfView** ↓ = vision tunnel
- **fleeWeight** ↑ = fuite plus forte
- **chaseWeight** ↑ = chasse plus agressive

## 🎓 Concepts Théoriques

### Steering Forces (Craig Reynolds)
Au lieu de définir directement la vitesse, on calcule une **force de steering** :

```
desired = normalize(target - position) * maxSpeed
steering = desired - velocity
steering = limit(steering, maxForce)  ← Clé pour fluidité
```

### Field of View
Vision réaliste basée sur le produit scalaire :

```
dot(velocity, toNeighbor) > cos(fieldOfView/2)  → neighbor visible
dot(velocity, toNeighbor) ≤ cos(fieldOfView/2)  → neighbor invisible
```

### États Internes (Emergent Behavior)
Les variables `energy` et `fear` créent des **variations individuelles** :
- Deux boids identiques se comportent différemment selon leur état
- Comportements émergents au niveau du groupe

---

**Documentation complète** : `docs/ENHANCEMENTS.md`
