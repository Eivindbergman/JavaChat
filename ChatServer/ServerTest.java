package ChatServer;

/**
 * Created by beej15 on on 3/28/18
 */
public class ServerTest {
    public static void main(String[] args) {
        Server server = new Server(65123);
        System.out.println("Starting chatserver...");
        server.startServer();
    }
}
