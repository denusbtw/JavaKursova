package com.kursova.ui.renderer;

import com.kursova.model.TourDTO;
import com.kursova.service.FavoriteApiService;
import javafx.scene.control.*;

public class TourTableUIRenderer {

    public static void configureFavoriteColumn(TableColumn<TourDTO, Void> column, TableView<TourDTO> table, FavoriteApiService service) {
        column.setCellFactory(col -> new FavoriteToggleCell(service, table));
    }

    public static void configureRowStyling(TableView<TourDTO> table) {
        TourRowStyler.applyStripedStyle(table);
    }
}
