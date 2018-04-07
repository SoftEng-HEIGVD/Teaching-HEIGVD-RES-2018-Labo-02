package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {

    private int numberOfCommands;

    private String status;

    public ByeCommandResponse() {
    }

    public ByeCommandResponse(String status, int numberOfCommands) {

        this.numberOfCommands = numberOfCommands;
        this.status = status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
