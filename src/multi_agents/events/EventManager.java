package multi_agents.events;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Gestionnaire d'événements pour notre simulation discrète.
 * Il gère une file de priorité où les événements sont triés par date.
 * À chaque appel de next(), on exécute l'événement le plus ancien de la file.
 */
public class EventManager {
    private long currentDate;
    private PriorityQueue<Event> events;

    /**
     * Constructeur qui initialise le gestionnaire.
     * Au départ la date est à 0 et la file d'événements est vide.
     */
    public EventManager() {
        this.currentDate = 0;
        // On utilise une PriorityQueue qui trie automatiquement les événements par date
        this.events = new PriorityQueue<>(Comparator.comparingLong(Event::getDate));
    }

    /**
     * Ajoute un nouvel événement dans la file d'attente.
     * Attention : on ne peut pas ajouter un événement dans le passé !
     * @param e l'événement à ajouter
     */
    public void addEvent(Event e) {
        if (e.getDate() < currentDate) {
            throw new IllegalArgumentException("Impossible d'ajouter un événement dans le passé: " + e.getDate() + " < " + currentDate);
        }
        events.add(e);
    }

    /**
     * Exécute le prochain événement de la file (celui avec la date la plus petite).
     * On met à jour la date courante avec celle de l'événement exécuté.
     */
    public void next() {
        if (!events.isEmpty()) {
            Event e = events.poll(); // poll() retire l'élément avec la plus petite priorité
            currentDate = e.getDate();
            e.execute();
        }
    }

    /**
     * Réinitialise complètement le gestionnaire d'événements.
     * On vide la file et on remet la date à 0, comme au début.
     */
    public void restart() {
        currentDate = 0;
        events.clear();
    }

    /**
     * Vérifie si la file d'événements est vide.
     * @return true s'il n'y a plus d'événements à traiter
     */
    public boolean isEmpty() {
        return events.isEmpty();
    }

    /**
     * Renvoie la date courante de la simulation.
     * @return la date actuelle (en pas de temps)
     */
    public long getCurrentDate() {
        return currentDate;
    }
}
