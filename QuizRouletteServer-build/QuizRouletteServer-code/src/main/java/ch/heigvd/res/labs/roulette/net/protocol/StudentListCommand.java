package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

/**
 * This class is used for maping the generative List into a Student list, when parsing to json.
 *
 * @author Marc Labie
 */
public class StudentListCommand {
    private List<Student> students;



    public List<Student> getStudents(){
        return students;
    }

    public void setStudents(List<Student> list){
        students = list;
    }
}
