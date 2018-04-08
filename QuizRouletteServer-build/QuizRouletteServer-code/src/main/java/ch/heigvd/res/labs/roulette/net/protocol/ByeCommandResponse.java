package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created on 05.04.18.
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification.
 * @author Max Caduff
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
