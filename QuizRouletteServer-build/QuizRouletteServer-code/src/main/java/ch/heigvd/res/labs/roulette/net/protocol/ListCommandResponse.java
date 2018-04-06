package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LIST" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Olivier Kopp
 */

public class ListCommandResponse {
    List<Student> students;

    public ListCommandResponse(List<Student> s) {
        students = new ArrayList<>(s);
    }

    public ListCommandResponse() {
    }

    public List<Student> getStudents() {
        return students;
    }
}
