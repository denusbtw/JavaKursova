package com.kursova.ui.renderer;

import com.kursova.model.TourDTO;
import com.kursova.service.FavoriteApiService;
import javafx.scene.control.*;

public class FavoriteToggleCell extends TableCell<TourDTO, Void> {
    private final Button button = new Button();
    private final FavoriteApiService favoriteService;
    private final TableView<TourDTO> table;

    public FavoriteToggleCell(FavoriteApiService favoriteService, TableView<TourDTO> table) {
        this.favoriteService = favoriteService;
        this.table = table;

        button.setOnAction(event -> {
            TourDTO tour = getTableView().getItems().get(getIndex());
            if (Boolean.TRUE.equals(tour.getIsFavorite())) {
                favoriteService.removeFromFavorites(tour.getId());
                tour.setIsFavorite(false);
            } else {
                favoriteService.addToFavorites(tour.getId());
                tour.setIsFavorite(true);
            }
            updateStyle(tour);
            table.refresh();
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getIndex() >= getTableView().getItems().size()) {
            setGraphic(null);
        } else {
            TourDTO tour = getTableView().getItems().get(getIndex());
            updateStyle(tour);
            setGraphic(button);
        }
    }

    private void updateStyle(TourDTO tour) {
        button.getStyleClass().setAll("fav-button");
        if (Boolean.TRUE.equals(tour.getIsFavorite())) {
            button.setText("❤");
            button.getStyleClass().add("filled");
        } else {
            button.setText("♡");
            button.getStyleClass().add("outlined");
        }
    }
}
