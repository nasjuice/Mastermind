package com.uenyihung.mastermindv2.server;
import com.uenyihung.mastermindv2.datacom.MMPacket;
import java.net.*;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author 1338559 
 * @version 2.0
 * @since 21/10/2016
 */
public class MMServer {
    private MMPacket clientSocket;
    private ServerSocket serverSocket;
    private MMServerSession session;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final int port = 50000;
    /**
     * Default constructor that set the port.
     *
     * @throws IOException
     */
    public MMServer() throws IOException { 
        super();
        serverSocket = new ServerSocket(port);
    }
    /**
     * Starts the server.
     *
     * @throws IOException
     */
    public void startServer() {
        log.info("MMServer: startServer");
        //Infinite for loop
        for (;;) {
            try {
                log.info("MMServer: looping");
                clientSocket = new MMPacket(serverSocket.accept());
                session = new MMServerSession(clientSocket);
                log.info("Connected to client IP#: " + clientSocket.getClientIP());
                session.startSession();
                
                clientSocket.close();
            } catch (IOException ioe) {
                log.info("MMServer: statServer - startServer " + ioe.getMessage());
            }
            if(serverSocket.isClosed()){
                System.exit(0);
            }
        }
    }
}