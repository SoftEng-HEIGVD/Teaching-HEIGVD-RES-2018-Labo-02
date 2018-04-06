package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Daniel Gonzalez Lopez, Héléna Line Reymond
 */
public class ByeCommandResponse {

    private String commandStatus;
    private int totalCommands;

    public ByeCommandResponse(String commandStatus, int totalCommands) {
        this.commandStatus = commandStatus;
        this.totalCommands = totalCommands;
    }

    public String getCommandStatus() {
        return commandStatus;
    }

    public int getTotalCommands() {
        return totalCommands;
    }

    public void setCommandStatus(String commandStatus) {
        this.commandStatus = commandStatus;
    }

    public void setTotalCommands(int totalCommands) {
        this.totalCommands = totalCommands;
    }
}
