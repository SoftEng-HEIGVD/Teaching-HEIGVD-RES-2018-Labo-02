package ch.heigvd.res.labs.roulette.net.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 *
 * @author Marc Labie
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadCommandResponse {

    private String error;

    private String status;

    private int numberOfNewStudents = 0;

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

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }


    public void setNumberOfNewStudents(int val){
        numberOfNewStudents = val;
    }

}
