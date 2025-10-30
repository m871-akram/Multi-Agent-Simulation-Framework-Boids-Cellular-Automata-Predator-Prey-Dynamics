package multi_agents.simulation;

import gui.GraphicalElement;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Élément graphique personnalisé qui affiche une image tournée.
 * en faite j'avais pas pu faire de triangle avec Polygon , du coup je me suis creer MOI meme une classe pour utiliser
 * l'image de loupe que j aie retrouve dans le dossier fourni
 */
public class RotatedImageElement implements GraphicalElement {
    private int x;
    private int y;
    private String imagePath;
    private int width;
    private int height;
    private double angle;
    private Color tint;

    /**
     * Constructeur complet avec possibilité d'ajouter une teinte de couleur.
     * @param x la position x du centre de l'image
     * @param y la position y du centre de l'image
     * @param imagePath le chemin vers le fichier image
     * @param width la largeur d'affichage
     * @param height la hauteur d'affichage
     * @param angle l'angle de rotation (en radians)
     * @param tint une couleur de teinte à appliquer (ou null si pas de teinte)
     */
    public RotatedImageElement(int x, int y, String imagePath, int width, int height, double angle, Color tint) {
        this.x = x;
        this.y = y;
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.tint = tint;
    }

    /**
     * Dessine l'image rotée sur le contexte graphique.
     * On utilise AffineTransform pour gérer la rotation correctement.
     */
    @Override
    public void paint(Graphics2D g2d) {
        try {
            // On charge l'image à chaque fois ( on peut optimiser avec le cache )
            BufferedImage image = ImageIO.read(new File(imagePath));

            // On sauvegarde la transformation actuelle du contexte graphique
            AffineTransform oldTransform = g2d.getTransform();

            // On crée une nouvelle transformation pour la rotation
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y); // On se déplace au centre du boid
            transform.rotate(angle + Math.toRadians(360-45)); // On applique la rotation + un offset que vous devrez deviner en regardant l image de la loupe
            transform.translate(-width / 2.0, -height / 2.0); // On recentre l'image

            // On applique la transformation
            g2d.setTransform(transform);

            // On dessine l'image
            g2d.drawImage(image, 0, 0, width, height, null);

            // on applique un filtre de couleur semi-transparent
            if (tint != null) {
                g2d.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 100));
                g2d.fillRect(0, 0, width, height);
            }

            // On restaure la transformation d'origine
            g2d.setTransform(oldTransform);

        } catch (Exception e) {
            // Si l'image n'existe pas ou qu'il y a une erreur, on dessine juste un cercle
            g2d.setColor(tint != null ? tint : Color.BLACK);
            g2d.fillOval(x - width / 2, y - height / 2, width, height);
        }
    }
}
