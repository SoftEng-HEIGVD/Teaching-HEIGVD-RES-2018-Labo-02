
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * @author Iando Rafidimalala
 */

public class LoadCommandResponse {
    private final String status;
    private final int numberOfNewStudents;

    public LoadCommandResponse(String status, int nbNewSt) {
        this.status = status;
        numberOfNewStudents = nbNewSt;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }    
}
