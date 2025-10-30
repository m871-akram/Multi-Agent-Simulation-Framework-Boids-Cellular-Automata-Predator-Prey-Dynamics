package LKhalaya;

import gui.GUISimulator;

import java.awt.Color;

public class TestConway {

    public static void main(String[] args) {
        // Fenêtre
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        // Modèle : grille 40x60
        int rows = 70, cols = 80;
        JeuVie grid = new JeuVie(rows, cols);

        // Init aléatoire (25% de vivantes), seed fixe pour rejouabilité
        grid.randomInit(0.25, 42L);

        // Simu graphique
        int cellHalf = 5;      // demi-côté des cellules (pixels)


        new JeuVieSimulator(
                gui,
                grid,
                cellHalf,
                Color.decode("#1f77b4"), // vivante (bleu)
                Color.decode("#222222")  // morte (gris très foncé)
        );
    }
}






























