/**
 * This class is used to serialize/deserialize the response sent by the
 * server when processing the "LOAD" command defined in the protocol
 * specification. The JsonObjectMapper utility class can use this class.
 *
 * @author Bryan Curchod, François Burgener
 */
package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author François Burgener, Bryan Curchod
 */
public class LoadCommandReponse {

    private String status;
    private int numberOfNewStudents;

    /**
     * Default constructor, set the attribute to the default value
     */
    public LoadCommandReponse() {
    }

    /**
     * Construct and set the status and the numberOfNewStudents
     * @param status status of the server loading process
     * @param numberOfNewStudents number of student added
     */
    public LoadCommandReponse(String status, int numberOfNewStudents) {
        this.status = status;
        this.numberOfNewStudents = numberOfNewStudents;
    }

    /**
     * Get the status. The status represents the state of servers loading attempt
     * @return status of the servers loading process
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status
     * @param status new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the number of student added by the "LOAD" command.
     * @return number of student added by the "LOAD" command.
     */
    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    /**
     * set the number of student added by the "LOAD" command.
     * @param numberOfNewStudents number of student added by the "LOAD" command.
     */
    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

}
