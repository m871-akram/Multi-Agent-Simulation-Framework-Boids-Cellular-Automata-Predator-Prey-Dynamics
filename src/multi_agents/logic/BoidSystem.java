package multi_agents.logic;

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
     * C'est ici que chaque type de boid va définir son comportement spécifique
     */
    public abstract void step();
    
    /**
     * Permet d'éviter instanceof et respecter le principe du polymorphisme
     * @return true si ce système représente des prédateurs, false sinon
     */
    public abstract boolean estpredateur();
    
    /**
     * @return true si ce système représente des proies, false sinon
     */
    public abstract boolean estproie();

    /**
     * Réinitialise le système en recréant un nombre spécifique de boids
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
     * @return la liste des boids
     */
    public List<Boid> getBoids() {
        return boids;
    }

    /**
     * @return le nombre de boids
     */
    public int size() {
        return boids.size();
    }

    /**
     * @param others la liste des autres systèmes
     */
    public void setinterGroups(List<BoidSystem> others) {
        this.interGroups = others;
    }

    
    /**
     * Méthode pour mettre à jour tous les boids avec leurs accélérations respectives
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
