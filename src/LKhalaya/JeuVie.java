package LKhalaya;

import java.util.Random;

/** Modèle du Jeu de la vie de Conway (grille torique). */
public class JeuVie extends CellularGrid {

    private final boolean[][] init;     // état initial
    private boolean[][] current;        // état courant

    /** Crée une grille vide (toutes mortes). */
    public JeuVie(int rows, int cols) {
        super(rows, cols);
        this.init = new boolean[rows][cols];
        this.current = new boolean[rows][cols];
    }

    /** Initialisation aléatoire avec probabilité 'aliveProb' d'être vivante. */
    public void randomInit(double aliveProb, long seed) {
        Random r = new Random(seed);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                init[i][j] = r.nextDouble() < aliveProb;
                current[i][j] = init[i][j];
            }
        }
    }

    /** Repart de l'état initial. */
    @Override
    public void reInit() {
        for (int i = 0; i < rows; i++) {
            System.arraycopy(init[i], 0, current[i], 0, cols);
        }
    }

    /** Accès lecture à l'état courant d'une cellule. */
    public boolean isAlive(int i, int j) {
        return current[i][j];
    }

    /** Calcule une génération (règles de Conway) sur une grille TORIQUE. */
    @Override
    public void step() {
        boolean[][] next = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int n = countNeighborsToroidal(i, j);
                if (current[i][j]) {
                    // vivante → survit si 2 ou 3 voisines vivantes
                    next[i][j] = (n == 2 || n == 3);
                } else {
                    // morte → naît si exactement 3 voisines vivantes
                    next[i][j] = (n == 3);
                }
            }
        }
        current = next; // on remplace l'état courant par le nouvel état
    }


    /** Compte les voisines vivantes en considérant la grille comme torique. */
    private int countNeighborsToroidal(int i, int j) {
        int count = 0;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = (i + di + rows) % rows;
                int nj = (j + dj + cols) % cols;
                if (current[ni][nj]) count++;
            }
        }
        return count;
    }
}



