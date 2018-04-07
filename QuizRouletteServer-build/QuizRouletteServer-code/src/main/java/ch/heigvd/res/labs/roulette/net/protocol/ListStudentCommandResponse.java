package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LIST" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * @author Guillaume Hochet
 */
public class ListStudentCommandResponse {

    List<Student> students;

    public ListStudentCommandResponse() {

        students    = new ArrayList<>();
    }

    public ListStudentCommandResponse(List<Student> list) {

        students    = new ArrayList<>(list);
    }

    public void setStudents(List<Student> list) {

        students = list;
    }

    public List<Student> getStudents() {

        return students;
    }
}
