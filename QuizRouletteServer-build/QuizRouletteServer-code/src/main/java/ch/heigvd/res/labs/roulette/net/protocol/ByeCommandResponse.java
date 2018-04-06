package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
        status = "success";     //creating BYE response means command is successfully executed
    }
    public String getStatus(){
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
