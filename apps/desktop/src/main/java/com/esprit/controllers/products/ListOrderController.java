package com.esprit.controllers.products;

import com.esprit.models.products.Order;
import com.esprit.enums.OrderStatus;
import com.esprit.services.products.OrderService;
import com.esprit.utils.NavigationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j2;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

@Log4j2
public class ListOrderController {

    @FXML
    private TableColumn<Order, String> idStatu;
    @FXML
    private TableColumn<Order, String> idadresse;
    @FXML
    private TableColumn<Order, String> iddate;
    @FXML
    private TableColumn<Order, String> idnom;
    @FXML
    private TableColumn<Order, String> idnumero;
    @FXML
    private TableColumn<Order, String> idprenom;
    @FXML
    private TableView<Order> orderTableView;
    @FXML
    private TextField SearchBar;
    @FXML
    private TableColumn<Order, Void> deleteColumn;
    @FXML
    private ComboBox<String> statusComboBox;
    private static final String ALL_STATUSES = "All statuses";

    /**
     * Initializes the controller by setting up event listeners and populating the
     * table view.
     *
     * <p>
     * This method configures the search bar to trigger filtering when text changes,
     * initializes the order display, and sets up the delete column functionality.
     * </p>
     */
    @FXML
    void initialize() {
        this.statusComboBox.getItems().add(ALL_STATUSES);
        Arrays.stream(OrderStatus.values()).map(OrderStatus::getDisplayName).forEach(this.statusComboBox.getItems()::add);
        this.statusComboBox.setValue(ALL_STATUSES);
        this.SearchBar.textProperty().addListener((observable, oldValue, newValue) -> this.filterOrders());
        this.statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> this.filterOrders());
        this.afficheOrder();
        this.initDeleteColumn();
    }

    /**
     * Populates the TableView with orders and configures columns to show client and
     * order fields.
     * <p>
     * Configures columns for client first name, client last name, address, phone
     * number, date, and status;
     * loads orders from OrderService into the table and enables cell selection.
     */
    void afficheOrder() {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        this.idnom.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getClient() == null ? "" : cell.getValue().getClient().getFirstName()));
        this.idprenom.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getClient() == null ? "" : cell.getValue().getClient().getLastName()));
        this.idadresse.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getShippingAddress()));
        this.idnumero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNumber()));
        this.iddate.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getOrderDate() == null ? "" : cell.getValue().getOrderDate().format(dateFormat)));
        this.idStatu.setCellValueFactory(cell -> {
            final OrderStatus status = cell.getValue().getStatus();
            return new SimpleStringProperty(status == null ? "" : status.getDisplayName());
        });
        // Utiliser une ObservableList pour stocker les éléments
        final ObservableList<Order> list = FXCollections.observableArrayList();
        final OrderService ps = new OrderService();
        list.addAll(ps.read());
        this.orderTableView.setItems(list);
        // Activer la sélection de cellules
        this.orderTableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Filter orders shown in the table by a search keyword.
     * <p>
     * If `keyword` is null or empty, all orders are displayed. Otherwise the table
     * is updated to show orders whose address, client's first name, client's
     * last name, or status contain the keyword (case-insensitive).
     *
     */
    private void filterOrders() {
        final OrderService orderservice = new OrderService();
        final ObservableList<Order> filteredList = FXCollections.observableArrayList();
        final String keyword = this.SearchBar.getText().strip().toLowerCase(Locale.ROOT);
        final String selectedStatus = this.statusComboBox.getValue();
        for (final Order order : orderservice.read()) {
            final String status = order.getStatus() == null ? "" : order.getStatus().getDisplayName();
            final var client = order.getClient();
            final boolean matchesStatus = ALL_STATUSES.equals(selectedStatus) || status.equals(selectedStatus);
            final boolean matchesSearch = keyword.isEmpty() || contains(order.getShippingAddress(), keyword)
                || contains(status, keyword) || (client != null
                    && (contains(client.getFirstName(), keyword) || contains(client.getLastName(), keyword)));
            if (matchesStatus && matchesSearch) {
                filteredList.add(order);
            }
        }
        this.orderTableView.setItems(filteredList);
    }

    private static boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(keyword);
    }

    /**
     * Add a per-row delete button to the deleteColumn that removes the
     * corresponding order.
     *
     * <p>
     * Each cell produced for the column contains a "Delete" button which, when
     * pressed,
     * deletes the Order using OrderService and updates the TableView to reflect the
     * removal.
     * </p>
     */
    private void initDeleteColumn() {
        final Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<>() {
            /**
             * Create a TableCell that renders a delete button for its Order row.
             *
             * <p>
             * Pressing the button deletes the corresponding Order via OrderService and
             * removes
             * the Order from the table view.
             * </p>
             *
             * @param param the TableColumn to which the cell belongs
             * @return a TableCell containing a delete button that removes the row's Order
             *         from the data source and table
             */
            @Override
            public TableCell<Order, Void> call(TableColumn<Order, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        this.btnDelete.getStyleClass().add("delete-button");
                        this.btnDelete.setOnAction((final ActionEvent event) -> {
                            final Order order = this.getTableView().getItems().get(this.getIndex());
                            final OrderService ps = new OrderService();
                            ps.delete(order);
                            // Mise à jour de la TableView après la suppression de la base de données
                            orderTableView.getItems().remove(order);
                            orderTableView.refresh();
                        });
                    }

                    /**
                     * Set the cell's graphic to the row delete button when the cell contains data;
                     * clear the graphic when empty.
                     *
                     * @param item  unused placeholder for the cell value (always null for a `Void`
                     *              cell)
                     * @param empty `true` if the cell does not contain data, `false` otherwise
                     */
                    @Override
                    protected void updateItem(final Void item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic(null);
                        } else {
                            this.setGraphic(this.btnDelete);
                        }

                    }

                };
            }

        };
        this.deleteColumn.setCellFactory(cellFactory);
    }

    /**
     * Opens the order statistics view in the current window.
     *
     * @param event The action event that triggered this method
     */
    @FXML
    void statOrder(final ActionEvent event) {
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationManager.navigateToOrderAnalytics(stage);
    }

}
