package multi_agents.EvenT;

/**
 * Chaque événement possède une date et une action à exécuter quand cette date arrive
 */
public abstract class Event {
    private final long date;

    /**
     * Constructeur qui initialise un événement avec sa date d'exécution
     * @param date la date (en pas de temps) à laquelle cet événement doit s'exécuter
     */
    public Event(long date) {
        this.date = date;
    }

    /**
     * @return la date de l'événement
     */
    public long getDate() {
        return date;
    }

    /**
     * Chaque type d'événement concret devra implémenter cette méthode.
     */
    public abstract void execute();
}
