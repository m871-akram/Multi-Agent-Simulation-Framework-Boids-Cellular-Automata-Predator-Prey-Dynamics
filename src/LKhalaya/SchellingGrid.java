package LKhalaya;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Modèle de ségrégation de Schelling.
 * Chaque cellule peut être vide (0) ou habitée par une famille de couleur c (1..nColors).
 */
public class SchellingGrid extends CellularGrid {
    private final int nColors;          // nombre de couleurs (familles)
    private final int K;                // seuil de tolérance
    private final double vacantRatio;   // proportion de logements vides
    private final int[][] init;
    private int[][] current;
    private List<Point> emptyHouses;    // positions vides disponibles
    private final Random rand = new Random();

    public SchellingGrid(int rows, int cols, int nColors, int K, double vacantRatio) {
        super(rows, cols);
        this.nColors = nColors;
        this.K = K;
        this.vacantRatio = vacantRatio;
        this.init = new int[rows][cols];
        this.current = new int[rows][cols];
        randomInit();
    }

    private void randomInit() {
        emptyHouses = new ArrayList<>();
        int total = rows * cols;
        int vacantCount = (int) (total * vacantRatio);

        // Crée une liste de toutes les positions
        List<Point> allPositions = new ArrayList<>();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                allPositions.add(new Point(i, j));

        Collections.shuffle(allPositions, rand);

        // Les premières sont vides
        for (int i = 0; i < total; i++) {
            Point p = allPositions.get(i);
            if (i < vacantCount) {
                init[p.x][p.y] = 0;
                emptyHouses.add(new Point(p));
            } else {
                init[p.x][p.y] = 1 + rand.nextInt(nColors); // couleur entre 1 et nColors
            }
            current[p.x][p.y] = init[p.x][p.y];
        }
    }

    @Override
    public void reInit() {
        for (int i = 0; i < rows; i++) {
            System.arraycopy(init[i], 0, current[i], 0, cols);
        }
    }

    @Override
    public void step() {
        List<Point> unhappy = new ArrayList<>();

        // Trouver les familles insatisfaites
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int color = current[i][j];
                if (color == 0) continue; // vide
                if (isUnhappy(i, j, color)) unhappy.add(new Point(i, j));
            }
        }

        // Déménagement : chaque famille insatisfaite prend une maison vide
        Collections.shuffle(unhappy, rand);
        Collections.shuffle(emptyHouses, rand);

        int moves = Math.min(unhappy.size(), emptyHouses.size());
        for (int m = 0; m < moves; m++) {
            Point oldPos = unhappy.get(m);
            Point newPos = emptyHouses.get(m);

            // La famille déménage
            int color = current[oldPos.x][oldPos.y];
            current[newPos.x][newPos.y] = color;
            current[oldPos.x][oldPos.y] = 0;

            // Mise à jour des logements vides
            emptyHouses.set(m, oldPos);
        }
    }

    private boolean isUnhappy(int i, int j, int color) {
        int diff = 0;
        int total = 0;

        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = (i + di + rows) % rows;
                int nj = (j + dj + cols) % cols;

                int neighbor = current[ni][nj];
                if (neighbor != 0) {
                    total++;
                    if (neighbor != color) diff++;
                }
            }
        }
        return total > 0 && diff > K;
    }

    public int getState(int i, int j) {
        return current[i][j];
    }


}