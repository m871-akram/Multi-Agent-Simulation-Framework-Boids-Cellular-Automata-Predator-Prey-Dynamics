package LKhalaya;

import gui.GUISimulator;
import gui.Simulable;

/**
 * Classe abstraite représentant un simulateur générique pour automate cellulaire.
 * Factorise la logique commune de simulation (next, restart) et délègue
 * le dessin spécifique aux sous-classes.
 */
public abstract class CellularSimulator implements Simulable {
    
    protected final GUISimulator gui;
    protected final int cellHalf;
    protected final int step;
    
    /**
     * Constructeur du simulateur.
     * @param gui l'interface graphique
     * @param cellHalf demi-taille de chaque cellule (paramètre "size" de Rectangle)
     */
    public CellularSimulator(GUISimulator gui, int cellHalf) {
        this.gui = gui;
        this.cellHalf = cellHalf;
        this.step = 2 * cellHalf;
        
        gui.setSimulable(this);
        // Note : le dessin initial sera fait par la sous-classe après initialisation complète
    }
    
    /**
     * @return la grille cellulaire associée à ce simulateur
     */
    protected abstract CellularGrid getGrid();
    
    /**
     * Dessine l'état courant de la grille dans l'interface graphique.
     * Chaque sous-classe implémente sa propre logique de dessin.
     */
    protected abstract void draw();
    
    @Override
    public void next() {
        getGrid().step();
        draw();
    }
    
    @Override
    public void restart() {
        getGrid().reInit();
        draw();
    }
}
