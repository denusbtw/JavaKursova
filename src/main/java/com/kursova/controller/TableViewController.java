package com.kursova.controller;

import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.model.TourFilter;
import com.kursova.service.FavoriteApiService;
import com.kursova.service.TourApiService;
import com.kursova.ui.renderer.TourTableUIRenderer;
import com.kursova.util.ComboBoxUtils;
import com.kursova.util.NumberParser;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TableViewController {

    private static final Logger logger = LoggerFactory.getLogger(TableViewController.class);

    private final int pageSize = 10;
    private int currentPage = 0;
    private int totalPages = 1;
    private boolean showFavourites = false;
    private String searchQuery = "";

    private final TourApiService tourService = new TourApiService();
    private final FavoriteApiService favoriteService = new FavoriteApiService();

    @FXML private Label titleLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterBox, transportFilterBox, mealOptionFilterBox;
    @FXML private TextField minPriceField, maxPriceField, minRatingField, maxRatingField, minDaysField, maxDaysField;
    @FXML private TableView<TourDTO> tourTable;
    @FXML private TableColumn<TourDTO, String> nameColumn, typeColumn, mealOptionColumn, transportColumn;
    @FXML private TableColumn<TourDTO, Integer> numberOfDaysColumn, priceColumn;
    @FXML private TableColumn<TourDTO, Double> ratingColumn;
    @FXML private TableColumn<TourDTO, Void> favouriteColumn;
    @FXML private Button prevPageButton, nextPageButton;
    @FXML private Label pageLabel;

    public void setShowFavourites(boolean showFavourites) {
        this.showFavourites = showFavourites;
        titleLabel.setText(showFavourites ? "Улюблені путівки" : "Усі доступні путівки");
        loadData();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        TourTableUIRenderer.configureFavoriteColumn(favouriteColumn, tourTable, favoriteService);
        TourTableUIRenderer.configureRowStyling(tourTable);
        loadFilterData();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        mealOptionColumn.setCellValueFactory(new PropertyValueFactory<>("mealOption"));
        transportColumn.setCellValueFactory(new PropertyValueFactory<>("transportName"));
        numberOfDaysColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfDays"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                setText((empty || price == null) ? null : price + " ₴");
            }
        });

        tourTable.setFixedCellSize(38);
        tourTable.setPrefHeight(38 * 10 + 38);
        tourTable.setMinHeight(Region.USE_PREF_SIZE);
        tourTable.setMaxHeight(Region.USE_PREF_SIZE);
        tourTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        searchQuery = searchField.getText() != null ? searchField.getText().trim() : "";
        currentPage = 0;
        loadData();
    }

    @FXML
    public void handleApplyFilters(ActionEvent event) {
        currentPage = 0;
        loadData();
    }

    @FXML
    public void handlePrevPage(ActionEvent event) {
        if (currentPage > 0) {
            currentPage--;
            loadData();
        }
    }

    @FXML
    public void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadData();
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kursova/ui/views/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            controller.setPrimaryStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("Головне меню");
        } catch (Exception e) {
            logger.error("Failed to return to main menu", e);
        }
    }

    private void loadData() {
        TourFilter filter = collectFilter();
        PageResponse<TourDTO> response = showFavourites
                ? favoriteService.getFavorites(filter.getName(), filter.getType(), filter.getMealOption(),
                filter.getMinDays(), filter.getMaxDays(),
                filter.getMinPrice(), filter.getMaxPrice(),
                filter.getMinRating(), filter.getMaxRating(),
                filter.getTransportName(), currentPage, pageSize)
                : tourService.getTours(currentPage, pageSize, filter.getName(),
                filter.getType(), filter.getTransportName(), filter.getMealOption(),
                filter.getMinPrice(), filter.getMaxPrice(), filter.getMinDays(), filter.getMaxDays(),
                filter.getMinRating(), filter.getMaxRating());

        tourTable.setItems(FXCollections.observableArrayList(response.getContent()));
        favouriteColumn.setVisible(false);
        favouriteColumn.setVisible(true);

        totalPages = response.getTotalPages();
        pageLabel.setText("Сторінка " + (currentPage + 1) + " з " + totalPages);
        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
    }

    private TourFilter collectFilter() {
        TourFilter filter = new TourFilter();
        filter.setName(searchQuery);
        filter.setType(ComboBoxUtils.getSelectedValue(typeFilterBox));
        filter.setTransportName(ComboBoxUtils.getSelectedValue(transportFilterBox));
        filter.setMealOption(ComboBoxUtils.getSelectedValue(mealOptionFilterBox));
        filter.setMinPrice(NumberParser.parseIntOrNull(minPriceField.getText()));
        filter.setMaxPrice(NumberParser.parseIntOrNull(maxPriceField.getText()));
        filter.setMinDays(NumberParser.parseIntOrNull(minDaysField.getText()));
        filter.setMaxDays(NumberParser.parseIntOrNull(maxDaysField.getText()));
        filter.setMinRating(NumberParser.parseDoubleOrNull(minRatingField.getText()));
        filter.setMaxRating(NumberParser.parseDoubleOrNull(maxRatingField.getText()));
        return filter;
    }

    private void loadFilterData() {
        List<String> types = new ArrayList<>(tourService.getTourTypes());
        types.add(0, "Усі");
        typeFilterBox.setItems(FXCollections.observableArrayList(types));

        List<String> transports = new ArrayList<>(tourService.getTransportNames());
        transports.add(0, "Усі");
        transportFilterBox.setItems(FXCollections.observableArrayList(transports));

        List<String> meals = new ArrayList<>(tourService.getMealOptions());
        meals.add(0, "Усі");
        mealOptionFilterBox.setItems(FXCollections.observableArrayList(meals));
    }
}