package LKhalaya;

/**
 * Classe abstraite représentant une grille cellulaire générique.
 * Factorise les attributs et méthodes communes à tous les automates cellulaires
 * (Jeu de la Vie, Jeu de l'Immigration, etc.).
 */
public abstract class CellularGrid {
    
    protected final int rows;
    protected final int cols;
    
    /**
     * Constructeur de la grille cellulaire.
     * @param rows nombre de lignes
     * @param cols nombre de colonnes
     */
    public CellularGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }
    
    /**
     * Calcule l'état suivant de la grille (une génération/étape).
     */
    public abstract void step();
    
    /**
     * Réinitialise la grille à son état initial.
     */
    public abstract void reInit();
    
    /**
     * @return le nombre de lignes de la grille
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * @return le nombre de colonnes de la grille
     */
    public int getCols() {
        return cols;
    }
}
