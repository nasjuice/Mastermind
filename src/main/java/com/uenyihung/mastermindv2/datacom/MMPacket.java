package com.uenyihung.mastermindv2.datacom;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Description: Creates an instance of an MMPacket for DC.
 *
 * @author Uen Yi Cindy Hung 1031002
 * @version 1.0
 * @since 21/10/2016
 */
public class MMPacket {
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private final int BUFF_SIZE = 1;
    private byte[] buffer = new byte[BUFF_SIZE];
    private byte[] msg = new byte[5];
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    /**
     * 1 parameter constructor that sets the socket.
     *
     * @param clientSocket the socket needed to instantiate the class.
     */
    public MMPacket(Socket socket) throws IOException {
        this.socket = socket;
        out = socket.getOutputStream();
        in = socket.getInputStream();
    }
    /**
     * Reads the message in the packet.
     *
     * @return the message as a byte[].
     * @throws IOException
     */
    public byte[] readPacket() throws IOException {
        log.info("MMPacket Server Side: readPacket");
        int totalBytesRcvd = 0;
        int bytesRcvd = 0;
        int ctr = 0;
        while (totalBytesRcvd < msg.length) {
            
            if ((bytesRcvd = in.read(buffer)) != -1) {
                log.info("MMPacket Server Side: readPacket - bytesRcvd: " + bytesRcvd);
                msg[ctr] = buffer[0];
                ctr++;
                totalBytesRcvd += bytesRcvd;
            }
        }
        log.info("MMPacket Server Side: readPacket - DATA: " + Arrays.toString(msg));
        return msg;
    }
    /**
     * Write the data out to the socket.
     *
     * @param data the byte[] to write.
     * @throws IOException
     */
    public void write(byte[] data) throws IOException {
        log.info("MMPacket Server Side: write");
        out.write(data);
    }
    /**
     * Return the state of if the socket is closed.
     *
     * @return
     */
    public boolean isClosed() {
        log.info("MMPacket Server Side: isClosed " + socket.isClosed());
        return socket.isClosed();
    }
    /**
     * Closes the socket connection
     *
     * @throws IOException
     */
    public void close() throws IOException {
        this.socket.close();
    }
    
    /**
     * This method returns the client's IP# used to be displayed on server side. 
     * @return String holding client's IP#
     */
    public String getClientIP(){
        return this.socket.getInetAddress().getHostAddress();
    }
}