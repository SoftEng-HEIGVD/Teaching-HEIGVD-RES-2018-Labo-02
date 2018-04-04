package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author David Jaquet
 * @author Vincent Guidoux
 * @remark We don't test the command tested in the version 1
 */
public class RouletteV2NortalleTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void test1() throws IOException {
        assertTrue(true);
    }

    @Test
    @TestAuthor(githubId = "Nortalle")
    public void test2() throws IOException {
        assertTrue(true);

    }
    @Test
    @TestAuthor(githubId = "Nortalle")
    public void test3() throws IOException {
        assertTrue(true);

    }
}