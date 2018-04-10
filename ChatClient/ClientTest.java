package ChatClient;

/**
 * Created by beej15 on on 4/10/18
 */
public class ClientTest {
    public static void main(String[] args) {
        Client client = new Client("localhost", 65123);
        System.out.println("Starting Client...");
        client.startClient();
    }
}
