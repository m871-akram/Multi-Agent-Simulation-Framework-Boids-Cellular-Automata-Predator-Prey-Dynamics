package multi_agents.logic;

/**
 * Classe représentant un boid (agent autonome) dans une simulation de flocking
 */
public class Boid {
    /** Position du boid  */
    Vecteur2D position;
    
    /** Vecteur vitesse du boid (package-protected pour accès efficace dans LaLoi) */
    Vecteur2D vitesse;
    
    /** Vecteur accélération */
    private Vecteur2D acceleration;

    /** Vitesse maximale autorisée  */
    double Vmax;
    
    /** Force de steering maximale pour des virages fluides*/
    double Fmax;
    
    /** Énergie du boid (diminue avec le temps, augmente en mangeant) - privée pour garantir l'intégrité */
    private double energie;
    
    /** Âge du boid en nombre de frame */
    private int age;
    
    /** Indique si le boid est vivant (mort si énergie <= 0) */
    private boolean vivant;

    /**
     * Constructeur qui initialise un boid 
     * @param x la position initiale en x
     * @param y la position initiale en y
     */
    public Boid(double x, double y) {
        this.position = new Vecteur2D(x, y);
        // Vitesse aléatoire entre -1 et 1 pour chaque composante
        this.vitesse = new Vecteur2D(Math.random() * 2 - 1, Math.random() * 2 - 1);
        this.acceleration = new Vecteur2D(0, 0);
        this.Vmax = 3.0;
        this.Fmax = 0.1;
        this.energie = 100.0;  // Énergie maximale au départ
        this.age = 0;
        this.vivant = true;
    }

    /**
     * La force est limitée par Fmax pour un mouvement plus fluide et réaliste.
     * @param force le vecteur force à appliquer
     */
    public void limiterForce(Vecteur2D force) {
        this.acceleration = this.acceleration.add(force.limit(Fmax));
    }

    /**
     * Met à jour la position et la vitesse du boid selon les lois 
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     */
    public void màj(int width, int height) {
        // Mise à jour avec steering : vitesse += accélération (limitée)
        vitesse = vitesse.add(acceleration).limit(Vmax);

        // Puis on met à jour la position avec la nouvelle vitesse
        position = position.add(vitesse);
        
        if (position.x < 0) {
            position.x = 0;
            vitesse.x = Math.abs(vitesse.x); // Rebond vers la droite
        } else if (position.x > width) {
            position.x = width;
            vitesse.x = -Math.abs(vitesse.x); // Rebond vers la gauche
        }
        
        if (position.y < 0) {
            position.y = 0;
            vitesse.y = Math.abs(vitesse.y); // Rebond vers le bas
        } else if (position.y > height) {
            position.y = height;
            vitesse.y = -Math.abs(vitesse.y); // Rebond vers le haut
        }

        //  on remet l'accélération à zéro pour le prochain cycle
        acceleration = new Vecteur2D(0, 0);
        
        // Mise à jour des états internes
        age++;  // Le boid vieillit
    }
    
    /**
     * Fait perdre de l'énergie au boid par exeemple par mouvement 
     * Si l'énergie atteint 0, le boid meurt
     * @param mana la quantité d'énergie à perdre
     */
    public void fatigue(double mana) {
        this.energie -= mana;
        if (this.energie <= 0) {
            this.energie = 0;
            this.vivant = false;
        }
    }
    
    /**
     * Augmente l'énergie du boid ( quand on mange ).
     * L'énergie est limitée à 100 ( on est pas supersayen ici )
     * @param mana la quantité d'énergie à ajouter
     */
    public void gainenergie(double mana) {
        this.energie = Math.min(100.0, this.energie + mana);
    }

    /**
     * Renvoie la position actuelle du boid
     * @return la position du boid
     */
    public Vecteur2D getPosition() {
        return position;
    }
    
    /**
     * Renvoie la vitesse actuelle du boid
     * @return la vitesse du boid
     */
    public Vecteur2D getVitesse() {
        return vitesse;
    }

    /**
     * Renvoie la vitesse maximale du boid
     * @return la vitesse maximale
     */
    public double getVmax() {
        return Vmax;
    }

    /**
     * Définit la vitesse maximale du boid
     * @param Vmax la nouvelle vitesse maximale
     * @throws IllegalArgumentException si Vmax est négative
     */
    public void setVmax(double Vmax) {
        if (Vmax < 0) {
            throw new IllegalArgumentException("La vitesse maximale doit être positive");
        }
        this.Vmax = Vmax;
    }

    /**
     * Renvoie le niveau d'énergie actuel du boid
     * @return l'énergie
     */
    public double getenergie() {
        return energie;
    }

    
    /**
     * Renvoie l'âge du boid en frames
     * @return l'âge (nombre de frames depuis la naissance)
     */
    public int getAge() {
        return age;
    }
    
    /**
     * Indique si le boid est vivant
     * @return true si le boid est vivant, false s'il est mort (énergie épuisée)
     */
    public boolean estvivant() {
        return vivant;
    }
    
    @Override
    public String toString() {
        return String.format("Boid[pos=%s, vel=%s, energie=%.2f, age=%d, vivant=%b]", 
                           position, vitesse, energie, age, vivant);
    }
}
