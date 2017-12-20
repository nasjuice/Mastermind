package com.uenyihung.mastermindv2.client;

import com.uenyihung.mastermindv2.beans.ConfigBean;
import com.uenyihung.mastermindv2.datacom.MMClient;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

/**
 *
 * This class takes care of handling events in the connection controller.
 *
 * @author Naasir Jusab, Dimitri Spyropoulos
 * @version 2.0
 * @since 21/10/2016
 *
 */
public class ConnectionController implements Initializable {

    @FXML
    private TextField ipInput;
    @FXML
    private TextField portInput;
    @FXML
    private TextField answerInput;
    @FXML
    private Button btnSubmit;
    @FXML
    private Label errorLabel;

    private ConfigBean cb;

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void submit(ActionEvent event) {

        int port = 50000;
        String ip = ipInput.getText();
        errorLabel.setText("");

        try {

            String validIp = "";

            if (IPAddressUtil.isIPv4LiteralAddress(ip)
                    || IPAddressUtil.isIPv6LiteralAddress(ip)) {
                validIp = ip;
            } else {
                throw new IllegalArgumentException("Invalid ip");
            }

            //reads userAnswer
            String answer = answerInput.getText();

            //sets data to the bean
            cb.setIp(validIp);
            cb.setPort(port);

            MMClient client = new MMClient();
            //creates session
            client.createConnection(validIp, port);

            //if no answer then uses a generatedAnswer
            if (answer.length() == 0 && !client.isClosed()) {

                client.startGame();

            } // uses the user's answer if it is valid
            else {

                if (answer.length() != 4) {
                    throw new IllegalArgumentException("Invalid length");
                }

                byte[] determinedAnswer = new byte[5];
                determinedAnswer[0] = 0;

                for (int i = 1; i < 5; i++) {
                    if (!Character.isDigit(answer.charAt(i - 1))) {
                        throw new IllegalArgumentException("Invalid arguments");
                    } else {
                        Character c = answer.charAt(i - 1);
                        String num = Character.toString(c);
                        int digit = Integer.parseInt(num);
                        if (digit > 0 && digit < 9) {
                            determinedAnswer[i] = Byte.parseByte(num);
                        } else {
                            throw new IllegalArgumentException("Invalid arguments, only digits in the range of 1-8 are acceptable.");
                        }

                    }

                }

                client.startGame(determinedAnswer);

            }
            cb.setClient(client);

            Stage s = (Stage) btnSubmit.getScene().getWindow();
            s.close();

        } catch (IllegalArgumentException iae) {
            errorLabel.setText("Invalid arguments");
            ipInput.setText("");
            answerInput.setText("");

        } catch (Exception e) {
            Stage s = (Stage) btnSubmit.getScene().getWindow();
            s.close();
        }

    }

    /**
     * Sets the configBean to share data across two windows
     *
     * @param cb
     */
    public void setConfigBean(ConfigBean cb) {
        this.cb = cb;
    }

}