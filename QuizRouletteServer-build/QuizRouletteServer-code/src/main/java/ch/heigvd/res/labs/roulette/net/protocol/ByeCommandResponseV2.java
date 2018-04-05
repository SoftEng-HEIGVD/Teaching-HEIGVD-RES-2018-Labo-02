package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponseV2 {

    private String status;
    private int numberOfCommands;

    public ByeCommandResponseV2() {
    }

    public ByeCommandResponseV2(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getnumberOfCommands() {
        return numberOfCommands;
    }

    public void setnumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

}
