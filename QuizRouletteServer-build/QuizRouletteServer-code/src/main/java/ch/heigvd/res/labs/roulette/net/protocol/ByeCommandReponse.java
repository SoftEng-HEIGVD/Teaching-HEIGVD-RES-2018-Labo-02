/**
 * This class is used to serialize/deserialize the response sent by the
 * server when processing the "BYE" command defined in the protocol
 * specification. The JsonObjectMapper utility class can use this class.
 *
 * @author Bryan Curchod, François Burgener
 */
package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandReponse {

    private String status;
    private int numberOfCommands;

    public ByeCommandReponse() {
    }

    public ByeCommandReponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

}
