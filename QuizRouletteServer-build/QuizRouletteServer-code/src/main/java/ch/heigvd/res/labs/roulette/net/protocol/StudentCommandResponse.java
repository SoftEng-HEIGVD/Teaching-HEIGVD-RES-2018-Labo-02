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
public class StudentCommandResponse {
    
    private String status;
    private int numberOfNewStudents;
    
    public StudentCommandResponse() {}
    
    public StudentCommandResponse(String status, int numberOfStudentsAdded) {
       this.status = status;
       this.numberOfNewStudents  = numberOfStudentsAdded;
   }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }
    
    
}
