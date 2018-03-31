/**
 * This class is used to serialize/deserialize the response sent by the
 * server when processing the "LOAD" command defined in the protocol
 * specification. The JsonObjectMapper utility class can use this class.
 *
 * @author Bryan Curchod, François Burgener
 */
package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandReponse {

    private String status;
    private int numberOfNewStudents;

    public LoadCommandReponse() {
    }

    public LoadCommandReponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
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
