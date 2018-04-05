package ch.heigvd.res.labs.roulette.net.client;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RouletteV2NyahonTest extends RouletteV2WasadigiTest {

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void loadedStudentsShouldBeInList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Yohann");
        client.loadStudent("Johanna");
        assertEquals("Yohann", client.listStudents().get(0).getFullname());
        assertEquals("Johanna", client.listStudents().get(1).getFullname());
    }

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void numberOfStudentInListMustBeCorrect() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Yohann");
        client.loadStudent("Johanna");
        assertEquals(2, client.listStudents().size());
    }

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void theServerShouldSendAnErrorResponseWhenListIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.listStudents();
    }

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void thereShouldBeNoMoreStudentsWhenDataCleared() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Yohann");
        client.loadStudent("Johanna");
        client.clearDataStore();
        exception.expect(EmptyStoreException.class);
        client.listStudents();
    }

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void theServerShouldHaveZeroStudentsInListAtStart() throws IOException, EmptyStoreException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        exception.expect(EmptyStoreException.class);
        client.listStudents();
    }

    @Test
    @TestAuthor(githubId = "Nyahon")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }


}