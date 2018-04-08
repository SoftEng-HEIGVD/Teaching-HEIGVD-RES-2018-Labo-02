package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponseV2 {
    private Boolean status;
    private int numberOfCommands;

    public ByeCommandResponseV2(){};

    public ByeCommandResponseV2(Boolean status, int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
        this.status = status;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }

    public int getNumberOfCommands(){
        return numberOfCommands;
    }

    public boolean getStatus() {return status;}

}
