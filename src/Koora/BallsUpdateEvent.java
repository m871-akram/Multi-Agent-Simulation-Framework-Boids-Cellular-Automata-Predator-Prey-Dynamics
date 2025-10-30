package Koora;

import multi_agents.EvenT.Event;

public class BallsUpdateEvent extends Event {
    private Balls balls;
    private BallsSimulator simulator;

    public BallsUpdateEvent(long date, Balls balls, BallsSimulator simulator) {
        super(date);
        this.balls = balls;
        this.simulator = simulator;
    }

    @Override
    public void execute() {
        balls.Rebond(simulator.gui.getWidth(), simulator.gui.getHeight());
        simulator.draw();
        simulator.manager.addEvent(new BallsUpdateEvent(getDate() + 1, balls, simulator));
    }
}