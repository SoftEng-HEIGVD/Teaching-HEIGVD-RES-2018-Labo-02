package ch.heigvd.res.labs.roulette.net.protocol;

public class InfoCommandResponseV3 {
    private String status;
    private int numberOfCommands;

    public InfoCommandResponseV3(Boolean status, int numberOfCommands) {
        setNumberOfCommands(numberOfCommands);
        setStatus(status);
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public void setStatus(Boolean status){
        this.status = status? "success" : "failure";
    }

    public int getNumberOfCommands(){
        return numberOfCommands;
    }

}
