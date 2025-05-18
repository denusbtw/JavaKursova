package com.kursova.ui.renderer;

import com.kursova.model.TourDTO;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class TourRowStyler {
    public static void applyStripedStyle(TableView<TourDTO> table) {
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TourDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    String color = (getIndex() % 2 == 0) ? "#ffffff" : "#dddddd";
                    setStyle("-fx-background-color: " + color + ";");
                }
            }
        });
    }
}

