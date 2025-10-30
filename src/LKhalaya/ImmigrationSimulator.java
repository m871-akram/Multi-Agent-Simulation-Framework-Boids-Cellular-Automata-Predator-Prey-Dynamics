package LKhalaya;

import gui.GUISimulator;
import gui.Rectangle;

import java.awt.Color;

/**
 * Simulateur pour le jeu de l'immigration.
 * Hérite de CellularSimulator et implémente uniquement la logique de dessin.
 */
public class ImmigrationSimulator extends CellularSimulator {
    
    private final ImmigrationGrid grid;
    private final Color[] palette; // couleurs associées aux états

    public ImmigrationSimulator(GUISimulator gui, ImmigrationGrid grid, Color[] palette, int cellHalf) {
        super(gui, cellHalf);
        this.grid = grid;
        this.palette = palette;
        draw(); // dessin initial après initialisation complète
    }

    @Override
    protected CellularGrid getGrid() {
        return grid;
    }

    @Override
    protected void draw() {
        gui.reset();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                int cx = cellHalf + j * step;
                int cy = cellHalf + i * step;
                int state = grid.getState(i, j);
                Color color = palette[state ];
                gui.addGraphicalElement(new Rectangle(cx, cy, color, color, cellHalf));
            }
        }
    }
}