/**
 * This class is used to serialize/deserialize the response sent by the
 * server when processing the "LIST" command defined in the protocol
 * specification. The JsonObjectMapper utility class can use this class.
 *
 * @author Bryan Curchod, François Burgener
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

public class ListCommandReponse {
    
    private Student[] students;

    public ListCommandReponse() {
    }

    public ListCommandReponse(Student[] students) {
        this.students = students;
    }

    public void setStudents(Student[] students) {
        this.students = students;
    }

    public Student[] getStudents() {
        return students;
    }
}
