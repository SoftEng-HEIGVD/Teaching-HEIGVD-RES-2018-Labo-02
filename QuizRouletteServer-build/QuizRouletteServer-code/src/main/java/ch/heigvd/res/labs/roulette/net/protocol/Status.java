/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Enumerate the status of command 
 * @author fidimala
 */
public enum Status {
    Success("success"),
    Error("error");

    private final String status;
    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
