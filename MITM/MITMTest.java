package MITM;

import MITM.Server;

/**
 *
 *
 * @author beej15
 * Created on 4/11/18
 */
public class MITMTest {
    public static void main(String[] args) {
        int port = 1234;
        Server server = new Server(port);
        System.out.printf("Start listening on port %d for incoming packets...", port);
        server.startServer();
    }
}
