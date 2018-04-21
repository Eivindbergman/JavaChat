package ChatClient;

import javax.swing.*;

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
        String ip = JOptionPane.showInputDialog("IP address");
        int     port = 1234;

        Client client = new Client(ip, port);
    }
}
