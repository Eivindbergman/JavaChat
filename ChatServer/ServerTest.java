package ChatServer;

/**
 *
 *
 * @author beej15
 * Created on 4/11/18
 */
public class ServerTest {
    public static void main(String[] args) {
        Server server = new Server(1234);
        System.out.println("Starting chatserver...");
        server.startServer();
    }
}
