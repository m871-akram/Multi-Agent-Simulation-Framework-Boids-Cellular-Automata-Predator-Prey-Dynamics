package LKhalaya;

import gui.GUISimulator;
import gui.Rectangle;

import java.awt.*;

/**
 * Simulateur graphique pour le modèle de Schelling.
 */
public class SchellingSimulator extends CellularSimulator {
    private final SchellingGrid grid;
    private final Color[] palette; // couleurs associées aux familles

    /** Wires up the grid and color palette, then draws the initial state. */
    public SchellingSimulator(GUISimulator gui, SchellingGrid grid, int cellHalf, Color[] palette) {
        super(gui, cellHalf);
        this.grid = grid;
        this.palette = palette;
        draw();
    }

    /** Returns the Schelling grid managed by this simulator. */
    @Override
    protected CellularGrid getGrid() {
        return grid;
    }

    /** Draws each cell as white if vacant, or in the corresponding family color. */
    @Override
    protected void draw() {
        gui.reset();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                int state = grid.getState(i, j);
                int cx = cellHalf + j * step;
                int cy = cellHalf + i * step;

                Color c;
                if (state == 0)
                    c = Color.WHITE; // logement vide
                else
                    c = palette[state - 1]; // couleur famille

                gui.addGraphicalElement(new Rectangle(cx, cy, c, c, cellHalf));
            }
        }
    }
}