package com.kursova.controller;

import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.service.FavoriteApiService;
import com.kursova.service.TourApiService;
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

import java.io.IOException;
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

    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @FXML private ComboBox<String> typeFilterBox;
    @FXML private ComboBox<String> transportFilterBox;
    @FXML private ComboBox<String> mealOptionFilterBox;

    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField minRatingField;
    @FXML private TextField maxRatingField;
    @FXML private TextField minDaysField;
    @FXML private TextField maxDaysField;
    @FXML private Button applyFiltersButton;

    @FXML private TableView<TourDTO> tourTable;
    @FXML private TableColumn<TourDTO, String> nameColumn;
    @FXML private TableColumn<TourDTO, String> typeColumn;
    @FXML private TableColumn<TourDTO, String> mealOptionColumn;
    @FXML private TableColumn<TourDTO, Integer> numberOfDaysColumn;
    @FXML private TableColumn<TourDTO, Integer> priceColumn;
    @FXML private TableColumn<TourDTO, Double> ratingColumn;
    @FXML private TableColumn<TourDTO, Void> favouriteColumn;
    @FXML private TableColumn<TourDTO, String> transportColumn;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;

    public void setShowFavourites(boolean showFavourites) {
        this.showFavourites = showFavourites;
        titleLabel.setText("Улюблені путівки");
        loadData();
    }

    @FXML
    public void initialize() {
        titleLabel.setText("Усі доступні тури");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        mealOptionColumn.setCellValueFactory(new PropertyValueFactory<>("mealOption"));
        numberOfDaysColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfDays"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        transportColumn.setCellValueFactory(new PropertyValueFactory<>("transportName"));

        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                setText((empty || price == null) ? null : price + " ₴");
            }
        });

        tourTable.setFixedCellSize(38);

        tourTable.setPrefHeight(tourTable.getFixedCellSize() * 10 + 38);
        tourTable.setMinHeight(Region.USE_PREF_SIZE);
        tourTable.setMaxHeight(Region.USE_PREF_SIZE);

        tourTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 3);         // 3/12
        typeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 1);         // 1/12
        transportColumn.setMaxWidth(1f * Integer.MAX_VALUE * 1.2);  // 1.2/12
        mealOptionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 1.4); // 1.4/12
        numberOfDaysColumn.setMaxWidth(1f * Integer.MAX_VALUE * 1); // 1/12
        priceColumn.setMaxWidth(1f * Integer.MAX_VALUE * 1);        // 1/12
        ratingColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.8);     // 0.8/12
        favouriteColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.6);  // 0.6/12

        tourTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TourDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #ebf8ff;");
                } else {
                    int index = getIndex();
                    String color = (index % 2 == 0) ? "#ffffff" : "#dddddd";
                    setStyle("-fx-background-color: " + color + ";");
                }
            }
        });

        addFavouriteButtonToTable();
        loadFilterData();
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String inputText = searchField.getText();
        searchQuery = inputText != null ? inputText.trim() : "";
        currentPage = 0;
        loadData();
        logger.info("User searched for '{}'", searchQuery);
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
            logger.info("Navigated to page {}", currentPage);
        }
    }

    @FXML
    public void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadData();
            logger.info("Navigated to page {}", currentPage);
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
            logger.info("Returning to main menu");
        } catch (IOException e) {
            logger.error("Failed to load main menu", e);
        }
    }

    private void loadData() {
        String type = getSelectedFilterValue(typeFilterBox);
        String transportName = getSelectedFilterValue(transportFilterBox);
        String mealOption = getSelectedFilterValue(mealOptionFilterBox);
        Integer minPrice = parseIntOrNull(minPriceField.getText());
        Integer maxPrice = parseIntOrNull(maxPriceField.getText());
        Integer minDays = parseIntOrNull(minDaysField.getText());
        Integer maxDays = parseIntOrNull(maxDaysField.getText());
        Double minRating = parseDoubleOrNull(minRatingField.getText());
        Double maxRating = parseDoubleOrNull(maxRatingField.getText());

        logFilterParameters(type, transportName, mealOption, minPrice, maxPrice, minDays, maxDays, minRating, maxRating);

        PageResponse<TourDTO> pageResponse = showFavourites
                ? favoriteService.getFavorites(
                searchQuery, type, mealOption, minDays, maxDays,
                minPrice, maxPrice, minRating, maxRating,
                transportName, currentPage, pageSize
        )
                : tourService.getTours(
                currentPage, pageSize, searchQuery,
                type, transportName, mealOption,
                minPrice, maxPrice, minDays, maxDays,
                minRating, maxRating
        );

        tourTable.setItems(FXCollections.observableArrayList(pageResponse.getContent()));
        favouriteColumn.setVisible(false);
        favouriteColumn.setVisible(true);

        totalPages = pageResponse.getTotalPages();
        pageLabel.setText("Сторінка " + (currentPage + 1) + " з " + totalPages);
        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
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

    private void addFavouriteButtonToTable() {
        favouriteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(event -> {
                    TourDTO tour = getTableView().getItems().get(getIndex());

                    if (Boolean.TRUE.equals(tour.getIsFavorite())) {
                        favoriteService.removeFromFavorites(tour.getId());
                        tour.setIsFavorite(false);
                        logger.info("Removed tour with ID={} from favorites", tour.getId());
                    } else {
                        favoriteService.addToFavorites(tour.getId());
                        tour.setIsFavorite(true);
                        logger.info("Added tour with ID={} to favorites", tour.getId());
                    }

                    updateButtonStyle(tour);
                    tourTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    TourDTO tour = getTableView().getItems().get(getIndex());
                    updateButtonStyle(tour);
                    setGraphic(actionButton);
                }
            }

            private void updateButtonStyle(TourDTO tour) {
                actionButton.getStyleClass().setAll("fav-button");
                if (tour != null && Boolean.TRUE.equals(tour.getIsFavorite())) {
                    actionButton.setText("❤");
                    actionButton.getStyleClass().add("filled");
                } else {
                    actionButton.setText("♡");
                    actionButton.getStyleClass().add("outlined");
                }
            }
        });
    }

    private String getSelectedFilterValue(ComboBox<String> comboBox) {
        String value = comboBox.getValue();
        return (value != null && !"Усі".equals(value)) ? value : null;
    }

    private Integer parseIntOrNull(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String text) {
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return null;
        }
    }

    private void logFilterParameters(String type, String transport, String meal,
                                     Integer minPrice, Integer maxPrice,
                                     Integer minDays, Integer maxDays,
                                     Double minRating, Double maxRating) {
        StringBuilder log = new StringBuilder(showFavourites ? "Loading FAVORITES with filters:" : "Loading TOURS with filters:");

        if (searchQuery != null && !searchQuery.isBlank()) {
            log.append(" name='").append(searchQuery).append("'");
        }
        if (type != null) log.append(", type='").append(type).append("'");
        if (transport != null) log.append(", transport='").append(transport).append("'");
        if (meal != null) log.append(", meal='").append(meal).append("'");

        if (minPrice != null || maxPrice != null) {
            log.append(", price=[").append(minPrice != null ? minPrice : "").append("-")
                    .append(maxPrice != null ? maxPrice : "").append("]");
        }

        if (minDays != null || maxDays != null) {
            log.append(", days=[").append(minDays != null ? minDays : "").append("-")
                    .append(maxDays != null ? maxDays : "").append("]");
        }

        if (minRating != null || maxRating != null) {
            log.append(", rating=[").append(minRating != null ? minRating : "").append("-")
                    .append(maxRating != null ? maxRating : "").append("]");
        }

        logger.info(log.toString());
    }
}
