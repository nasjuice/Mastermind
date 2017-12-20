package com.uenyihung.mastermindv2.beans;

import com.uenyihung.mastermindv2.datacom.MMClient;

/**
 * This class takes care of setting information received from the 
 * Connection.fxml.
 * So that the data can be transferred to the FXMLController.
 * 
 * @author  Naasir Jusab, Dimitri Spyropoulos
 * @version 2.0
 * @since 21/10/2016
 */
public class ConfigBean {
    
    private String ip;
    private int port;
    private MMClient client;

    /**
     * 
     * @return client from the ConnectionController
     */
    public MMClient getClient() {
        return client;
    }

    /**
     * 
     * @param client sets client from the ConnectionController
     */
    public void setClient(MMClient client) {
        this.client = client;
    }
    
    /**
     * 
     * @return ip received from the user
     */
    public String getIp() {
        return ip;
    }
    
    /**
     * 
     * @param ip sets the ip received from the user
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    /**
     * 
     * @return port received from the user 
     */
    public int getPort() {
        return port;
    }
    
    /**
     * 
     * @param port sets port received from the user
     */
    public void setPort(int port) {
        this.port = port;
    }
    
}