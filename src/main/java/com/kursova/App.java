package com.kursova;


import com.kursova.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final String MAIN_FXML = "/com/kursova/ui/views/main.fxml";
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 575;
    private static final String TITLE = "Путівки";

    @Override
    public void start(Stage primaryStage) {
        Parent root = loadMainView(primaryStage);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Parent loadMainView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_FXML));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setPrimaryStage(stage);

            return root;
        } catch (Exception e) {
            logger.error("Failed to load main view", e);
            System.exit(1);
        }
        return null;
    }
}
