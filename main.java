import ChatClient.Client;
import ChatServer.Server;

/**
 * Description...
 *
 * @author beej15
 * Created on 4/23/18
 */
public class main {
    public static void main(String args[]) {
        if ((args[0].toLowerCase().equals("--server")
                || args[0].toLowerCase().equals("-s"))
                && !args[1].isEmpty()) {

            Server server = new Server(Integer.parseInt(args[1]));
            System.out.printf("\nStarting Chat Server on port %s\n", args[1]);
            server.startServer();
        } else if ((args[0].toLowerCase().equals("--client")
                || args[0].toLowerCase().equals("-c"))
                    && !args[1].isEmpty()
                    && !args[2].isEmpty()) {

            System.out.printf("\nStarting Chat Client for %s:%s\n",args[1], args[2]);
            Client client = new Client(args[1], Integer.parseInt(args[2]));
        } else if ((args[0].toLowerCase().equals("--mitm")
                    || args[0].toLowerCase().equals("-m"))
                    && !args[1].isEmpty()) {

            MITM.Server server = new MITM.Server(Integer.parseInt(args[1]));
            System.out.printf("\nStarting mitm listener on port %s\n", args[1]);
            server.startServer();
        } else {
            usage();
        }
    }

    public static void usage() {
        System.out.println("--------JavaChat--------");
        System.out.println("Usage : java -jar JavaChat.jar {--server | -s} [port]" +
                            "                              {--client | -c} [ip] [port]" +
                            "                              {--mitm   | -m} [port]");
    }
}
