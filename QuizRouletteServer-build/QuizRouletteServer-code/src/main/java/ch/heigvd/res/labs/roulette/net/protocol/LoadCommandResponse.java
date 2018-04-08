/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author youndzo francine
 *  * @author Lemdjo Marie
 */
public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public LoadCommandResponse() {

    }

    public LoadCommandResponse(String status, int numberOfNewStudents){
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
