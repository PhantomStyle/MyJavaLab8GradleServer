import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MonoThreadClientHandler implements Runnable {

    private static Socket clientDialog;
    private static List<Socket> clients = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(MonoThreadClientHandler.class);


    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
        clients.add(client);
    }

    @Override
    public void run() {

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientDialog.getOutputStream()));

            BufferedReader in = new BufferedReader(new InputStreamReader(clientDialog.getInputStream()));

            while (true) {
                String entry = in.readLine();

//                System.out.println(entry);
                logger.info("Received " + entry + " message");
                if (entry.startsWith("$$")) {
                    logger.info("It's changing color command");
                    for (Socket socket : clients) {
                        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.write(entry + "\n");
                        out.flush();
                    }
                }

//                if(entry.equals("/closeApp")){
//                    clientDialog.close();
//                    continue;
//                }


                for (Socket socket : clients) {
                    try {
                        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.write(entry + "\n");

                        out.flush();
                    } catch (Exception ex){
                        logger.info("One of clients is disconnected");
                        continue;
                    }
                }
                for (Socket socket : clients) {
                   if(socket.isClosed()) {
                       clients.remove(socket);
                   }
                }

                if(clientDialog.isClosed()){
                    break;
                }
            }

            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            in.close();
            out.close();

            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}