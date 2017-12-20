package com.uenyihung.mastermindv2.client;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class takes care of starting the Application.
 * 
 * @author  Naasir Jusab, Dimitri Spyropoulos
 * @version 2.0
 * @since 21/10/2016
 * 
 */
public class MainApp extends Application {

   /**
    * Sets the scene when the application is executed
    * @param stage
    * @throws Exception 
    */ 
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.
                load(getClass().getResource("/fxml/Scene.fxml"));       
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");        
        
        stage.setTitle("Mastermind");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
      
    }
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}