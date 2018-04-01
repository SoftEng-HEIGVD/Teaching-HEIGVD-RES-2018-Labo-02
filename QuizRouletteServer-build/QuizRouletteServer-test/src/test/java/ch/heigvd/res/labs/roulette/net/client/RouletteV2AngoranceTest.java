    package ch.heigvd.res.labs.roulette.net.client;

    import ch.heigvd.res.labs.roulette.data.Student;
    import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
    import ch.heigvd.schoolpulse.TestAuthor;
    import org.junit.Rule;
    import org.junit.Test;
    import org.junit.rules.ExpectedException;

    import java.io.IOException;
    import java.util.*;

    import static org.junit.Assert.assertEquals;
    import static org.junit.Assert.assertTrue;

    /**
    * This class contains automated tests to validate the client and the server
    * implementation of the Roulette Protocol (version 2)
    *
    * @author Olivier Liechti
    */
    public class RouletteV2AngoranceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldClearStudents() throws IOException {

        // Create the client from the informations of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);

        // Add students on the server
        List<Student> serverStudent = new ArrayList<>();

        serverStudent.add(new Student("Sacha"));
        serverStudent.add(new Student("Olivier"));
        serverStudent.add(new Student("Fabienne"));

        for(Student s : serverStudent) {
            client.loadStudent(s.getFullname());
        }

        // Clear the list of students from the client
        assertEquals(serverStudent.size(), client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"Angorance", "LNAline"})
    public void theServerShouldListStudents() throws IOException {

        // Create the client from the informations of the server
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);

        // Add students on the server
        List<Student> serverStudent = new ArrayList<>();

        serverStudent.add(new Student("Sacha"));
        serverStudent.add(new Student("Olivier"));
        serverStudent.add(new Student("Fabienne"));

        for(Student s : serverStudent) {
            client.loadStudent(s.getFullname());
        }

        // Get the list of students from the client
        List<Student> clientStudents = client.listStudents();

        // Check if the list of student is the same on client and server
        assertEquals(serverStudent.size(), client.getNumberOfStudents());

        for(Student s : serverStudent){
            assertTrue(clientStudents.contains(s));
        }
    }
}
