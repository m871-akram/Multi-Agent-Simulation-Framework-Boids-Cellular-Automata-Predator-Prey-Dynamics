# Manuel Utilisateur - Simulation Multi-Agents Boids

**Version :** 1.0  
**Date :** Octobre 2025

---

## 1. Présentation du Programme

Ce programme implémente une simulation interactive de **boids** (agents autonomes) avec des comportements émergents complexes. Il permet d'observer :

- **Flocking** : Groupes de boids se déplaçant de manière coordonnée
- **Prédation** : Interactions dynamiques prédateurs vs proies
- **Comportements adaptatifs** : Fuite, chasse, exploration

L'interface graphique affiche les boids en temps réel avec des couleurs différentes selon leur type.

---

## 2. Configuration Requise

### Logiciels
- **Java JDK 8 ou supérieur**
- Terminal (macOS/Linux) ou Command Prompt (Windows)

### Fichiers nécessaires
```
JAVA_POO/
├── lib/gui.jar                    # Bibliothèque graphique (fournie)
├── src/multi_agents/              # Code source
│   ├── core/
│   ├── events/
│   ├── sim/
│   └── tests/
├── bin/                           # Fichiers compilés (créé automatiquement)
└── Makefile                       # Fichier de compilation
```

### Vérification de Java
```bash
java -version
```
Doit afficher Java version 1.8 ou supérieure.

---

## 3. Compilation du Programme

### Option A : Avec Makefile (Recommandé)

**Depuis le répertoire racine du projet (`JAVA_POO/`) :**

```bash
cd src/multi_agents
make
```

**Résultat attendu :**
```
=== Compilation du projet Multi-Agents Boids ===
✓ Compilation réussie !
```

### Option B : Compilation Manuelle

**macOS/Linux :**
```bash
cd /chemin/vers/JAVA_POO
javac -d bin -classpath lib/gui.jar src/multi_agents/logic/*.java \
      src/multi_agents/EvenT/*.java src/multi_agents/simulation/*.java \
      src/multi_agents/TestTest/*.java
```

**Windows :**
```cmd
cd C:\chemin\vers\JAVA_POO
javac -d bin -classpath lib/gui.jar src\multi_agents\core\*.java ^
      src\multi_agents\events\*.java src\multi_agents\sim\*.java ^
      src\multi_agents\tests\*.java
```

**Note :** Sous Windows, utilisez `;` au lieu de `:` dans le classpath si besoin.

---

## 4. Exécution du Programme

### 4.1 Test Simple : Flocking de Proies

Ce test affiche **50 proies** (cyan) effectuant un flocking classique.

**Avec Makefile :**
```bash
make run
```

**Commande manuelle (macOS/Linux) :**
```bash
java -classpath bin:lib/gui.jar multi_agents.TestTestest.TestBoids
```

**Commande manuelle (Windows) :**
```cmd
java -classpath bin;lib/gui.jar multi_agents.TestTest.TestBoids
```

### 4.2 Test Avancé : Prédateurs vs Proies

Ce test affiche **60 proies** (cyan) et **8 prédateurs** (rouge) avec interactions dynamiques.

**Avec Makefile :**
```bash
make run-multi
```

**Commande manuelle (macOS/Linux) :**
```bash
java -classpath bin:lib/gui.jar multi_agents.TestTestest.TestMultiGroupBoids
```

**Commande manuelle (Windows) :**
```cmd
java -classpath bin;lib/gui.jar multi_agents.TestTest.TestMultiGroupBoids
```

### 4.3 Test du Gestionnaire d'Événements

Test unitaire qui valide le système événementiel.

**Avec Makefile :**
```bash
make run-EvenT
```

---

## 5. Utilisation de l'Interface Graphique

### 5.1 Fenêtre de Simulation

Une fois le programme lancé, une fenêtre s'ouvre affichant :

```
┌──────────────────────────────────────┐
│  [Next]  [Restart]            [X]    │  ← Barre de contrôle
├──────────────────────────────────────┤
│                                      │
│         🔵🔵  🔵🔵🔵                  │
│      🔵🔵  🔴  🔵🔵                   │  ← Zone de simulation
│         🔵🔵🔵  🔵🔵                   │     (cyan = proies)
│                                      │     (rouge = prédateurs)
└──────────────────────────────────────┘
```

### 5.2 Commandes Disponibles

| Bouton | Action | Description |
|--------|--------|-------------|
| **Next** | Exécute un pas de simulation | Avance la simulation d'un événement |
| **Restart** | Réinitialise la simulation | Replace tous les boids aléatoirement |
| **[X]** | Ferme la fenêtre | Quitte le programme |

**Note :** La simulation s'exécute automatiquement en continu. Le bouton "Next" est surtout utile pour déboguer pas à pas.

### 5.3 Éléments Visuels

**Proies (cyan - 🔵) :**
- Se déplacent en groupe (flocking)
- Fuient les prédateurs proches
- Représentées par des icônes bleues orientées selon leur direction

**Prédateurs (rouge - 🔴) :**
- Chassent les proies les plus proches
- Se déplacent moins souvent (tous les 3 pas)
- Capturent les proies à proximité (<10 pixels)

### 5.4 Comportements Observables

1. **Cohésion** : Les boids isolés rejoignent le groupe
2. **Alignement** : Les vitesses se synchronisent
3. **Séparation** : Évitement des collisions
4. **Fuite** : Les proies s'éloignent des prédateurs
5. **Chasse** : Les prédateurs poursuivent les proies
6. **Capture** : Les proies disparaissent quand attrapées
7. **Rebondissement** : Les boids rebondissent sur les bords

---

## 6. Paramètres de Simulation

Les paramètres sont définis dans les fichiers de test (`TestBoids.java`, `TestMultiGroupBoids.java`).

### Paramètres Modifiables

**Pour modifier le nombre de boids :**
```java
int nbBoids = 50;  // Changer cette valeur
```

**Pour ajuster le comportement :**
```java
double rayonVision = 100.0;      // Distance de détection des voisins
double distanceSep = 30.0;       // Distance minimale entre boids
double Vmax = 3.0;               // Vitesse maximale
double angleVision = Math.PI * 0.8;  // Angle de vision (144°)
```

**Pour modifier la fréquence de mise à jour :**
```java
simulator.addSystem(proieSystem, Color.CYAN, 1);  // delay=1 (rapide)
simulator.addSystem(predateurSystem, Color.RED, 3);  // delay=3 (plus lent)
```

**Après modification :** Recompiler avec `make` ou `javac`.

---

## 7. Commandes Makefile Avancées

### Génération de la Documentation

```bash
make doc
```
Génère la documentation Javadoc dans `doc/multi_agents/`.

### Nettoyage

```bash
make clean      # Supprime les fichiers compilés
make cleanall   # Supprime compilés + documentation
```

### Aide

```bash
make help       # Affiche toutes les commandes disponibles
```

---

## 8. Résolution de Problèmes

### Erreur : "javac: command not found"

**Solution :** Java n'est pas installé ou pas dans le PATH.
```bash
# macOS/Linux
export PATH=$PATH:/chemin/vers/jdk/bin

# Windows
set PATH=%PATH%;C:\chemin\vers\jdk\bin
```

### Erreur : "NoClassDefFoundError: gui/GUISimulator"

**Solution :** Le fichier `lib/gui.jar` est manquant ou le classpath est incorrect.
- Vérifiez que `lib/gui.jar` existe dans le répertoire racine
- Utilisez `:` (macOS/Linux) ou `;` (Windows) dans le classpath

### Fenêtre Vide

**Solution :** Les boids sont peut-être hors de la zone visible.
- Cliquez sur **Restart** pour replacer les boids
- Vérifiez que les dimensions (width, height) correspondent à la fenêtre

### Simulation Trop Rapide/Lente

**Solution :** Modifier le paramètre `delay` dans le code :
```java
simulator.addSystem(system, color, 1);  // 1=rapide, 5=lent
```

---

## 9. Contact & Support

Pour toute question sur l'utilisation du programme, consultez :
- Le fichier `README.md` pour la structure du projet
- Le fichier `CONCEPTION.md` pour les détails techniques
- La documentation Javadoc : `make doc` puis ouvrir `doc/multi_agents/index.html`
