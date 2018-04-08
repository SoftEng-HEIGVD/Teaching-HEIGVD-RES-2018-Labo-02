package ch.heigvd.res.labs.roulette.data;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple implementation of the IStudentStore contract. The data is managed in
 * memory (no persistent storage).
 *
 * @author Olivier Liechti
 */
public class StudentsStoreImpl implements IStudentsStore {

    static final Logger LOG = Logger.getLogger(StudentsStoreImpl.class.getName());

    private final List<Student> students = new LinkedList<>();
    private int numberOfAddStudents;

    @Override
    public synchronized void clear() {
        students.clear();
    }

    @Override
    public synchronized void addStudent(Student student) {
        students.add(student);
    }

    @Override
    public synchronized List<Student> listStudents() {
        List<Student> result = new LinkedList<>(students);
        return result;
    }

    @Override
    public synchronized Student pickRandomStudent() throws EmptyStoreException {
        if (students.isEmpty()) {
            throw new EmptyStoreException();
        }
        int n = (int) (Math.random() * students.size());
        return students.get(n);
    }

    @Override
    public synchronized int getNumberOfStudents() {
        return students.size();
    }

    @Override
    public synchronized int getNumberOfAddStudents() {
        return numberOfAddStudents;
    }

    @Override
    public void importData(BufferedReader reader) throws IOException {
        List<Student> addStudents = new ArrayList<>();
        boolean finishAdd = false;
        String fileStudent;
        numberOfAddStudents = 0;
        while (!finishAdd && (fileStudent = reader.readLine()) != null) {
            if (fileStudent.equalsIgnoreCase(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER)) {
                finishAdd = true;
            } else {
                addStudents.add(new Student(fileStudent));
                ++numberOfAddStudents;
            }
        }
        synchronized (this) {
            students.addAll(addStudents);
        }
    }
}
