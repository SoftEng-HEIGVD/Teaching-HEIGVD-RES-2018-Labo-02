package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LIST" command defined in the protocol specification V2.
 * The JsonObjectMapper utility class can use this class.
 *
 * @author Lionel Nanchen
 */
public class ListCommandResponse {

    private List<Student> students;

    public ListCommandResponse(){
    }

    public ListCommandResponse(List<Student> students){
        this.students = students;
    }

    public List<Student> getStudents(){
        return students;
    }
}
