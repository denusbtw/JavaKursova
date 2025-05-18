package com.kursova.controller;

import com.kursova.util.ViewLoader;
import javafx.event.ActionEvent;
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
            TableViewController controller = ViewLoader.load("/com/kursova/ui/views/tourTable.fxml", stage, title);
            controller.setShowFavourites(showFavourites);
            logger.info("Loaded tour table view: {}, title='{}'", showFavourites ? "favourites" : "all", title);
        } catch (Exception e) {
            logger.error("Failed to load tourTable.fxml", e);
        }
    }
}
