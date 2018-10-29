import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author inmikhaylov
 *
 */
public class MultiThreadServer {

    private static ExecutorService executeIt = Executors.newFixedThreadPool(10);
    private static List<Socket> clients = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadServer.class);

    public static void main(String[] args) {
        logger.info("Start server");
        try (ServerSocket server = new ServerSocket(3345);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            while (!server.isClosed()) {
                for(Socket s : clients){
                    if(s.isClosed()){
                        clients.remove(s);
                    }
                }

                if (br.ready()) {

                }

                Socket client = server.accept();
                clients.add(client);

                executeIt.execute(new MonoThreadClientHandler(client));

            }

            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}