package com.esprit.controllers.common;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.enums.CategoryType;
import com.esprit.models.common.Category;
import com.esprit.models.users.Admin;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.User;
import com.esprit.services.common.CategoryService;
import com.esprit.utils.PageRequest;
import com.esprit.utils.SessionManager;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Unified controller for managing all category types (MOVIE, SERIE, PRODUCT).
 * Uses pure FormsFX with FormRenderer for form generation.
 *
 * @author RAKCHA Team
 * @version 2.0.0
 */

@Log4j2
public class CategoryManagementController implements Initializable {

    private final CategoryService categoryService = new CategoryService();
    // FormsFX properties
    private final StringProperty categoryNameProperty = new SimpleStringProperty("");
    private final StringProperty categoryDescriptionProperty = new SimpleStringProperty("");
    private final ListProperty<CategoryType> typeListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<CategoryType> selectedTypeProperty = new SimpleObjectProperty<>();
    private User currentUser;
    private CategoryType selectedType = null; // null means all types
    private Form categoryForm;

    // FXML Components
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Long> idColumn;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> descriptionColumn;
    @FXML
    private TableColumn<Category, String> typeColumn;
    @FXML
    private TableColumn<Category, Void> actionsColumn;

    @FXML
    private VBox formContainer;
    @FXML
    private ComboBox<CategoryType> filterTypeComboBox;

    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusLabel;

    private Category selectedCategory = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SessionManager.getCurrentUser();
        setupTable();
        setupTypeComboBoxes();
        setupSearch();
        loadCategories();

        // Initialize FormsFX with FormRenderer
        setupFormsFX();

        log.info("CategoryManagementController initialized for user: {}",
            currentUser != null ? currentUser.getEmail() : "unknown");
    }

    /**
     * Configures FormsFX for category form with FormRenderer.
     */
    private void setupFormsFX() {
        // Setup available types based on user role
        ObservableList<CategoryType> availableTypes = FXCollections.observableArrayList();
        if (currentUser instanceof Admin) {
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE, CategoryType.PRODUCT);
        } else if (currentUser instanceof CinemaManager) {
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE);
        } else {
            availableTypes.addAll(CategoryType.values());
        }
        typeListProperty.setAll(availableTypes);
        if (!availableTypes.isEmpty()) {
            selectedTypeProperty.set(availableTypes.get(0));
        }

        categoryForm = Form.of(
            com.dlsc.formsfx.model.structure.Group.of(
                Field.ofStringType(categoryNameProperty)
                    .label("Name")
                    .placeholder("Enter category name")
                    .required("Category name is required")
                    .validate(
                        StringLengthValidator.between(2, 50, "Name must be between 2 and 50 characters")
                    ),
                Field.ofSingleSelectionType(typeListProperty, selectedTypeProperty)
                    .label("Type")
                    .required("Please select a category type"),
                Field.ofStringType(categoryDescriptionProperty)
                    .label("Description")
                    .placeholder("Enter category description")
                    .multiline(true)
                    .required("Description is required")
                    .validate(
                        StringLengthValidator.atLeast(1, "Description is required")
                    )
            )
        ).title("Category Details");

        // Render the form into the container
        if (formContainer != null) {
            FormRenderer renderer = new FormRenderer(categoryForm);
            renderer.getStyleClass().add("formsfx-form");
            formContainer.getChildren().clear();
            formContainer.getChildren().add(renderer);
        }
    }

    /**
     * Configure table columns and cell factories.
     */
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getType().toString()));

        // Setup actions column with Edit and Delete buttons
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("edit-btn");
                deleteBtn.getStyleClass().add("delete-btn");

                editBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    editCategory(category);
                });

                deleteBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    deleteCategory(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        // Enable row selection to populate form
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        categoryTable.setEditable(false);
    }

    /**
     * Setup type combo boxes based on user role.
     */
    private void setupTypeComboBoxes() {
        ObservableList<CategoryType> availableTypes = FXCollections.observableArrayList();

        if (currentUser instanceof Admin) {
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE, CategoryType.PRODUCT);
        } else if (currentUser instanceof CinemaManager) {
            availableTypes.addAll(CategoryType.MOVIE, CategoryType.SERIE);
        } else {
            availableTypes.addAll(CategoryType.values());
        }

        // Filter combo box includes "All" option
        ObservableList<CategoryType> filterTypes = FXCollections.observableArrayList();
        filterTypes.add(null); // null represents "All"
        filterTypes.addAll(availableTypes);
        filterTypeComboBox.setItems(filterTypes);
        filterTypeComboBox.getSelectionModel().selectFirst();

        // Custom cell factory to display "All" for null
        filterTypeComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All Categories" : item.toString()));
            }
        });
        filterTypeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CategoryType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? "All Categories" : item.toString()));
            }
        });

        // Filter change listener
        filterTypeComboBox.setOnAction(e -> {
            selectedType = filterTypeComboBox.getValue();
            loadCategories();
        });
    }

    /**
     * Setup search functionality.
     */
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCategories(newVal));
    }

    /**
     * Load categories from database based on current filter.
     */
    private void loadCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        PageRequest pageRequest = PageRequest.defaultPage();

        if (selectedType != null) {
            categories.addAll(categoryService.readByType(selectedType, pageRequest).getContent());
        } else {
            if (currentUser instanceof Admin) {
                categories.addAll(categoryService.read(pageRequest).getContent());
            } else if (currentUser instanceof CinemaManager) {
                categories.addAll(categoryService.getAllByType(CategoryType.MOVIE));
                categories.addAll(categoryService.getAllByType(CategoryType.SERIE));
            }
        }

        categoryTable.setItems(categories);
        updateStatus("Loaded " + categories.size() + " categories");
    }

    /**
     * Filter displayed categories by search text.
     */
    private void filterCategories(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadCategories();
            return;
        }

        String lowerSearch = searchText.toLowerCase().trim();
        ObservableList<Category> filtered = categoryTable.getItems().filtered(cat ->
            cat.getName().toLowerCase().contains(lowerSearch) ||
                cat.getDescription().toLowerCase().contains(lowerSearch) ||
                cat.getType().toString().toLowerCase().contains(lowerSearch)
        );
        categoryTable.setItems(filtered);
    }

    /**
     * Populate the form with category data for editing.
     */
    private void populateForm(Category category) {
        selectedCategory = category;
        categoryNameProperty.set(category.getName());
        categoryDescriptionProperty.set(category.getDescription());
        selectedTypeProperty.set(category.getType());
        addButton.setText("Update");
    }

    /**
     * Clear the form fields.
     */
    @FXML
    private void clearForm() {
        selectedCategory = null;
        categoryNameProperty.set("");
        categoryDescriptionProperty.set("");
        if (!typeListProperty.isEmpty()) {
            selectedTypeProperty.set(typeListProperty.get(0));
        }
        addButton.setText("Add");
        categoryTable.getSelectionModel().clearSelection();

        // Reset form validation state
        if (categoryForm != null) {
            categoryForm.reset();
        }
    }

    /**
     * Add or update a category.
     */
    @FXML
    private void saveCategory(ActionEvent event) {
        // Validate using FormsFX
        if (!categoryForm.isValid()) {
            categoryForm.persist();
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fix the validation errors before saving.");
            return;
        }

        String name = categoryNameProperty.get().trim();
        String description = categoryDescriptionProperty.get().trim();
        CategoryType type = selectedTypeProperty.get();

        if (type == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a category type.");
            return;
        }

        try {
            if (selectedCategory != null) {
                selectedCategory.setName(name);
                selectedCategory.setDescription(description);
                selectedCategory.setType(type);
                categoryService.update(selectedCategory);
                updateStatus("Category updated successfully");
                log.info("Updated category: {}", selectedCategory.getId());
            } else {
                Category newCategory = new Category(name, description, type);
                categoryService.create(newCategory);
                updateStatus("Category created successfully");
                log.info("Created new category: {}", name);
            }

            clearForm();
            loadCategories();
        } catch (Exception e) {
            log.error("Error saving category: {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save category: " + e.getMessage());
        }
    }

    /**
     * Edit a category by populating the form.
     */
    private void editCategory(Category category) {
        populateForm(category);
    }

    /**
     * Delete a category with confirmation.
     */
    private void deleteCategory(Category category) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Category");
        confirmDialog.setContentText("Are you sure you want to delete the category '" + category.getName() + "'?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.delete(category);
                loadCategories();
                clearForm();
                updateStatus("Category deleted successfully");
                log.info("Deleted category: {}", category.getId());
            } catch (Exception e) {
                log.error("Error deleting category: {}", e.getMessage(), e);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete category: " + e.getMessage());
            }
        }
    }

    /**
     * Show an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Update the status label.
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Navigate back to the appropriate dashboard.
     */
    @FXML
    private void goBack(ActionEvent event) {
        try {
            String fxmlPath;
            if (currentUser instanceof Admin) {
                fxmlPath = "/ui/users/HomeAdmin.fxml";
            } else if (currentUser instanceof CinemaManager) {
                fxmlPath = "/ui/users/HomeCinemaManager.fxml";
            } else {
                fxmlPath = "/ui/users/HomeClient.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            log.error("Error navigating back: {}", e.getMessage(), e);
        }
    }

    /**
     * Set the initial category type filter.
     */
    public void setInitialType(CategoryType type) {
        this.selectedType = type;
        if (filterTypeComboBox != null) {
            filterTypeComboBox.setValue(type);
        }
        loadCategories();
    }

    /**
     * Set the current user for role-based access control.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        setupTypeComboBoxes();
        setupFormsFX(); // Rebuild form with correct types
        loadCategories();
    }
}
