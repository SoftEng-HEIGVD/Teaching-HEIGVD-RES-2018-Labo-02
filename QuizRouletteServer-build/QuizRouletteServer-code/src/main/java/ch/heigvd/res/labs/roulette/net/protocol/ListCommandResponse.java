package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LIST" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Mathieu Jee
 * @author Lionel Burgbacher
 *
 */
public class ListCommandResponse {
    private List<Student> students;

    public ListCommandResponse() {
        students = new ArrayList<>();
    }

    public ListCommandResponse(List<Student> students) {
        this.students = new ArrayList<>(students);
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

}
