package ChatClient.Crypto.AES;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/16/18
 */
public class BadCipherParametersException extends Exception {
    public BadCipherParametersException(String message) {
        super(message);
    }
}
