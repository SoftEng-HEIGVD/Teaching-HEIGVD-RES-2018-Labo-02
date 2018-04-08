package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {

    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(String status, int numberOfCommands){
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus){
        status = newStatus;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int newNumberOfCommands){
        numberOfCommands = newNumberOfCommands;
    }


}
