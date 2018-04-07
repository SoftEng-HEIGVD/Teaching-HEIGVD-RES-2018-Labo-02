package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.ArrayList;
import java.util.List;

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
