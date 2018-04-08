package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Marc Labie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadCommandResponse {

    private String error;

    private String status;

    private String numberOfNewStudents = "0";

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

    public String getNumberOfNewStudents() {
        return numberOfNewStudents;
    }


    public void setNumberOfNewStudents(int val){
        numberOfNewStudents = "" + val;
    }

}
