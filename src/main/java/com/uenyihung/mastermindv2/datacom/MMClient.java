package com.uenyihung.mastermindv2.datacom;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of communicating with the server.
 * There are different methods that perform writes to communicate with the
 * server and read the response from the server to execute different tasks 
 * required for the game.
 * 
 * @author  Naasir Jusab, Dimitri Spyropoulos
 * @version 2.0
 * @since 21/10/2016
 */
public class MMClient {
   
    private MMPacket packet;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    /**
     * Default constructor to instantiate an MMClient.
     */
    public MMClient(){
        super();       
    }
    
    /**
     * Calls MMPacket close method which will take care of closing the socket.
     * 
     * @throws IOException 
     */
    public void close() throws IOException
    {
        packet.close();
    }
    
    /**
     * This method takes care of creating the socket and buffers where the 
     * client and server will communicate.
     * 
     * @param ip address of the server
     * @param port using 50000
     */
    public void createConnection(String ip, int port){
        
        try{
            Socket socket = new Socket(ip,port);
            packet = new MMPacket(socket); 
        }
        catch(IOException ioe){
            log.error("Error connecting");
        }        
       
    }
    
   /**
    * This method takes care of starting the game with a randomly generated 
    * answer.
    * 
    * @return true if the game was successfully started
    * @throws IOException 
    */
    public boolean startGame() throws IOException
    {
       byte[] byteData = {0,0,0,0,0};
       packet.write(byteData);
       byte[] response = packet.readPacket();
       log.info("MMClient: startGame - Response: " + response + " my data: " + byteData);
       log.info("MMClient: startGame - Result: " + Arrays.equals(byteData, response));
       log.info("is socket closed?: "+ this.packet.isClosed());
       
       return Arrays.equals(byteData, response);     
    }
   
    /**
     * This method starts the game with a predetermined answer input by the
     * user.
     * 
     * @param answer predetermined answer
     * @return true if game was successfully started
     * @throws IOException 
     */
    public boolean startGame(byte[] answer) throws IOException
    {
       log.info("MMClient Answer: " + Arrays.toString(answer));
       packet.write(answer);
       byte[] response = packet.readPacket();
       log.info("MMClient Response: " + Arrays.toString(response));
       
       return Arrays.equals(new byte[]{0,0,0,0,0}, response);       
    }
   
    /**
     * Takes care of surrendering the game. It returns an array that holds the
     * answer.
     * 
     * @return byte[] that holds the answer
     * @throws IOException
     */
    public byte[] surrender() throws IOException
    {
        packet.write(new byte[] {2,0,0,0,0});
        return packet.readPacket();  
    }
   
    /**
     * Sends data to the server to tell it to terminate.
     * 
     * @param data holds byte[] to send to the server to indicate a termination
     * @throws IOException 
     */
    public void terminateGame(byte[] data) throws IOException
    {
        packet.write(data);    
    }

    /**
     * This method takes care of sending guesses to the server and the server 
     * returns a response. The server can return hints or end the game.
     * 
     * @param guess contains the user's guess
     * @return server response
     * @throws IOException 
     */
    public byte[] sendGuesses(byte[] guess) throws IOException
    {
        log.info("MMClient: " + Arrays.toString(guess));
        packet.write(guess);
        byte[] response = packet.readPacket();
        log.info("MMClient: " + Arrays.toString(response));
        return response;
    }
   
    /**
     * This method checks if the socket is closed
     * 
     * @return true if the socket is closed
     */
    public boolean isClosed() {
        log.info("MMClient : isClosed " + packet.isClosed());
        return packet.isClosed();

    }
   
}