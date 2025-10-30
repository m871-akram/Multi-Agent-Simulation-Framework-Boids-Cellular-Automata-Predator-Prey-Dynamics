package multi_agents.core;

/**
 * Classe représentant un vecteur 2D avec toutes les opérations mathématiques usuelles.
 * On l'utilise pour gérer les positions, vitesses et accélérations des boids.
 */
public class Vecteur2D {
    public double x;
    public double y;

    /**
     * Constructeur par défaut : crée le vecteur nul (0, 0).
     */
    public Vecteur2D() {
        this(0, 0);
    }

    /**
     * Constructeur avec les coordonnées données.
     * @param x la coordonnée en x
     * @param y la coordonnée en y
     */
    public Vecteur2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructeur de copie : permet de dupliquer un vecteur existant.
     * @param v le vecteur à copier
     */
    public Vecteur2D(Vecteur2D v) {
        this(v.x, v.y);
    }

    /**
     * Addition de deux vecteurs (this + v).
     * @param v le vecteur à ajouter
     * @return un nouveau vecteur égal à la somme
     */
    public Vecteur2D add(Vecteur2D v) {
        return new Vecteur2D(this.x + v.x, this.y + v.y);
    }

    /**
     * Soustraction de deux vecteurs (this - v).
     * @param v le vecteur à soustraire
     * @return un nouveau vecteur égal à la différence
     */
    public Vecteur2D sub(Vecteur2D v) {
        return new Vecteur2D(this.x - v.x, this.y - v.y);
    }

    /**
     * Multiplication par un scalaire.
     * @param scalar le coefficient multiplicateur
     * @return un nouveau vecteur multiplié par le scalaire
     */
    public Vecteur2D mult(double scalar) {
        return new Vecteur2D(this.x * scalar, this.y * scalar);
    }

    /**
     * Division par un scalaire.
     * Attention : si le scalaire vaut 0, on renvoie le vecteur nul.
     * @param scalar le coefficient diviseur
     * @return un nouveau vecteur divisé par le scalaire
     */
    public Vecteur2D div(double scalar) {
        if (scalar == 0) {
            return new Vecteur2D(0, 0);
        }
        return new Vecteur2D(this.x / scalar, this.y / scalar);
    }

    /**
     * Calcul de la norme (ou longueur) du vecteur.
     * On utilise le théorème de Pythagore : sqrt(x² + y²).
     * @return la norme du vecteur
     */
    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Normalisation du vecteur pour obtenir un vecteur de norme 1.
     * Utile pour avoir une direction unitaire.
     * @return un nouveau vecteur normalisé (de norme 1)
     */
    public Vecteur2D normalize() {
        double n = norm();
        if (n == 0) {
            return new Vecteur2D(0, 0);
        }
        return div(n);
    }

    /**
     * Limite la norme du vecteur à une valeur maximale donnée.
     * Si la norme dépasse max, on réduit le vecteur proportionnellement.
     * @param max la norme maximale autorisée
     * @return un nouveau vecteur dont la norme ne dépasse pas max
     */
    public Vecteur2D limit(double max) {
        double n = norm();
        if (n > max) {
            return normalize().mult(max);
        }
        return new Vecteur2D(this);
    }

    /**
     * Calcule la distance euclidienne entre ce vecteur et un autre.
     * @param v l'autre vecteur
     * @return la distance entre les deux vecteurs
     */
    public double distance(Vecteur2D v) {
        return sub(v).norm();
    }

    /**
     * Calcule l'angle (heading) du vecteur en radians.
     * C'est l'angle par rapport à l'axe des x positifs.
     * Pratique pour savoir dans quelle direction pointe le boid.
     * @return l'angle en radians (entre -PI et PI)
     */
    public double heading() {
        return Math.atan2(y, x);
    }

    /**
     * Produit scalaire (dot product) entre ce vecteur et un autre.
     * Utile pour calculer l'angle entre deux vecteurs.
     * Si le résultat est > 0, les vecteurs pointent dans des directions similaires.
     * @param v l'autre vecteur
     * @return le produit scalaire
     */
    public double dot(Vecteur2D v) {
        return this.x * v.x + this.y * v.y;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
