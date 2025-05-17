package com.kursova.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private Stage stage;

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
        logger.info("Primary stage set.");
    }

    public void handleShowAllTours(ActionEvent event) {
        logger.info("User selected: Show all tours");
        openTourTable(false, "Усі путівки");
    }

    public void handleShowFavouriteTours(ActionEvent event) {
        logger.info("User selected: Show favourite tours");
        openTourTable(true, "Улюблені путівки");
    }

    private void openTourTable(boolean showFavourites, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kursova/ui/views/tourTable.fxml"));
            Parent root = loader.load();

            TableViewController controller = loader.getController();
            controller.setShowFavourites(showFavourites);

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            logger.info("Loaded tour table view: {}, title='{}'", showFavourites ? "favourites" : "all", title);
        } catch (Exception e) {
            logger.error("Failed to load tourTable.fxml", e);
        }
    }
}
