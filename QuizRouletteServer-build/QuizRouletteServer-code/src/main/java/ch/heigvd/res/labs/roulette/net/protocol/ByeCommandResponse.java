package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {
    private String status;
    private int nbCommands;

    public ByeCommandResponse(String status, int nbCommands){
        this.status = status;
        this.nbCommands = nbCommands;
    }

    public String getStatus(){
        return status;
    }

    public int getNbCommands(){
        return nbCommands;
    }
}
