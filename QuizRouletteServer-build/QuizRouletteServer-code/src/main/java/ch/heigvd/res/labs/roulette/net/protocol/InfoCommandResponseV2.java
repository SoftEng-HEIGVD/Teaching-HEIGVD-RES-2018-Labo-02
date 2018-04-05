package ch.heigvd.res.labs.roulette.net.protocol;

public class InfoCommandResponseV2 extends InfoCommandResponse {
    private String status;
    private int numberOfNewStudents;
    private int numberOfCommands;

    public InfoCommandResponseV2(Boolean status) {
        setStatus(status);
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public void setNumberOfCommands(int numberOfCommands){
        this.numberOfCommands = numberOfCommands;
    }

    public void setStatus(Boolean status){
        this.status = status? "success" : "failure";
    }

    /*

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    */
}
