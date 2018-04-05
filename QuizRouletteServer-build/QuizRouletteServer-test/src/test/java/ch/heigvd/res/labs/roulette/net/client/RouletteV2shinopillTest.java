
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RouletteV2shinopillTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "shinopill")
    public void theServerShoudReturnTheGoodProtocol() throws IOException{
        assertEquals(roulettePair.client.getProtocolVersion(),"2.0");
    }

    @Test
    @TestAuthor(githubId = "shinopill")
    public void theServerShouldBeEmptyAfterClear() throws IOException{
        roulettePair.client.loadStudent("Florent");
        roulettePair.client.loadStudent("Olivier");
        assertEquals(roulettePair.client.getNumberOfStudents(),2);
        ((IRouletteV2Client)roulettePair.client).clearDataStore();
        assertEquals(roulettePair.client.getNumberOfStudents(),0);

    }

    @Test
    @TestAuthor(githubId = "shinopill")
    public void theServerShoudReturnTheSameListOfStudent() throws IOException{
        Student a = new Student("a");
        Student b = new Student("b");
        Student c = new Student("c");
        List<Student> studentList = new ArrayList<>();
        studentList.add(a);
        studentList.add(b);
        studentList.add(c);

        roulettePair.client.loadStudents(studentList);

        List<Student> severList =  ((IRouletteV2Client)roulettePair.client).listStudents();

        for(int i = 0 ; i < studentList.size(); i++){
            assertEquals(severList.get(i),studentList.get(i));
        }

    }
}
