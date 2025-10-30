package multi_agents.events;

/**
 * Classe abstraite représentant un événement générique dans notre système de simulation.
 * Dans une simulation événementielle, tout se passe à des dates précises.
 * Chaque événement possède une date et une action à exécuter quand cette date arrive.
 */
public abstract class Event {
    private final long date;

    /**
     * Constructeur qui initialise un événement avec sa date d'exécution.
     * @param date la date (en pas de temps) à laquelle cet événement doit s'exécuter
     */
    public Event(long date) {
        this.date = date;
    }

    /**
     * Renvoie la date de cet événement.
     * @return la date de l'événement
     */
    public long getDate() {
        return date;
    }

    /**
     * Méthode abstraite qui définit ce que fait l'événement quand il se produit.
     * Chaque type d'événement concret devra implémenter cette méthode.
     */
    public abstract void execute();
}
