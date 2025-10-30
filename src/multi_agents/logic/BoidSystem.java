package multi_agents.logic;

import gui.GUISimulator;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite qui représente un système de boids,
 * elle factorise les trucs communs à tous les types de boids (proies, prédateurs ...)
 */
public abstract class BoidSystem {
    protected List<Boid> boids;
    protected double rayonVision;
    protected double distanceSep;
    protected double Vmax;
    protected double Fmax;        // Force de steering maximale
    protected double angleVision;     // Angle de vision en radians
    protected double poiDECohesion;
    protected double poiDEAlignement;
    protected double poiDESeparation;
    protected int width;
    protected int height;
    
    // Liste des autres systèmes pour les interactions inter-groupes
    protected List<BoidSystem> interGroups;

    /**
     * Constructeur qui initialise un système avec un certain nombre de boids
     * @param nbBoids le nombre de boids à créer dans ce système
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @param rayonVision le rayon dans lequel un boid peut voir ses voisins
     * @param distanceSep la distance minimale à respecter entre boids
     * @param Vmax la vitesse maximale autorisée pour les boids
     * @param Fmax la force de steering maximale (pour des mouvements fluides)
     * @param angleVision l'angle de vision
     * @param poiDECohesion l'importance de la règle de cohésion
     * @param poiDEAlignement le poids de la règle d'alignement
     * @param poiDESeparation le poids de la règle de séparation
     */
    public BoidSystem(int nbBoids, int width, int height,
                               double rayonVision, double distanceSep, double Vmax,
                               double Fmax, double angleVision,
                               double poiDECohesion, double poiDEAlignement, double poiDESeparation) {
        this.boids = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.rayonVision = rayonVision;
        this.distanceSep = distanceSep;
        this.Vmax = Vmax;
        this.Fmax = Fmax;
        this.angleVision = angleVision;
        this.poiDECohesion = poiDECohesion;
        this.poiDEAlignement = poiDEAlignement;
        this.poiDESeparation = poiDESeparation;
        this.interGroups = new ArrayList<>();

        // On crée les boids avec des positions aléatoires
        for (int i = 0; i < nbBoids; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            Boid b = new Boid(x, y);
            // On configure les paramètres de chaque boid
            b.Vmax = Vmax;
            b.Fmax = Fmax;
            boids.add(b);
        }
    }

    /**
     * Méthode abstraite qui effectue une étape de simulation.
     * C'est ici que chaque type de boid va définir son comportement spécifique.
     */
    public abstract void step();
    
    /**
     * Indique si ce système est un système de prédateurs.
     * Permet d'éviter instanceof et respecter le principe du polymorphisme.
     * @return true si ce système représente des prédateurs, false sinon
     */
    public abstract boolean isPredator();
    
    /**
     * Indique si ce système est un système de proies.
     * Permet d'éviter instanceof et respecter le principe du polymorphisme.
     * @return true si ce système représente des proies, false sinon
     */
    public abstract boolean isPrey();

    /**
     * Réinitialise tout le système en replaçant les boids aléatoirement.
     * Utile quand on clique sur le bouton "Restart" de l'interface.
     * @param gui le simulateur graphique (pour récupérer les dimensions si besoin)
     */
    public void reInit(GUISimulator gui) {
        int nbBoids = boids.size();
        boids.clear();
        for (int i = 0; i < nbBoids; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            Boid b = new Boid(x, y);
            b.Vmax = Vmax;
            b.Fmax = Fmax;
            boids.add(b);
        }
    }

    /**
     * Réinitialise le système en recréant un nombre spécifique de boids.
     * Pratique si on veut changer le nombre de boids en cours de simulation.
     * @param nbBoids le nouveau nombre de boids à créer
     */
    public void reInit(int nbBoids) {
        boids.clear();
        for (int i = 0; i < nbBoids; i++) {
            double x = Math.random() * width;
            double y = Math.random() * height;
            Boid b = new Boid(x, y);
            b.Vmax = Vmax;
            b.Fmax = Fmax;
            boids.add(b);
        }
    }

    /**
     * Accesseur qui renvoie la liste de tous les boids du système.
     * @return la liste des boids
     */
    public List<Boid> getBoids() {
        return boids;
    }

    /**
     * Renvoie le nombre de boids actuellement dans le système.
     * @return le nombre de boids
     */
    public int size() {
        return boids.size();
    }
    
    /**
     * Définit les autres systèmes de boids avec lesquels ce système peut interagir.
     * Utile pour les interactions prédateur-proie.
     * @param others la liste des autres systèmes
     */
    public void setinterGroups(List<BoidSystem> others) {
        this.interGroups = others;
    }
    
    /**
     * Récupère la liste des autres systèmes connus par ce système.
     * @return la liste des autres systèmes
     */
    public List<BoidSystem> getinterGroups() {
        return interGroups;
    }
    
    /**
     * Met à jour les dimensions de l'espace de simulation.
     * Cette méthode permet d'adapter dynamiquement la taille de la simulation
     * à la taille de la fenêtre GUI, rendant le système responsive.
     * @param newWidth la nouvelle largeur de l'espace de simulation
     * @param newHeight la nouvelle hauteur de l'espace de simulation
     */
    public void màjDimensions(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
    }
    
    /**
     * Méthode utilitaire pour mettre à jour tous les boids avec leurs accélérations respectives.
     * Permet d'éviter la duplication de code dans les sous-classes.
     * @param accelerations liste des vecteurs d'accélération (un par boid)
     */
    protected void màjBoids(List<Vecteur2D> accelerations) {
        for (int i = 0; i < boids.size(); i++) {
            Boid b = boids.get(i);
            Vecteur2D acc = accelerations.get(i);
            b.limiterForce(acc);
            b.màj(width, height);
        }
    }
}
