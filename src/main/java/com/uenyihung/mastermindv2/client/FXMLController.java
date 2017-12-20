package com.uenyihung.mastermindv2.client;

import com.uenyihung.mastermindv2.beans.ConfigBean;
import com.uenyihung.mastermindv2.datacom.MMClient;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of handling events in the main controller.
 * 
 * @author  Naasir Jusab, Dimitri Spyropoulos
 * @version 2.0
 * @since 21/10/2016
 */

public class FXMLController implements Initializable {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Paint greenCircle = Color.rgb(31, 255, 112, 1);
    private Paint blueCircle = Color.rgb(23, 106, 185, 1);
    private Paint brownCircle = Color.rgb(143, 37, 37, 1);
    private Paint orangeCircle = Color.rgb(255, 133, 31, 1);
    private Paint pinkCircle = Color.rgb(255, 31, 219, 1);
    private Paint redCircle = Color.rgb(255, 31, 31, 1);
    private Paint yellowCircle = Color.rgb(246, 255, 0, 1);
    private Paint lightBlueCircle = Color.rgb(31, 246, 255, 1);

    private Paint clientColorChoice;
    private Paint[] guessCircles;
   
    private int line = 10;
    private ConfigBean cb;

    @FXML
    private GridPane gameGrid;
    @FXML
    private Circle solutionCircle1;
    @FXML
    private Circle solutionCircle2;
    @FXML
    private Circle solutionCircle3;
    @FXML
    private Circle solutionCircle4; 
    @FXML
    private Label label;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cb = new ConfigBean();
        //first color is defaulted
        clientColorChoice = Color.rgb(31, 255, 112, 1);
        guessCircles = new Paint[4];
    }
    
     /**
     * Loads connection scene
     * @param event 
     */
    @FXML
    private void connect(ActionEvent event) {
        try {

            
            URL url = getClass().getResource("/fxml/connection.fxml");

            FXMLLoader fxmlloader = new FXMLLoader();
            fxmlloader.setLocation(url);
            fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());

            Stage stage = new Stage();

            Scene scene = new Scene(fxmlloader.load());
           
            
            //sets the configBean to share Data
            ((ConnectionController) fxmlloader.
                    getController()).setConfigBean(cb);
            
            scene.getStylesheets().add("/styles/connectioncontroller.css");

            stage.setTitle("Connection");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
           
        } catch (IOException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
    
     /**
       * Loads about scene
       * @param event 
       */
      @FXML 
      private void aboutClick(ActionEvent event) { 
          try {
            URL url = getClass().getResource("/fxml/About.fxml");
     
            FXMLLoader fxmlloader = new FXMLLoader(); 
            fxmlloader.setLocation(url);
     
            Stage stage = new Stage();
     
            Scene scene = new Scene(fxmlloader.load());
            
     
            stage.setTitle("About"); 
            stage.setScene(scene);
            stage.setResizable(false); 
            stage.show(); 
          } 
          catch (IOException ex) {    
            log.error("Error: " + ex.getMessage());
          } 
      } 
      
     
      /**
       * Loads instructions scene
       * @param event 
       */
      @FXML 
      private void getInstructions(ActionEvent event) { 
          try {  
            URL url = getClass().getResource("/fxml/Instructions.fxml");
     
            FXMLLoader fxmlloader = new FXMLLoader(); 
            fxmlloader.setLocation(url);
     
            Stage stage = new Stage();
     
            Scene scene = new Scene(fxmlloader.load());
           
     
            stage.setTitle("Help"); 
            stage.setScene(scene);
            stage.setResizable(false); 
            stage.show(); 
            
          } 
          catch (IOException ex) { 
            log.error("Error: " + ex.getMessage());
          } 
    }
      
     /**
     * Disconnects the socket and exits the game
     * @param event 
     */
    @FXML
    private void disconnect(ActionEvent event)
    {
        try
        {
            if(cb.getClient()!= null)
            {
                cb.getClient().terminateGame(new byte[] {-1,-1,-1,-1,-1});
                log.info("Connection has been aborted: " + cb.getClient().isClosed());
                cb.getClient().close();
                log.info("Connection has been aborted: " + cb.getClient().isClosed());
                Stage s = (Stage)gameGrid.getScene().getWindow();
                s.close();
            }         
            else
            {
                Stage s = (Stage)gameGrid.getScene().getWindow();
                s.close();
            }
        
        }
        catch(IOException ioe)
        {
            log.error(ioe.getMessage());
            log.error("Fatal error... Exiting System");
            System.exit(1);          
        }
        
    }     
      
     /**
     * Starts a new game
     * @param event
     */
    @FXML
    private void newGame(ActionEvent event) throws IOException,Exception {
      
        //reset row counter
        line = 10;
        //check if a session exists
        if(cb.getClient() != null)
        {
            //If socket is not closed, start the game
            if (!cb.getClient().isClosed() ) {
                if(cb.getClient().startGame())
                {
                    hideCirclesAndEllipses();
                    label.setText("");
                    gameGrid.setMouseTransparent(false);
                }        
            }
        }
    }
    
    /**
     * Starts a game with an answer received by the user
     * @param event
     * @throws IOException 
     */
    @FXML
    private void startGameWithAnswer(ActionEvent event) throws IOException {
        try{
            String inputString
                    = JOptionPane.showInputDialog("Enter 4 digits from 1-8, where 1 represents green, as the answer.");

            if (inputString.length() != 4) {
                throw new IllegalArgumentException("Invalid length");
            }

            byte[] determinedAnswer = new byte[5];
            determinedAnswer[0] = 0;

            for (int i = 1; i < 5; i++) {
                if (!Character.isDigit(inputString.charAt(i - 1))) {
                    throw new IllegalArgumentException("Invalid arguments");
                } else {
                    Character c = inputString.charAt(i - 1);
                    String num = Character.toString(c);
                    int digit = Integer.parseInt(num);
                    if (digit > 0 && digit < 9) {
                        determinedAnswer[i] = Byte.parseByte(num);
                    } else {
                        throw new IllegalArgumentException("Invalid arguments, only digits in the range of 1-8 are acceptable.");
                    }

                }

            }

            //here the determinedAnswer data has been parsed and now the game will start
            line = 10;
            if(cb.getClient() != null)
            {
                if (!cb.getClient().isClosed()) {
                    if (cb.getClient().startGame(determinedAnswer)) {
                        hideCirclesAndEllipses();
                        label.setText("");
                        gameGrid.setMouseTransparent(false);
                    }
                }
            }

        }
        catch(IllegalArgumentException iae){
            
            if(cb.getClient() == null)
                log.error("Client not connected");
            else
                log.error("Invalid data");
        }
                
    }                      

    /**
     * If the user gives up, end the game
     *
     * @param event
     */
    @FXML
    private void giveUp(ActionEvent event) throws IOException,Exception {

        if(cb.getClient() != null)
        {
            if (!cb.getClient().isClosed()) {

                byte[] response = cb.getClient().surrender();
                //response holds the answer
                displayEndGame(response, new byte[]{-1,-1,-1,-1,-1});
            
            }
        }
    }
    
    /**
     * Gets the color from the colored circles that a user clicked
     *
     * @param event click event
     * @throws Exception
     */
    @FXML
    private void determineColor(MouseEvent event) throws Exception {
        Object src = event.getSource();
        if (src instanceof Circle) {
            Circle circle = (Circle) src;
            clientColorChoice = circle.getFill();
            log.info("Color selected: " + clientColorChoice);        

        }

    }

    /**
     * Determines the circle that was selected, if it is valid then it sets its
     * color
     *
     * @param e
     */
    @FXML
    private void gridClick(MouseEvent clickEvent) {
        
        if(cb.getClient() != null)
        {
            if (!this.cb.getClient().isClosed()) {
                GridPane gp = (GridPane) clickEvent.getSource();

                for (Node node : gp.getChildren()) {
                    if (node instanceof Circle && node.getBoundsInParent().contains(clickEvent.getX(),clickEvent.getY())) 
                    {
                       
                        if(GridPane.getRowIndex(node) != null && line == GridPane.getRowIndex(node)) 
                        {

                            Circle circle = (Circle) node;
                            circle.setVisible(true);
                            circle.setFill(clientColorChoice);

                            //returns null if column is 0
                            if (GridPane.getColumnIndex(node) == null) 
                                guessCircles[0] = clientColorChoice;

                            else 
                                guessCircles[GridPane.getColumnIndex(node)]= clientColorChoice;

                        }              
                    }
                }
            }     
        }
    }

    /**
     * Sends user guess to the server 
     * Receives a response and performs the task required from the response
     *
     * @param e
     */
    @FXML
    private void userAttempt(ActionEvent e) throws Exception {

        if(cb.getClient() != null)
        {
            if (!this.cb.getClient().isClosed()) {
                //all circles have been set
                if (guessCircles[0] != null && guessCircles[1] != null
                        && guessCircles[2] != null && guessCircles[3] != null) {
                    try {
                        byte[] usersGuessInBytes = convertColorsToBytes();
                        MMClient client = cb.getClient();
                        log.info("UserAttempt: " + Arrays.toString(usersGuessInBytes));
                        byte[] response = client.sendGuesses(usersGuessInBytes);
                        log.info("UserAttempt: " + Arrays.toString(response));

                        //if response is correct
                        if (response != null && response.length == 5) {
                                if (response[0] == 1) 
                                    displayHints(response);
                                 else if (response[0] == 2) 
                                    displayEndGame(response, usersGuessInBytes);
                                
                        }

                        //need to reset these values
                        guessCircles = new Paint[4];
                        line--;       

                    } catch (IOException ioe) {
                        log.error("Error: " + ioe.getMessage());
                    }
                }
            }      
        }
    }

    /**
     * Displays the hints received from the server
     *
     * @param hints
     */
    private void displayHints(byte[] hints) {

        
        GridPane currentHintsGridPane = new GridPane();
        
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof GridPane && line == GridPane.getRowIndex(node))
                currentHintsGridPane = (GridPane) node;
            
        }
        ObservableList<Node> children = currentHintsGridPane.getChildren();
        
        boolean isEllipsePlacedOnGrid;
        //start at 1 because hints[0] is the response type
        for (int i = 1; i < hints.length; i++) {
            isEllipsePlacedOnGrid = false;
            for (int j = 0; j < 4 && !isEllipsePlacedOnGrid; j++) {
                Ellipse ellipse = (Ellipse) children.get(j);
                if ( hints[i] == 0 && !ellipse.isVisible()) 
                {
                    ellipse.setFill(Color.rgb(0, 0, 0));
                    isEllipsePlacedOnGrid = true;
                    ellipse.setVisible(true);
                } 
                else if (hints[i] == 1 && !ellipse.isVisible()) 
                {
                    ellipse.setFill(Color.rgb(255, 255, 255));
                    isEllipsePlacedOnGrid = true;
                    ellipse.setVisible(true);
                }
            }
        }    
    }

    /**
     * Ends the game
     * Checks if the answer is the same as the guess
     * Fills the top level circles with the answer
     * Displays a message
     *
     * @param answer
     * @param guess
     */
    private void displayEndGame(byte[] answer, byte[] guess) {
        
        boolean isVictory = checkIfEquals(answer,guess);
        
        if (isVictory) 
            label.setText("Victory!");
        else 
            label.setText("Defeat!");
        
        convertBytesToColors(answer);
        solutionCircle1.setVisible(true);
        solutionCircle2.setVisible(true);
        solutionCircle3.setVisible(true);
        solutionCircle4.setVisible(true);
        solutionCircle1.setFill(guessCircles[0]);
        solutionCircle2.setFill(guessCircles[1]);
        solutionCircle3.setFill(guessCircles[2]);
        solutionCircle4.setFill(guessCircles[3]);   
        
        gameGrid.setMouseTransparent(true);
    }
    
   
    /**
     * This method checks if the answer and guess arrays are equal
     * @param answer
     * @param guess
     * @return true if they are equal
     */
    private boolean checkIfEquals(byte[] answer, byte[] guess)
    {
         boolean isEquals = true;
         for (int i = 1; i < 5 && isEquals; i++) {
            if (answer[i] != guess[i]) {
                isEquals = false;
            }
        } 
         return isEquals;
    }

   /**
    * Hides everything from the board
    */
    private void hideCirclesAndEllipses() {
        
         GridPane pane = new GridPane();
         for (Node node : gameGrid.getChildren()) {
            if (node instanceof Circle && node.isVisible()) 
                node.setVisible(false);          
            
            if (node instanceof GridPane) {
                pane = (GridPane) node;
                ObservableList<Node> childs = pane.getChildren();
                for (int j = 0; j < 4; j++) {
                    if(childs.get(j).isVisible())
                        childs.get(j).setVisible(false);
                
                }
            }         
        }      
    }
    
    /**
     * Converts circle colors to their corresponding integers
     *
     * @return
     */
    private byte[] convertColorsToBytes() {
        byte[] nums = new byte[guessCircles.length+1];
        nums[0] = 1;

 
        for (int i = 0; i < guessCircles.length; i++) {
            if (guessCircles[i].equals(greenCircle)) 
                nums[i+1] = 1;
            else if (guessCircles[i].equals(blueCircle)) 
                nums[i+1] = 2;
            else if (guessCircles[i].equals(brownCircle)) 
                nums[i+1] = 3;
            else if (guessCircles[i].equals(orangeCircle)) 
                nums[i+1] = 4;
            else if (guessCircles[i].equals(pinkCircle)) 
                nums[i+1] = 5;
            else if (guessCircles[i].equals(redCircle)) 
                nums[i+1] = 6;
            else if (guessCircles[i].equals(yellowCircle)) 
                nums[i+1] = 7;
            else if (guessCircles[i].equals(lightBlueCircle)) 
                nums[i+1] = 8;
            
        }
        return nums;
    }

    /**
     * Converts integers to circle colors
     *
     * @param numbers
     */
    private void convertBytesToColors(byte[] numbers) {
        for (int i = 1; i < numbers.length; i++) {
            if(numbers[i] == 1)
                guessCircles[i-1] = greenCircle;
            else if(numbers[i] == 2)
                guessCircles[i-1] = blueCircle;
            else if(numbers[i] == 3)
                guessCircles[i-1] = brownCircle;
            else if(numbers[i] == 4)
                guessCircles[i-1] = orangeCircle;
            else if(numbers[i] == 5)
                guessCircles[i-1] = pinkCircle;
            else if(numbers[i] == 6)
                guessCircles[i-1] = redCircle;
            else if(numbers[i] == 7)
                guessCircles[i-1] = yellowCircle;
            else if(numbers[i] == 8)
                guessCircles[i-1] = lightBlueCircle;
  
        }
    }
}