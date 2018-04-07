package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created on 05.04.18.
 *
 * @author Max
 */
public class ByeCommandResponse {
    private String status = "failed";
    private int numberOfCommands = 0;

    public ByeCommandResponse(int nbCommands) {
        numberOfCommands = nbCommands;
        status = "success";
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

}
