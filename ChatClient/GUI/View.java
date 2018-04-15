package ChatClient.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/15/18
 */
public class View extends JFrame{

    public  JTextField  textField;
    public  JTextArea   textArea;

    public View(String name) {
        super("JavaChat - " +name);
        textField = new JTextField();
        textField.setEditable(true);
        add(textField, BorderLayout.SOUTH);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        showMessage(name);
    }

    public void showMessage(String m) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        textArea.append(m);
                    }
                }
        );
    }

    public void addChatListener(ActionListener listener) {
        textField.addActionListener(listener);
    }

}
