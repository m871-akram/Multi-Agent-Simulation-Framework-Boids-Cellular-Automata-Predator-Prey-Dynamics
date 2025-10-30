package LKhalaya;

import gui.GUISimulator;
import gui.Rectangle;

import java.awt.Color;

/**
 * Simulateur pour le Jeu de la Vie de Conway.
 * Hérite de CellularSimulator et implémente uniquement la logique de dessin.
 */
public class JeuVieSimulator extends CellularSimulator {

    private final JeuVie grid;
    private final Color aliveColor;
    private final Color deadColor;

    /**
     * @param gui l'interface graphique
     * @param grid la grille du jeu de la vie
     * @param cellHalf paramètre "size" du Rectangle du gui.jar (moitié de côté)
     * @param aliveColor couleur des cellules vivantes
     * @param deadColor couleur des cellules mortes
     */
    public JeuVieSimulator(GUISimulator gui, JeuVie grid, int cellHalf, Color aliveColor, Color deadColor) {
        super(gui, cellHalf);
        this.grid = grid;
        this.aliveColor = aliveColor;
        this.deadColor = deadColor;
        draw(); // dessin initial après initialisation complète
    }

    @Override
    protected CellularGrid getGrid() {
        return grid;
    }

    @Override
    protected void draw() {
        gui.reset();
        // On dessine chaque cellule comme un petit carré
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                int cx = cellHalf + j * step; // centre x
                int cy = cellHalf + i * step; // centre y
                Color fill = grid.isAlive(i, j) ? aliveColor : deadColor;
                // bord = même couleur (tu peux mettre une autre couleur si tu veux un contour)
                gui.addGraphicalElement(new Rectangle(cx, cy, fill, fill, cellHalf));
            }
        }
    }
}
