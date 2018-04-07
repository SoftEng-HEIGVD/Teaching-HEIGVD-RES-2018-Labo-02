package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
    Test class for the protovol V2
    Authors :   Adrien Allemand, Loyse Krug
 */
public class RouletteV2LoyseKrugTest {

    @Rule
    public EphemeralClientServerPair ephemeralClientServerPair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    //test CLEAR
    @Test
    @TestAuthor(githubId = {"LoyseKrug", "AdrienAllemand"})
    public void ClearCommandEmptiesStudentList() throws IOException {

        IRouletteV2Client client = (IRouletteV2Client)ephemeralClientServerPair.getClient();

        client.loadStudent("Loyse Krug");

        //now clear students
        client.clearDataStore();

        //check that studentlist is empty
        assertEquals(client.getNumberOfStudents(),0);
    }

    //test Liste
    @Test
    @TestAuthor(githubId = {"LoyseKrug", "AdrienAllemand"})
    public void ListStudentsReturnsAllStudentsPreviouslyAdded() throws IOException {

        IRouletteV2Client client = (IRouletteV2Client)ephemeralClientServerPair.getClient();

        // a few students
        LinkedList<Student> studentList = new LinkedList();
        studentList.add(new Student("Adrien Allemand"));
        studentList.add(new Student("Loyse Krug"));
        studentList.add(new Student("Olivier Liechti"));

        // add the students to the server list
        client.loadStudents(studentList);

        // fetch the list of students that are loaded on the server
        List<Student> studentLoadedList = client.listStudents();

        // check that all students added to the server are in the list we just got
        for (Student s : studentList) {
            assertTrue(studentLoadedList.contains(s));
        }
    }

    //test Load response
    @Test
    @TestAuthor(githubId = {"LoyseKrug", "AdrienAllemand"})
    public void ClientLoadGetsCorrectAnswerFromServer() throws IOException {

        // we make custom client to test the server answer for Load Command
        Socket client = new Socket("localhost",ephemeralClientServerPair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        in.readLine();

        // manually insert a new student
        out.println(RouletteV2Protocol.CMD_LOAD);
        out.flush();
        in.readLine();
        out.println("Adrien Allemand");
        out.flush();
        out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();

        // read the server answer and check it
        String s = in.readLine();
        assertEquals("{\"status\":\"success\",\"numberOfNewStudents\":1}", s);
    }


    //test BYE response
    @Test
    @TestAuthor(githubId = {"LoyseKrug", "AdrienAllemand"})
    public void ClientByeGetsCorrectAnswerFromServer() throws IOException {

        // we make custom client to test the server answer for bye command
        Socket client = new Socket("localhost",ephemeralClientServerPair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        in.readLine();

        // manually say bye
        out.println(RouletteV2Protocol.CMD_BYE);
        out.flush();

        // read the server answer and check it
        String s = in.readLine();
        assertEquals(s, "{\"status\":\"success\",\"numberOfCommands\":1}");
    }

    //test INFO response
    @Test
    @TestAuthor(githubId = {"LoyseKrug", "AdrienAllemand"})
    public void ClientInfoGetsCorrectAnswerFromServer() throws IOException {

        //first clear server as we don't want any students in it
        IRouletteV2Client client2 = (IRouletteV2Client)ephemeralClientServerPair.getClient();
        client2.clearDataStore();

        // next we make custom client to test the server answer for INFO command
        Socket client = new Socket("localhost",ephemeralClientServerPair.getServer().getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        in.readLine();

        // manually say bye
        out.println(RouletteV2Protocol.CMD_INFO);
        out.flush();

        // read the server answer and check it
        String s = in.readLine();
        assertEquals(s, "{\"protocolVersion\":\"2.0\",\"numberOfStudents\":0}");
    }
}
