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

public class RouletteV2SysmohTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void serverShouldReturnCorrectProtocolVersion() throws IOException {

        assertEquals(RouletteV2Protocol.VERSION, getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void clientShouldBeAbleToConnectToRouletteServerV2() throws IOException {

        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());

        client.connect("localhost", roulettePair.getServer().getPort());
        assertTrue(client.isConnected());

        client.disconnect();
        assertFalse(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void clientShouldBeAbleToClearStudentsList() throws IOException {

        IRouletteV2Client client    = getClient();
        client.connect("localhost", roulettePair.getServer().getPort());

        client.loadStudent("Sisi la famille");
        client.loadStudent("Himotep Asterix");

        assertEquals(2, client.getNumberOfStudents());

        client.clearDataStore();

        assertEquals(0, client.getNumberOfStudents());
        assertTrue(client.listStudents().isEmpty());
    }

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void clientShouldBeAbleToListPerformedCommands() throws IOException {

        IRouletteV2Client client = getClient();
        client.connect("localhost", roulettePair.getServer().getPort());

        assertEquals(client.getNumberOfCommands(), 0);

        client.loadStudent("wallah");
        client.loadStudent("habdoulilah");
        client.getProtocolVersion();
        client.getNumberOfStudents();
        client.disconnect();

        assertEquals(client.getNumberOfCommands(), 5);
    }

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void clientShouldBeAbleToListStudents() throws IOException {

        IRouletteV2Client client    = getClient();
        client.connect("localhost", roulettePair.getServer().getPort());

        ArrayList<String> students = new ArrayList<>();
        students.add("Jean Dujardin");
        students.add("Mark Ronson");
        students.add("Sacha Grey");

        for(String student : students)
            client.loadStudent(student);

        List<Student> studentList = client.listStudents();

        for(Student student : studentList)
            assertTrue(students.contains(student.getFullname()));
    }

    @Test
    @TestAuthor(githubId = "sysmoh")
    public void clientShouldBeAbleToSayNumberOfStudentsAdded() throws IOException {

        IRouletteV2Client client    = getClient();
        client.connect("localhost", roulettePair.getServer().getPort());

        String students [] = {
                "a",
                "b",
                "c"
        };

        for(String student : students)
            client.loadStudent(student);

        assertEquals(client.getNumberOfStudents(), 3);
    }


    private IRouletteV2Client getClient() {

        return (IRouletteV2Client) roulettePair.getClient();
    }
}
