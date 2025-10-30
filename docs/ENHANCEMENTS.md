# Enhancements de Boids - Nature of Code

Ce document décrit toutes les améliorations apportées au système multi-agents pour créer des comportements plus naturels et réalistes, inspirés du livre *The Nature of Code* de Daniel Shiffman.

## 🎯 Vue d'ensemble

Les boids utilisent maintenant un modèle physique complet basé sur les **steering behaviors** (comportements de pilotage), avec :
- **Forces de steering limitées** pour des mouvements fluides et réalistes
- **Champ de vision angulaire** (field of view) pour une perception réaliste
- **États internes** (énergie, peur) pour des comportements émergents
- **Comportements avancés** (vagabondage, poursuite, fuite)

## 🪶 1. Steering-Force Based Motion

### Changements dans `Vecteur2D.java`
Ajout de deux nouvelles méthodes essentielles :

```java
public double heading()        // Retourne l'angle du vecteur en radians
public double dot(Vecteur2D v) // Produit scalaire pour calculer l'angle entre vecteurs
```

### Changements dans `Boid.java`
- **Nouveaux attributs** :
  - `maxSpeed` : vitesse maximale du boid
  - `maxForce` : force de steering maximale (empêche les virages trop brusques)
  - `energy` : niveau d'énergie (1.0 = plein, 0.0 = épuisé)
  - `fear` : niveau de peur (augmente près des prédateurs)

- **Nouveau modèle de mise à jour** :
```java
// Ancienne méthode (instantanée) :
velocity = velocity.add(acceleration).limit(maxSpeed);

// Nouvelle méthode (steering avec force limitée) :
applyForce(force.limit(maxForce));  // Force limitée pour mouvements fluides
velocity = velocity.add(acceleration).limit(maxSpeed);
```

- **Méthodes utilitaires** :
  - `increaseEnergy(double)` : augmente l'énergie (ex: après avoir mangé)
  - `increaseFear(double)` : augmente la peur (ex: prédateur proche)

## 🧭 2. MaxForce Limitation

Chaque boid limite maintenant ses forces de steering avec `maxForce` :
- **Prédateurs** : `maxForce = 0.15` → virages plus larges, poursuite fluide
- **Proies** : `maxForce = 0.2` → plus réactives, changements de direction rapides

Cela crée des trajectoires courbes naturelles au lieu de virages à angle droit.

## 👁️ 3. Field-of-View (Angle-Based Perception)

### Implémentation dans `Rules.java`
Toutes les règles de flocking vérifient maintenant si un voisin est **dans le cône de vision** :

```java
Vecteur2D toOther = other.position.sub(b.position).normalize();
Vecteur2D velocity = b.vitesse.normalize();
double cosAngle = velocity.dot(toOther);

// Voisin visible seulement si dans le cône de vision
if (cosAngle > Math.cos(fieldOfView / 2)) {
    // Appliquer la règle de flocking...
}
```

### Paramètres de vision
- **Proies** : `fieldOfView = π * 0.9` (162°) → vision très large pour détecter les prédateurs
- **Prédateurs** : `fieldOfView = π * 0.6` (108°) → vision plus focalisée pour la chasse

Les boids ne réagissent plus aux voisins derrière eux, créant un comportement plus réaliste.

## 🌀 4. Wander Behavior (Vagabondage)

Nouveau comportement dans `Rules.java` :

```java
public static Vecteur2D wander(Boid b, double wanderStrength) {
    double theta = Math.random() * 2 * Math.PI;
    Vecteur2D randomDir = new Vecteur2D(Math.cos(theta), Math.sin(theta));
    return randomDir.mult(wanderStrength);
}
```

**Utilisation** : Les proies vagabondent quand elles sont isolées (pas de voisins détectés), créant un comportement exploratoire naturel.

## 🦈 5. Seek and Flee Behaviors (Améliorés)

### Nouvelle implémentation (steering basé sur maxSpeed)

```java
// Seek : poursuivre une cible
public static Vecteur2D seek(Boid b, Vecteur2D target, double maxSpeed) {
    Vecteur2D desired = target.sub(b.position).normalize().mult(maxSpeed);
    return desired.sub(b.vitesse); // steering = desired - velocity
}

// Flee : fuir une menace
public static Vecteur2D flee(Boid b, Vecteur2D threat, double maxSpeed) {
    Vecteur2D desired = b.position.sub(threat).normalize().mult(maxSpeed);
    return desired.sub(b.vitesse);
}
```

**Différence clé** : Retourne maintenant un **steering vector** (non normalisé) au lieu d'une direction simple, permettant des mouvements plus progressifs.

## ⚡ 6. Energy and Fear States

### États internes des boids

#### Énergie (`energy`)
- **Prédateurs** :
  - Diminue lentement au fil du temps (`energy -= 0.001`)
  - Augmente quand ils sont proches d'une proie (`energy += 0.05` si distance < 10)
  - Influence la motivation de chasse : `hungerFactor = 1.0 + (1.0 - energy)`

#### Peur (`fear`)
- **Proies** :
  - Augmente quand un prédateur est détecté (`fear += 0.1`)
  - Diminue progressivement si pas de menace (`fear -= 0.05`)
  - Amplifie le comportement de fuite : `flee.mult(fleeWeight * (1.0 + fear))`

Ces états créent des **comportements émergents** :
- Prédateurs affamés chassent plus agressivement
- Proies effrayées fuient plus rapidement

## 🔄 7. Changements dans les systèmes

### `AbstractBoidSystem.java`
Nouveaux paramètres obligatoires :
```java
protected double maxForce;      // Force de steering maximale
protected double fieldOfView;   // Angle de vision en radians
```

Constructeur mis à jour avec ces paramètres.

### `PreyBoidSystem.java`
Nouveaux comportements :
1. **Fuite des prédateurs** :
   - Détecte le prédateur le plus proche
   - Fuit si distance < `rayonVision * 1.5`
   - La peur amplifie la force de fuite

2. **Vagabondage** :
   - Active quand pas de voisins détectés
   - Force faible (`wanderWeight = 0.3`)

3. **Communication avec prédateurs** :
   - Méthode `addPredatorSystem()` pour enregistrer les systèmes de prédateurs

### `PredatorBoidSystem.java`
Améliorations de la chasse :
1. **Seek amélioré** :
   - Utilise le nouveau `Rules.seek()` avec maxSpeed
   - Facteur de faim amplifie la poursuite

2. **Système de "manger"** :
   - Si distance à la proie < 10, le prédateur récupère de l'énergie
   - Simule la capture et l'alimentation

## 📊 8. Paramètres finaux (TestMultiGroupBoids)

### Proies (Cyan - 60 boids)
```java
maxSpeed = 2.5
maxForce = 0.2        // Réactives
fieldOfView = 162°    // Vision très large
fleeWeight = 3.0      // Fuite prioritaire
wanderWeight = 0.3    // Exploration modérée
updateDelay = 1       // Mises à jour rapides
```

### Prédateurs (Red - 8 boids)
```java
maxSpeed = 3.5        // Plus rapides que les proies
maxForce = 0.15       // Virages plus larges
fieldOfView = 108°    // Vision focalisée
chaseWeight = 2.0     // Chasse importante
updateDelay = 3       // Mises à jour moins fréquentes
```

## 🎯 Comportements émergents observables

Grâce à ces améliorations, les simulations exhibent maintenant :

1. **Trajectoires courbes et fluides** (maxForce)
2. **Boids ignorant ce qui est derrière eux** (field of view)
3. **Proies fuyant en groupes paniqués** (fear + flee)
4. **Prédateurs chassant plus agressivement quand affamés** (energy)
5. **Boids isolés explorant aléatoirement** (wander)
6. **Mouvements réalistes sans virages à 90°** (steering forces)

## 🚀 Utilisation

### Compilation
```bash
javac -d bin -classpath lib/gui.jar:bin src/multi_agents/**/*.java
```

### Exécution
```bash
# Groupe unique de proies
java -classpath bin:lib/gui.jar multi_agents.tests.TestBoids

# Prédateurs vs Proies (recommandé)
java -classpath bin:lib/gui.jar multi_agents.tests.TestMultiGroupBoids
```

## 📚 Références

- **The Nature of Code** - Daniel Shiffman (Chapitre 6: Autonomous Agents)
- **Steering Behaviors For Autonomous Characters** - Craig Reynolds (1999)
- **Flocks, Herds, and Schools** - Reynolds (1987)

---

*Toutes les améliorations sont compatibles avec l'architecture existante et peuvent être désactivées en ajustant les poids à 0.*
