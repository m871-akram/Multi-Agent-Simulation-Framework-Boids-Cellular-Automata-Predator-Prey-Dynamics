package Koora;

// src/Balls.java
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un ensemble de balles avec position et vitesse.
 * Cette classe ne contient aucune logique graphique.
 */
public class Balls {
    private List<Point> balls;           // positions courantes
    private List<Point> PositionsInit; // positions initiales
    private List<Point> Vitesse;       // vitesses (vx, vy)
    private int rayon;                   // rayon des balles

    /**
     * Constructeur : initialise les positions, vitesses et le rayon.
     * @param PositionsInit positions initiales des balles
     * @param VitesseInit vitesses initiales des balles (vx, vy)
     * @param rayon rayon des balles
     */
    public Balls(List<Point> PositionsInit, List<Point> VitesseInit, int rayon) {
        this.PositionsInit = new ArrayList<>();
        this.balls = new ArrayList<>();
        this.Vitesse = new ArrayList<>();
        this.rayon = rayon;

        // Copie profonde des listes
        for (int i = 0; i < PositionsInit.size(); i++) {
            this.PositionsInit.add(new Point(PositionsInit.get(i)));
            this.balls.add(new Point(PositionsInit.get(i)));
            this.Vitesse.add(new Point(VitesseInit.get(i)));
        }
    }



    /**
     * Déplace toutes les balles selon leurs vitesses et gère les rebonds sur les bords.
     * @param width largeur de la fenêtre
     * @param height hauteur de la fenêtre
     */
    public void Rebond(int width, int height) {
        for (int i = 0; i < balls.size(); i++) {
            Point pos = balls.get(i);
            Point vel = Vitesse.get(i);

            // Mise à jour de la position
            pos.x += vel.x;
            pos.y += vel.y;

            // Rebond horizontal
            if (pos.x < rayon) {
                pos.x = rayon;
                vel.x = -vel.x;
            } else if (pos.x > width - rayon) {
                pos.x = width - rayon;
                vel.x = -vel.x;
            }

            // Rebond vertical
            if (pos.y < rayon) {
                pos.y = rayon;
                vel.y = -vel.y;
            } else if (pos.y > height - rayon) {
                pos.y = height - rayon;
                vel.y = -vel.y;
            }
        }
    }

    /**
     * Réinitialise toutes les balles à leurs positions de départ.
     */
    public void reInit() {
        for (int i = 0; i < balls.size(); i++) {
            Point init = PositionsInit.get(i);
            Point curr = balls.get(i);
            curr.x = init.x;
            curr.y = init.y;
        }
    }

    /**
     * Retourne une copie de la liste des positions courantes.
     */
    public List<Point> getBalls() {
        List<Point> copy = new ArrayList<>();
        for (Point p : balls) {
            copy.add(new Point(p));
        }
        return copy;
    }

    public int getRayon() {
        return rayon;
    }


    @Override
    public String toString() {
        return balls.toString();
    }
}