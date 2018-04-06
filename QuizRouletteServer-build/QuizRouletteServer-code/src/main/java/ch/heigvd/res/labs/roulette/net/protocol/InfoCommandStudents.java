package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

public class InfoCommandStudents {
    private List<Student> students;
    public InfoCommandStudents(List<Student> students){
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }
}
