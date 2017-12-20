package com.uenyihung.mastermindv2.server;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Description: Create an instance of MMServerApp that will start and MMServer.
 * 
 * @author 1338559
 * @version 9/29/2016
 */
public class MMServerApp {
    public static void main(String[] args) throws IOException
    {
        // print server ip
        System.out.println("Server IP#: " + InetAddress.getLocalHost().getHostAddress());
        MMServer mms = new MMServer();
        mms.startServer();
    }
}