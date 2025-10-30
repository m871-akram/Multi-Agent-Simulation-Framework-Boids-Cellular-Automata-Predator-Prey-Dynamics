package LKhalaya;

import java.util.Random;

/** Modèle du jeu de l'immigration (généralisation du jeu de la vie). */
public class ImmigrationGrid extends CellularGrid {
    private final int nStates;         // nombre d'états (ex : 4)
    private final int[][] init;        // état initial
    private int[][] current;           // état courant

    public ImmigrationGrid(int rows, int cols, int nStates) {
        super(rows, cols);
        this.nStates = nStates;
        this.init = new int[rows][cols];
        this.current = new int[rows][cols];
    }

    /** Initialisation aléatoire : chaque cellule prend un état entre 0 et n-1. */
    public void randomInit(long seed) {
        Random r = new Random(seed);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int val = r.nextInt(nStates);
                init[i][j] = val;
                current[i][j] = val;
            }
        }
    }

    /** Réinitialise la grille à son état de départ. */
    @Override
    public void reInit() {
        for (int i = 0; i < rows; i++) {
            System.arraycopy(init[i], 0, current[i], 0, cols);
        }
    }

    /** Renvoie l'état d'une cellule. */
    public int getState(int i, int j) {
        return current[i][j];
    }

    /** Calcule l'état suivant (tous en même temps, grille torique). */
    @Override
    public void step() {
        int[][] next = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int currentState = current[i][j];
                int nextState = (currentState + 1) % nStates;

                int neighborsNext = countNeighborsWithState(i, j, nextState);

                if (neighborsNext >= 3) {
                    next[i][j] = nextState;   // passe à l’état suivant
                } else {
                    next[i][j] = currentState; // reste inchangé
                }
            }
        }

        current = next;
    }

    /** Compte le nombre de voisines dans un état donné (grille torique). */
    private int countNeighborsWithState(int i, int j, int targetState) {
        int count = 0;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = (i + di + rows) % rows;
                int nj = (j + dj + cols) % cols;
                if (current[ni][nj] == targetState)
                    count++;
            }
        }
        return count;
    }
}