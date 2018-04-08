/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;


/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * @author fidimala
 */

public class ByeCommandResponse {
    private final String status;
    private final int numberOfCommands;

    public ByeCommandResponse(String status, int nbOfCommands) {
        this.status = status;
        this.numberOfCommands = nbOfCommands;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }
}
