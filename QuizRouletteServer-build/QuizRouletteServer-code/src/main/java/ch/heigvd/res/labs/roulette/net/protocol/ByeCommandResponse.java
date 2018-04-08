/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author youndzofrancine
 */
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public ByeCommandResponse() {
    }

    public ByeCommandResponse(String status, int numberOfCommands) {
        this.status = status;
        this.numberOfCommands = numberOfCommands;
    }

    public int getnumberOfCommands() {
        return numberOfCommands;
    }

    public void setnumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }
    
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
