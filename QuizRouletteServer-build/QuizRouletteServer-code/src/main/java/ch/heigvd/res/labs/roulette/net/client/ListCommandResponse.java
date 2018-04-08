package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author dorianekaffo
 */
class ListCommandResponse {

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
