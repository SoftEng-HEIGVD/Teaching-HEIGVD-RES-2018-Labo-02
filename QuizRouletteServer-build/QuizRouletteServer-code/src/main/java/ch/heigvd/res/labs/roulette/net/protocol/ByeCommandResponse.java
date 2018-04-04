package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Olivier Kopp
 */

public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse(String status, int numberOfCommands){
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public ByeCommandResponse(){}

        public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public String getStatus() {
        return status;
    }
}
