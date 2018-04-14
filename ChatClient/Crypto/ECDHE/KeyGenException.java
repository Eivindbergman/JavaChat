package ChatClient.Crypto.ECDHE;

/**
 * Exception for everything related to the key generation.
 *
 * @author beej15
 * Created on 4/11/18
 */
public class KeyGenException extends Exception {
    public KeyGenException(String message){
        super(message);
    }
}
