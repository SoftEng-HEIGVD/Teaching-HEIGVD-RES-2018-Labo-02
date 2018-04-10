package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;

import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 * 
 * @author kaffo Doriane
 */
public class RouletteV2ClientHandler implements IClientHandler {

    private IStudentsStore store;

    public RouletteV2ClientHandler(IStudentsStore store) {

        this.store = store;

    }

    @Override
    public void handleClientConnection(InputStream is, OutputStream os) throws IOException {

        int nbrOfCommands = 0;
        String command;
        boolean over = false;

        BufferedReader bReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

        pWriter.println("Online HELP is available! Up tp you to find it");
        pWriter.flush();

        while (!over && ((command = bReader.readLine()) != null)) {
            ++nbrOfCommands;

            switch (command.toUpperCase()) {

                case RouletteV2Protocol.CMD_HELP:
                    pWriter.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
                    break;

                case RouletteV2Protocol.CMD_RANDOM:
                    RandomCommandResponse rcResponse = new RandomCommandResponse();
                    try {
                        rcResponse.setFullname(store.pickRandomStudent().getFullname());
                    } catch (EmptyStoreException ex) {
                        rcResponse.setError("There is no student! No random one can picked");
                    }
                    pWriter.println(JsonObjectMapper.toJson(rcResponse));
                    pWriter.flush();
                    break;

                case RouletteV2Protocol.CMD_LOAD:
                    pWriter.println(RouletteV2Protocol.RESPONSE_LOAD_START);
                    pWriter.flush();
                    int n0 = store.getNumberOfStudents();
                    store.importData(bReader);
                    int n1 = store.getNumberOfStudents();
                    pWriter.println(JsonObjectMapper.toJson(new LoadCommandResponse("success", n1 - n0)));
                    pWriter.flush();
                    break;

                case RouletteV2Protocol.CMD_INFO:
                    InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
                    pWriter.println(JsonObjectMapper.toJson(response));
                    pWriter.flush();
                    break;

                case RouletteV2Protocol.CMD_LIST:
                    StudentsList students = new StudentsList();
                    students.setStudents(store.listStudents());
                    pWriter.println(JsonObjectMapper.toJson(students));
                    pWriter.flush();
                    break;

                case RouletteV2Protocol.CMD_CLEAR:
                    store.clear();
                    pWriter.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
                    pWriter.flush();
                    break;

                case RouletteV2Protocol.CMD_BYE:
                    pWriter.println(JsonObjectMapper.toJson(new ByeCommandResponse("success", nbrOfCommands)));
                    pWriter.flush();
                    over = true;
                    break;

                default:
                    pWriter.println("Please use HELP to find out about the available commands.");
                    pWriter.flush();
                    break;
            }
            pWriter.flush();
        }
    }

}