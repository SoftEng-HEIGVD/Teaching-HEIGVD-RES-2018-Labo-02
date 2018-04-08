package ch.heigvd.res.labs.roulette.net.protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import java.util.List;

/**
 * @author Guillaume Blanco, Patrick Neto
 */
public class ListCommandResponse {

    private List<Student> students;

    public ListCommandResponse() {}

    public ListCommandResponse(List<Student> students){
        this.students = students;
    }

    public List<Student> getStudents(){
        return students;
    }
}