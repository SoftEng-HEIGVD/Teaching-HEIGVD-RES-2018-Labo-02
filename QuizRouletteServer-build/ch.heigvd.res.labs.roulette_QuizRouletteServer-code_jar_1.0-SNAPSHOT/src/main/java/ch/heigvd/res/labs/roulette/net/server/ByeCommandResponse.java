package ch.heigvd.res.labs.roulette.net.server;

/**
 * @author Doriane Kaffo
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public ByeCommandResponse() {
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public String getStatus() {
        return status;
    }
}
