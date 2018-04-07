package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {

    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(){}

    public ByeCommandResponse(String status, int numberOfCommands){
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public int getNumberOfCommands(){
        return numberOfCommands;
    }

    public String getStatus(){
        return status;
    }

    public void setNumberOfCommands(int nbCommands){
        this.numberOfCommands = nbCommands;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
