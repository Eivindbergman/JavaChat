package ChatClient;

import ChatClient.Crypto.AES.MasterCipher;
import ChatClient.Crypto.AES.MasterSecret;
import ChatClient.GUI.View;
import Crypto.SecretKey;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyException;

/**
 * Main controller for Instant Messaging client.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class Client {
    private     String              ip;
    private     int                 port;
    private     ClientModel         model;
    private     View                view;

    /**
     * Creates the client object which is used to communicate with the server through the model.
     * @param ip IP-address of the server
     * @param port used network port on the server
     */
    public Client(String ip, int port) {
        this.model = new ClientModel(ip, port);
        this.view = new View(model.getClientName());
        ChatHandler chatHandler = new ChatHandler(view, model.getMasterCipher());
        view.addChatListener(new ChatListener());
        if (model.isKeyEstablished()) {
            System.out.println("Starting chatHandler");
            chatHandler.setInputStream(model.getIn());
            chatHandler.startThread();
        } else {
            for (int i = 0; i < 10; i++) {
                if (model.isKeyEstablished()) {
                    chatHandler.setInputStream(model.getIn());
                    chatHandler.startThread();
                    break;
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(String m) {
        try {
            model.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ChatListener implements ActionListener {
        String message;
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(view.textField)) {
                message = view.textField.getText();
                view.showMessage(String.format("\n[%s]: %s", model.getClientName(), message));
                sendMessage(message);
                view.textField.setText("");
            }
        }
    }

}
