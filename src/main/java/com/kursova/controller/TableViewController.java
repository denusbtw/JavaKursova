package com.kursova.controller;

import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.service.TourApiService;
import com.kursova.service.FavoriteApiService;
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

    private boolean showFavourites = false;
    private int currentPage = 0;
    private final int pageSize = 10;
    private int totalPages = 1;

    private final TourApiService tourService = new TourApiService();
    private final FavoriteApiService favoriteService = new FavoriteApiService();

    public void setShowFavourites(boolean value) {
        this.showFavourites = value;
        loadData();
    }

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

    @FXML
    public void initialize() {
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

        tourTable.setFixedCellSize(40);
        tourTable.itemsProperty().addListener((obs, oldVal, newVal) -> {
            int rows = Math.min(10, newVal.size());
            tourTable.prefHeightProperty().bind(
                    tourTable.fixedCellSizeProperty().multiply(rows).add(28)
            );
            tourTable.setMaxHeight(Region.USE_PREF_SIZE);
        });

        addFavouriteButtonToTable();
        loadFilterData();
    }

    private String searchQuery = "";

    private void loadData() {
        if (showFavourites) {
            String type = typeFilterBox.getValue();
            if ("Усі".equals(type)) type = null;

            String transportName = transportFilterBox.getValue();
            if ("Усі".equals(transportName)) transportName = null;

            String mealOption = mealOptionFilterBox.getValue();
            if ("Усі".equals(mealOption)) mealOption = null;

            Integer minPrice = parseIntOrNull(minPriceField.getText());
            Integer maxPrice = parseIntOrNull(maxPriceField.getText());
            Integer minDays = parseIntOrNull(minDaysField.getText());
            Integer maxDays = parseIntOrNull(maxDaysField.getText());
            Double minRating = parseDoubleOrNull(minRatingField.getText());
            Double maxRating = parseDoubleOrNull(maxRatingField.getText());

            StringBuilder logMessage = new StringBuilder(showFavourites ? "Loading FAVORITES with filters:" : "Loading TOURS with filters:");

            if (searchQuery != null && !searchQuery.isBlank()) logMessage.append(" name='").append(searchQuery).append("'");
            if (type != null) logMessage.append(", type='").append(type).append("'");
            if (transportName != null) logMessage.append(", transport='").append(transportName).append("'");
            if (mealOption != null) logMessage.append(", meal='").append(mealOption).append("'");

            if (minPrice != null || maxPrice != null)
                logMessage.append(", price=[").append(minPrice != null ? minPrice : "").append("-").append(maxPrice != null ? maxPrice : "").append("]");
            if (minDays != null || maxDays != null)
                logMessage.append(", days=[").append(minDays != null ? minDays : "").append("-").append(maxDays != null ? maxDays : "").append("]");
            if (minRating != null || maxRating != null)
                logMessage.append(", rating=[").append(minRating != null ? minRating : "").append("-").append(maxRating != null ? maxRating : "").append("]");

            logger.info(logMessage.toString());

            PageResponse<TourDTO> filteredFavorites = favoriteService.getFavorites(
                    searchQuery, type, mealOption,
                    minDays, maxDays,
                    minPrice, maxPrice,
                    minRating, maxRating,
                    transportName,
                    currentPage, pageSize
            );

            tourTable.setItems(FXCollections.observableArrayList(filteredFavorites.getContent()));
            totalPages = filteredFavorites.getTotalPages();

            pageLabel.setText("Сторінка " + (currentPage + 1) + " з " + totalPages);
            prevPageButton.setDisable(currentPage == 0);
            nextPageButton.setDisable(currentPage >= totalPages - 1);
            return;
        }

        String type = typeFilterBox.getValue();
        if (type != null && type.equals("Усі")) type = null;

        String transportName = transportFilterBox.getValue();
        if (transportName != null && transportName.equals("Усі")) transportName = null;

        String mealOption = mealOptionFilterBox.getValue();
        if (mealOption != null && mealOption.equals("Усі")) mealOption = null;

        Integer minPrice = parseIntOrNull(minPriceField.getText());
        Integer maxPrice = parseIntOrNull(maxPriceField.getText());
        Integer minDays = parseIntOrNull(minDaysField.getText());
        Integer maxDays = parseIntOrNull(maxDaysField.getText());
        Double minRating = parseDoubleOrNull(minRatingField.getText());
        Double maxRating = parseDoubleOrNull(maxRatingField.getText());

        var pageResponse = tourService.getTours(currentPage, pageSize, searchQuery,
                type, transportName, mealOption, minPrice, maxPrice, minDays, maxDays, minRating, maxRating);

        tourTable.setItems(FXCollections.observableArrayList(pageResponse.getContent()));
        totalPages = pageResponse.getTotalPages();

        pageLabel.setText("Сторінка " + (currentPage + 1) + " з " + totalPages);
        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
    }

    private void loadFilterData() {
        List<String> types = new ArrayList<>(tourService.getTourTypes());
        types.add(0, "Усі");
        typeFilterBox.setItems(FXCollections.observableArrayList(types));

        List<String> transportNames = new ArrayList<>(tourService.getTransportNames());
        transportNames.add(0, "Усі");
        transportFilterBox.setItems(FXCollections.observableArrayList(transportNames));

        List<String> mealOptions = new ArrayList<>(tourService.getMealOptions());
        mealOptions.add(0, "Усі");
        mealOptionFilterBox.setItems(FXCollections.observableArrayList(mealOptions));
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

            private void updateButtonStyle(TourDTO tour) {
                if (tour != null && Boolean.TRUE.equals(tour.getIsFavorite())) {
                    actionButton.setStyle("-fx-background-color: red;");
                    actionButton.setText("❤");
                } else {
                    actionButton.setStyle("-fx-background-color: lightgray;");
                    actionButton.setText("♡");
                }
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

        });
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kursova/ui/views/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            controller.setPrimaryStage(stage); // ← ОБОВʼЯЗКОВО

            stage.setScene(new Scene(root));
            stage.setTitle("Головне меню");
            logger.info("Returning to main menu");
        } catch (IOException e) {
            logger.error("Failed to load main menu", e);
        }
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
    public void handleSearch(ActionEvent event) {
        String input = searchField.getText();
        searchQuery = input != null ? input.trim() : "";
        currentPage = 0;
        loadData();
        logger.info("User searched for '{}'", searchQuery);
    }

    private Integer parseIntOrNull(String input) {
        try { return Integer.parseInt(input); } catch (Exception e) { return null; }
    }
    private Double parseDoubleOrNull(String input) {
        try { return Double.parseDouble(input); } catch (Exception e) { return null; }
    }

    @FXML
    public void handleApplyFilters(ActionEvent event) {
        currentPage = 0;
        loadData();
    }

}
