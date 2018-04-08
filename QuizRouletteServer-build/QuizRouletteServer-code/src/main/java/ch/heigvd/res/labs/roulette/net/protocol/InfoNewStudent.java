/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 *
 * @author Oussama Lagha
 */
public class InfoNewStudent {

    private String status;
    private int numberOfNewStudent;

    public InfoNewStudent() {
    }

    public InfoNewStudent(String status, int numberOfStudents) {
        this.status = status;
        this.numberOfNewStudent = numberOfStudents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfStudents() {
        return numberOfNewStudent;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfNewStudent = numberOfStudents;
    }

}
