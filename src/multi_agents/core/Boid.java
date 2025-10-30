package multi_agents.core;

/**
 * Classe représentant un boid (agent autonome) dans une simulation de flocking.
 * Chaque boid possède une position, une vitesse et une accélération qui évoluent au cours du temps.
 * On ajoute aussi maxSpeed et maxForce pour un mouvement plus réaliste (steering behaviors).
 * 
 * Principe d'encapsulation respecté : les attributs sensibles (énergie, peur) sont privés,
 * tandis que position/vitesse sont package-protected pour un accès efficace dans les classes
 * du même package (Rules, AbstractBoidSystem) tout en restant masqués de l'extérieur.
 */
public class Boid {
    /** Position du boid dans l'espace 2D (package-protected pour accès efficace dans Rules) */
    Vecteur2D position;
    
    /** Vecteur vitesse du boid (package-protected pour accès efficace dans Rules) */
    Vecteur2D vitesse;
    
    /** Vecteur accélération (réinitialisé à chaque frame) */
    private Vecteur2D acceleration;
    
    /** Vitesse maximale autorisée (pour un mouvement réaliste, package-protected) */
    double maxSpeed;
    
    /** Force de steering maximale (pour des virages fluides, package-protected) */
    double maxForce;
    
    /** Énergie du boid (diminue avec le temps, augmente en mangeant) - privée pour garantir l'intégrité */
    private double energy;
    
    /** Niveau de peur (augmente si des prédateurs sont proches) - privée pour garantir l'intégrité */
    private double fear;

    /**
     * Constructeur qui initialise un boid à une position donnée.
     * La vitesse initiale est aléatoire pour avoir un comportement naturel au départ.
     * @param x la position initiale en x
     * @param y la position initiale en y
     */
    public Boid(double x, double y) {
        this.position = new Vecteur2D(x, y);
        // Vitesse aléatoire entre -1 et 1 pour chaque composante
        this.vitesse = new Vecteur2D(Math.random() * 2 - 1, Math.random() * 2 - 1);
        this.acceleration = new Vecteur2D(0, 0);
        this.maxSpeed = 3.0;
        this.maxForce = 0.1;
        this.energy = 1.0;
        this.fear = 0.0;
    }

    /**
     * Applique une force au boid en l'ajoutant à son accélération.
     * C'est ici qu'on accumule toutes les forces (cohésion, séparation, etc.).
     * La force est limitée par maxForce pour un mouvement plus fluide et réaliste.
     * @param force le vecteur force à appliquer
     */
    public void applyForce(Vecteur2D force) {
        // On limite la force pour éviter des changements de direction trop brusques
        Vecteur2D limitedForce = force.limit(maxForce);
        this.acceleration = this.acceleration.add(limitedForce);
    }

    /**
     * Met à jour la position et la vitesse du boid selon les lois de la physique.
     * On utilise maintenant un modèle de steering avec maxSpeed et maxForce.
     * On applique aussi un système de "wrap-around" pour que les boids réapparaissent de l'autre côté.
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     */
    public void update(int width, int height) {
        // Mise à jour avec steering : vitesse += accélération (limitée)
        vitesse = vitesse.add(acceleration);
        vitesse = vitesse.limit(maxSpeed);

        // Puis on met à jour la position avec la nouvelle vitesse
        position = position.add(vitesse);

        // Gestion des bords : rebondissement (au lieu de wrap-around)
        // Si le boid touche un bord, on inverse la composante de vitesse correspondante
        boolean bounced = false;
        
        if (position.x < 0) {
            position.x = 0;
            vitesse.x = Math.abs(vitesse.x); // Rebond vers la droite
            bounced = true;
        } else if (position.x > width) {
            position.x = width;
            vitesse.x = -Math.abs(vitesse.x); // Rebond vers la gauche
            bounced = true;
        }
        
        if (position.y < 0) {
            position.y = 0;
            vitesse.y = Math.abs(vitesse.y); // Rebond vers le bas
            bounced = true;
        } else if (position.y > height) {
            position.y = height;
            vitesse.y = -Math.abs(vitesse.y); // Rebond vers le haut
            bounced = true;
        }
        
        // Si on a rebondi, on ajoute un peu d'amortissement pour éviter les rebonds infinis
        if (bounced) {
            vitesse = vitesse.mult(0.9);
        }

        // Important : on remet l'accélération à zéro pour le prochain cycle
        acceleration = new Vecteur2D(0, 0);
        
        // Mise à jour des états internes
        energy = Math.max(0, energy - 0.001); // L'énergie diminue lentement
        fear = Math.max(0, fear - 0.05);      // La peur diminue si pas de menace
    }
    
    /**
     * Version de update compatible avec l'ancienne API (pour ne pas casser le code existant).
     * @param vmax la vitesse maximale (maintenant gérée comme attribut)
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @deprecated Utilisez plutôt update(width, height) - maxSpeed est maintenant un attribut
     */
    @Deprecated
    public void update(double vmax, int width, int height) {
        this.maxSpeed = vmax;
        update(width, height);
    }
    
    /**
     * Augmente l'énergie du boid (par exemple après avoir mangé).
     * @param amount la quantité d'énergie à ajouter
     */
    public void increaseEnergy(double amount) {
        this.energy = Math.min(1.0, this.energy + amount);
    }
    
    /**
     * Augmente la peur du boid (par exemple quand un prédateur est proche).
     * @param amount la quantité de peur à ajouter
     */
    public void increaseFear(double amount) {
        this.fear = Math.min(1.0, this.fear + amount);
    }

    // ========== GETTERS ET SETTERS (Encapsulation) ==========
    
    /**
     * Renvoie la position actuelle du boid.
     * @return la position du boid
     */
    public Vecteur2D getPosition() {
        return position;
    }
    
    /**
     * Renvoie la vitesse actuelle du boid.
     * @return la vitesse du boid
     */
    public Vecteur2D getVitesse() {
        return vitesse;
    }
    
    /**
     * Renvoie l'accélération actuelle du boid.
     * @return l'accélération du boid
     */
    public Vecteur2D getAcceleration() {
        return acceleration;
    }
    
    /**
     * Renvoie la vitesse maximale du boid.
     * @return la vitesse maximale
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }
    
    /**
     * Définit la vitesse maximale du boid.
     * @param maxSpeed la nouvelle vitesse maximale (doit être positive)
     * @throws IllegalArgumentException si maxSpeed est négative
     */
    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed < 0) {
            throw new IllegalArgumentException("La vitesse maximale doit être positive");
        }
        this.maxSpeed = maxSpeed;
    }
    
    /**
     * Renvoie la force de steering maximale du boid.
     * @return la force maximale
     */
    public double getMaxForce() {
        return maxForce;
    }
    
    /**
     * Définit la force de steering maximale du boid.
     * @param maxForce la nouvelle force maximale (doit être positive)
     * @throws IllegalArgumentException si maxForce est négative
     */
    public void setMaxForce(double maxForce) {
        if (maxForce < 0) {
            throw new IllegalArgumentException("La force maximale doit être positive");
        }
        this.maxForce = maxForce;
    }
    
    /**
     * Renvoie le niveau d'énergie actuel du boid.
     * @return l'énergie (entre 0 et 1)
     */
    public double getEnergy() {
        return energy;
    }
    
    /**
     * Renvoie le niveau de peur actuel du boid.
     * @return la peur (entre 0 et 1)
     */
    public double getFear() {
        return fear;
    }
    
    @Override
    public String toString() {
        return String.format("Boid[pos=%s, vel=%s, energy=%.2f, fear=%.2f]", 
                           position, vitesse, energy, fear);
    }
}
