# Guide de Conversion des Documents en PDF

## Fichiers Créés

Tous les documents demandés ont été créés dans `src/multi_agents/` :

1. ✅ **README.md** - Explication des fichiers et dossiers
2. ✅ **CONCEPTION.md** - Document de conception (à convertir en PDF, 4 pages max)
3. ✅ **MANUEL_UTILISATEUR.md** - Documentation utilisateur (à convertir en PDF, 2 pages max)
4. ✅ **Makefile** - Fichier de compilation fonctionnel

## Conversion Markdown → PDF

### Option 1 : Pandoc (Recommandé)

**Installation :**
```bash
# macOS
brew install pandoc

# Linux
sudo apt-get install pandoc texlive-latex-base

# Windows
# Télécharger depuis https://pandoc.org/installing.html
```

**Conversion :**
```bash
cd /Users/mohammedakramlrhorfi/IdeaProjects/JAVA_POO/src/multi_agents

# Document de conception (4 pages)
pandoc CONCEPTION.md -o CONCEPTION.pdf \
  --pdf-engine=xelatex \
  -V geometry:margin=2cm \
  -V fontsize=10pt \
  --toc

# Manuel utilisateur (2 pages)
pandoc MANUEL_UTILISATEUR.md -o MANUEL_UTILISATEUR.pdf \
  --pdf-engine=xelatex \
  -V geometry:margin=2cm \
  -V fontsize=10pt
```

### Option 2 : VS Code Extension

1. Installer l'extension "Markdown PDF" dans VS Code
2. Ouvrir `CONCEPTION.md`
3. Cliquer droit → "Markdown PDF: Export (pdf)"
4. Répéter pour `MANUEL_UTILISATEUR.md`

### Option 3 : En Ligne (Plus Simple)

1. Aller sur https://www.markdowntopdf.com/
2. Copier-coller le contenu de `CONCEPTION.md`
3. Cliquer "Convert to PDF"
4. Télécharger le PDF
5. Répéter pour `MANUEL_UTILISATEUR.md`

### Option 4 : Google Docs

1. Ouvrir Google Docs
2. Fichier → Importer → Sélectionner `CONCEPTION.md`
3. Fichier → Télécharger → PDF
4. Répéter pour `MANUEL_UTILISATEUR.md`

## Vérification du Nombre de Pages

### CONCEPTION.md
- Contient ~4 pages de contenu
- Sections : Architecture, Choix de conception, Tests, Conclusion
- Si dépasse 4 pages, réduire les exemples de code

### MANUEL_UTILISATEUR.md
- Contient ~2 pages de contenu
- Sections : Présentation, Installation, Utilisation, Dépannage
- Si dépasse 2 pages, réduire la section "Résolution de problèmes"

## Structure Finale à Rendre

```
multi_agents/
├── README.md                      ✅ Créé
├── CONCEPTION.pdf                 ⏳ À convertir depuis CONCEPTION.md
├── MANUEL_UTILISATEUR.pdf         ⏳ À convertir depuis MANUEL_UTILISATEUR.md
├── Makefile                       ✅ Créé et testé
├── core/
│   ├── Boid.java
│   ├── Vecteur2D.java
│   ├── AbstractBoidSystem.java
│   ├── PreyBoidSystem.java
│   ├── PredatorBoidSystem.java
│   └── Rules.java
├── events/
│   ├── Event.java
│   ├── EventManager.java
│   └── BoidUpdateEvent.java
├── sim/
│   ├── AbstractSimulator.java
│   ├── BoidSimulator.java
│   └── RotatedImageElement.java
└── tests/
    ├── TestEventManager.java
    ├── TestBoids.java
    └── TestMultiGroupBoids.java
```

## Test Final Avant Rendu

```bash
cd /Users/mohammedakramlrhorfi/IdeaProjects/JAVA_POO/src/multi_agents

# 1. Test du Makefile
make clean
make
make run-multi

# 2. Vérification des fichiers
ls -la README.md CONCEPTION.md MANUEL_UTILISATEUR.md Makefile

# 3. Test de toutes les cibles Makefile
make help
make run
make run-events
make clean
```

## Résumé des Livrables

| Document | Fichier Source | Format Final | Pages Max | Statut |
|----------|---------------|--------------|-----------|--------|
| Explication fichiers/dossiers | README.md | Markdown | N/A | ✅ Créé |
| Document de conception | CONCEPTION.md | PDF | 4 | ⏳ À convertir |
| Documentation utilisateur | MANUEL_UTILISATEUR.md | PDF | 2 | ⏳ À convertir |
| Fichier de compilation | Makefile | Makefile | N/A | ✅ Créé et testé |

## Notes Importantes

1. **README.md** peut rester en Markdown (pas besoin de PDF)
2. **CONCEPTION.md** et **MANUEL_UTILISATEUR.md** doivent être convertis en PDF
3. Le **Makefile** fonctionne correctement (testé avec `make help`)
4. Tous les documents contiennent les informations demandées

## Commandes de Vérification Rapide

```bash
# Voir la structure
tree src/multi_agents -L 2

# Compter les lignes de chaque document
wc -l src/multi_agents/*.md

# Tester le Makefile
cd src/multi_agents && make clean && make && make run
```
