# TPL 2A POO

Les ressources distribuées contiennent:

- une librairie d'affichage graphique d'un simulateur (lib/gui.jar) et sa documentation (doc/index.html)
- un fichier de démonstration du simulateur (src/TestInvader.java)


## Compilation & exécution
### Avec un makefile?
Un fichier Makefile est distribué pour facilement compiler et exécuter le fichier TestInvader.java

Mais vu la taille de ce projet, il est ***très fortement recommandé d'utiliser un IDE*** pour compiler, exécuter et déboguer votre code!

### IDE Idea Intellij
- créer un nouveau projet:
    - menu *File/New Project*
    - si le répertoire distribué est dans "~/Ensimag/2A/POO/TPL_2A_POO", alors paramétrer les champs *Name* avec "TPL_2A_POO" et *Location* avec "~/Ensimag/2A/POO/"
    - configurer l'utilisation de la librairie
    - menu *File/Project Structure* puis *Projet setting/Modules*
    - clicker sur(*Add* puis "JARs & Directories" et sélectionner ~/Ensimag/2A/POO/TPL_2A_POO/lib
    - vous pouvez bien sûr utiliser git via l'interface d'idea Intellij

### IDE VS Code
- dans "~/Ensimag/2A/POO/TPL_2A_POO", lancer *code ."
- si vous avez installé les bonnes extensions java (exécution, debogage...) il est possible que tout fonctionne sans rien faire de spécial.
- s'il ne trouve pas la librairie, vous devez alors créer un vrai "projet" et configurer l'import du .jar.
- pas vraiment d'aide pour ça, vous trouverez
- vous pouvez bien sûr utiliser git via l'interface de VS code

### FAQ (étudiants ou non)
- Q1) Pour le jeu de la vie, il est indiqué que la grille est circulaire. En revanche, aucune indication n'est donnée pour le jeu de l'immigration ni pour le jeu de Schelling. Faut-il également adopter une grille circulaire ? R1) Oui. Pour le calcul des voisinages, toutes les grilles sont considérées comme circulaires. 
