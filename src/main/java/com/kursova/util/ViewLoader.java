package com.kursova.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewLoader {
    /**
     * Завантажує FXML і повертає контролер.
     *
     * @param fxmlPath шлях до FXML
     * @param stage    основна сцена
     * @param title    заголовок вікна
     * @param <T>      тип контролера
     * @return контролер
     */
    public static <T> T load(String fxmlPath, Stage stage, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}
