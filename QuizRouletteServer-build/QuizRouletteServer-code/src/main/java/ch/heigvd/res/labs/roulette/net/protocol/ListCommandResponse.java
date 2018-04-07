package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

public class ListCommandResponse {

    private List<Student> students;

    public ListCommandResponse(){}

    public ListCommandResponse(List<Student> students){
        this.students =students;
    }

    public List<Student> getStudents(){
        return students;
    }

    public void setStudents(List<Student> students){
        this.students =students;
    }
}
