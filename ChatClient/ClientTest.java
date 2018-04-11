package ChatClient;

/**
 *
 *
 * @author beej15
 * Created on 4/11/18
 */
public class ClientTest {
    public static void main(String[] args) {
        boolean connected = false;
        int i = 0;
        String ip = "localhost";
        Client client = new Client(ip, 1234);
        while (!connected && i <= 4) {
            try {
                client.startClient();
                connected = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("Can't reach server. Trying %d more times\n", (4 - i));
                i++;
            }
            try {
                Thread.sleep(15000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
