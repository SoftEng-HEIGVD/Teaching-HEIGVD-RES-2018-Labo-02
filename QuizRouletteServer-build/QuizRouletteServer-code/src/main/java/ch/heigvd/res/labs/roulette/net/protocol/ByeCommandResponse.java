package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Marc Labie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ByeCommandResponse {

    private String error;

    private String status;

    private int nbrOfCommands = 0;

    private String numberOfCommands = "" + nbrOfCommands;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumberOfCommands() {
        return numberOfCommands;
    }


    public void incrNbrOfCommands(){
        nbrOfCommands++;
        numberOfCommands = "" + nbrOfCommands;
    }


}
