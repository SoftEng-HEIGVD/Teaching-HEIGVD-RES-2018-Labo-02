/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author Nathan
 */
public class DisconnectCommandResponse {
        
    private String status;
    private int numberOfCommands;
    
    public DisconnectCommandResponse() {}
    
    public DisconnectCommandResponse(String status, int numberOfCommands) {
       this.status = status;
       this.numberOfCommands  = numberOfCommands;
   }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int numberOfNewStudents) {
        this.numberOfCommands = numberOfNewStudents;
    }
    
    
}