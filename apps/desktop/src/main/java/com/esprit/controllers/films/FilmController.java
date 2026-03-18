package com.esprit.controllers.films;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.common.Category;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.common.CategoryService;
import com.esprit.services.films.ActorFilmService;
import com.esprit.services.films.ActorService;
import com.esprit.services.films.FilmCategoryService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.log4j.Log4j2;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.CheckComboBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Log4j2
public class FilmController {

    private static final Logger LOGGER = Logger.getLogger(FilmController.class.getName());
    private static final long CACHE_DURATION_MS = 60000; // 1 minute
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> yearsCheckBoxes = new ArrayList<>();
    // FormsFX properties for declarative form handling
    private final StringProperty filmNameProperty = new SimpleStringProperty("");
    private final StringProperty filmDescriptionProperty = new SimpleStringProperty("");
    private final IntegerProperty filmDurationProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty filmReleaseYearProperty = new SimpleIntegerProperty(Year.now().getValue());
    private final List<Subscription> subscriptions = new ArrayList<>();
    // Search performance caching (Issue #13)
    private final Map<Long, String> actorNamesCache = new HashMap<>();
    private final Map<Long, String> categoryNamesCache = new HashMap<>();
    Validator validator;
    private String cloudinaryImageUrl;
    private long cacheTimestamp = 0;
    private Form filmForm;
    @FXML
    private TableView<Film> tvFilms;
    @FXML
    private TableColumn<Film, String> colTitre;
    @FXML
    private TableColumn<Film, String> colDescription;
    @FXML
    private TableColumn<Film, String> colCategorie; // Changed to String to match simple display, or keep CheckComboBox
    // if complex
    @FXML
    private TableColumn<Film, Integer> colDuree;
    @FXML
    private TableColumn<Film, Integer> colDate;
    @FXML
    private TableColumn<Film, HBox> colImage;
    @FXML
    private TableColumn<Film, Button> colAction;

    @FXML
    private TextField tfNom;
    @FXML
    private TextArea taDescription;
    @FXML
    private TextField tfDuree;
    @FXML
    private TextField tfReleaseYear;
    @FXML
    private ImageView imageFilm;
    @FXML
    private TextField searchField;

    @FXML
    private CheckComboBox<String> checkComboBoxCategory;
    @FXML
    private CheckComboBox<String> checkComboBoxActor;
    @FXML
    private CheckComboBox<String> checkComboBoxCinema;

    @FXML
    private Button AjouterFilm_Button;
    @FXML
    private AnchorPane filmCrudInterface;
    @FXML
    private Button ajouterCinema_Button;
    @FXML
    private Button preview3D_Button;
    @FXML
    private StackPane preview3DContainer;

    private FilteredList<Film> filteredFilms;
    private SortedList<Film> sortedFilms;

    // 3D Preview components
    private Group root3D;
    private SubScene subScene3D;
    private PerspectiveCamera camera3D;
    private boolean is3DPreviewActive = false;
    private AnimationTimer rotationTimer;

    @FXML
    void initialize() {
        // Setup FormsFX for declarative form handling
        this.setupFormsFX();
        this.setupEasyBindings();

        this.readFilmTable();
        final CategoryService cs = new CategoryService();
        final FilmService fs = new FilmService();
        final CinemaService cinemaService = new CinemaService();
        final ActorService actorService = new ActorService();
        PageRequest actorPageRequest = PageRequest.defaultPage();
        final List<Actor> actors = actorService.read(actorPageRequest).getContent();
        final List<String> actorNames = actors.stream().map(Actor::getName).collect(Collectors.toList());
        this.checkComboBoxActor.getItems().addAll(actorNames);
        this.checkComboBoxActor.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item);
            Tooltip.install(this.checkComboBoxActor, tooltip2);
        });
        PageRequest cinemaPageRequest = PageRequest.defaultPage();
        final List<Cinema> cinemaList = cinemaService.read(cinemaPageRequest).getContent();
        final List<String> cinemaNames = cinemaList.stream().map(Cinema::getName).collect(Collectors.toList());
        this.checkComboBoxCinema.getItems().addAll(cinemaNames);
        this.checkComboBoxCinema.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item);
            Tooltip.install(this.checkComboBoxCinema, tooltip2);
        });
        final CategoryService categoryService = new CategoryService();
        PageRequest categoryPageRequest = PageRequest.defaultPage();
        final List<Category> categories = categoryService.read(categoryPageRequest).getContent();
        final List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        this.checkComboBoxCategory.getItems().addAll(categoryNames);

        setupAdvancedSearch();
    }

    /**
     * Sets up the FormsFX form with declarative validation rules.
     */
    private void setupFormsFX() {
        this.filmForm = Form.of(
            com.dlsc.formsfx.model.structure.Group.of(
                Field.ofStringType(this.filmNameProperty)
                    .label("Film Name")
                    .validate(StringLengthValidator.atLeast(2, "Film name must be at least 2 characters")),
                Field.ofStringType(this.filmDescriptionProperty)
                    .label("Description")
                    .validate(StringLengthValidator.atLeast(10, "Description must be at least 10 characters")),
                Field.ofIntegerType(this.filmDurationProperty)
                    .label("Duration (minutes)")
                    .validate(IntegerRangeValidator.atLeast(1, "Duration must be at least 1 minute")),
                Field.ofIntegerType(this.filmReleaseYearProperty)
                    .label("Release Year")
                    .validate(IntegerRangeValidator.between(1900, Year.now().getValue() + 5,
                        "Year must be between 1900 and " + (Year.now().getValue() + 5)))
            )
        );
    }

    /**
     * Sets up EasyBind subscriptions to sync text fields with FormsFX properties.
     */
    private void setupEasyBindings() {
        if (this.tfNom != null) {
            this.tfNom.textProperty().bindBidirectional(this.filmNameProperty);
        }
        if (this.taDescription != null) {
            this.taDescription.textProperty().bindBidirectional(this.filmDescriptionProperty);
        }
        if (this.tfDuree != null) {
            this.subscriptions.add(EasyBind.subscribe(this.tfDuree.textProperty(), text -> {
                try {
                    if (text != null && !text.isEmpty()) {
                        this.filmDurationProperty.set(Integer.parseInt(text));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }));
            this.subscriptions.add(EasyBind.subscribe(this.filmDurationProperty, duration -> {
                if (duration != null && duration.intValue() > 0) {
                    this.tfDuree.setText(String.valueOf(duration.intValue()));
                }
            }));
        }
        if (this.tfReleaseYear != null) {
            this.subscriptions.add(EasyBind.subscribe(this.tfReleaseYear.textProperty(), text -> {
                try {
                    if (text != null && !text.isEmpty()) {
                        this.filmReleaseYearProperty.set(Integer.parseInt(text));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }));
            this.subscriptions.add(EasyBind.subscribe(this.filmReleaseYearProperty, year -> {
                if (year != null && year.intValue() > 0) {
                    this.tfReleaseYear.setText(String.valueOf(year.intValue()));
                }
            }));
        }
    }

    /**
     * Cleanup method to unbind EasyBind subscriptions when the view is closed.
     */
    public void cleanup() {
        for (final Subscription subscription : this.subscriptions) {
            subscription.unsubscribe();
        }
        this.subscriptions.clear();
    }

    private void setupAdvancedSearch() {
        filteredFilms = new FilteredList<>(tvFilms.getItems(), p -> true);
        sortedFilms = new SortedList<>(filteredFilms);
        sortedFilms.comparatorProperty().bind(tvFilms.comparatorProperty());
        tvFilms.setItems(sortedFilms);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFilms.setPredicate(createSearchPredicate(newValue));
        });
    }

    private Predicate<Film> createSearchPredicate(String searchText) {
        return film -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            long currentTime = System.currentTimeMillis();
            if (currentTime - cacheTimestamp > CACHE_DURATION_MS) {
                refreshSearchCache();
            }

            String actorNames = actorNamesCache.getOrDefault(film.getId(), "");
            String categoryNames = categoryNamesCache.getOrDefault(film.getId(), "");

            return film.getTitle().toLowerCase().contains(lowerCaseFilter)
                || film.getDescription().toLowerCase().contains(lowerCaseFilter)
                || String.valueOf(film.getReleaseYear()).contains(lowerCaseFilter)
                || categoryNames.toLowerCase().contains(lowerCaseFilter)
                || actorNames.toLowerCase().contains(lowerCaseFilter);
        };
    }

    private void refreshSearchCache() {
        actorNamesCache.clear();
        categoryNamesCache.clear();

        FilmCategoryService fcs = new FilmCategoryService();
        ActorFilmService afs = new ActorFilmService();

        if (tvFilms != null && tvFilms.getItems() != null) {
            for (Film film : tvFilms.getItems()) {
                if (film.getId() != null) {
                    actorNamesCache.put(film.getId(), afs.getActorsNames(film.getId()));
                    categoryNamesCache.put(film.getId(), fcs.getCategoryNames(film.getId()));
                }
            }
        }

        cacheTimestamp = System.currentTimeMillis();
        LOGGER.info("Search cache refreshed with " + actorNamesCache.size() + " films");
    }

    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void importFilmImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        final File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (!validateImage(selectedFile)) {
                showAlert(
                    "Image invalide. Veuillez sélectionner une image valide (PNG, JPG, JPEG, GIF) de taille inférieure à 5MB.");
                return;
            }

            try {
                CloudinaryStorage storage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = storage.uploadImage(selectedFile);
                final Image selectedImage = new Image(selectedFile.toURI().toString());
                this.imageFilm.setImage(selectedImage);
                LOGGER.info("Image uploaded successfully to Cloudinary: " + cloudinaryImageUrl);
            } catch (IOException e) {
                showAlert("Erreur lors du téléchargement de l'image: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Failed to upload image to Cloudinary", e);
            }
        }
    }

    private boolean validateImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return false;
        }
        final long maxSize = 5 * 1024 * 1024;
        if (imageFile.length() > maxSize) {
            LOGGER.warning("Image file too large: " + imageFile.length() + " bytes");
            return false;
        }
        String fileName = imageFile.getName().toLowerCase();
        boolean validExtension = fileName.endsWith(".png") || fileName.endsWith(".jpg") ||
            fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
        if (!validExtension) {
            LOGGER.warning("Invalid image extension: " + fileName);
        }
        return validExtension;
    }

    void deleteFilm(final Long id) {
        final FilmService fs = new FilmService();
        fs.delete(new Film(id));
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Film supprimée");
        alert.setContentText("Film supprimée !");
        alert.show();
        this.readFilmTable();
    }

    @FXML
    void insertFilm(final ActionEvent event) {
        try {
            if (this.tfNom.getText().isEmpty()) {
                this.showAlert("Le nom du film est requis.");
                return;
            }
            if (imageFilm.getImage() == null || cloudinaryImageUrl == null || cloudinaryImageUrl.isEmpty()) {
                this.showAlert("Une image pour le film est requise.");
                return;
            }

            if (this.tfDuree.getText().isEmpty()) {
                this.showAlert("La durée du film est requise.");
                return;
            }

            int durationMin;
            try {
                // Assuming input is in minutes as integer, or handle HH:MM:SS if needed.
                // The previous code parsed HH:MM:SS. Let's keep it if that's the requirement,
                // but usually duration in minutes is an int.
                // If the user enters "120", it's 120 minutes.
                // If the user enters "02:00:00", it's 120 minutes.
                if (this.tfDuree.getText().contains(":")) {
                    LocalTime time = LocalTime.parse(this.tfDuree.getText());
                    durationMin = time.getHour() * 60 + time.getMinute();
                } else {
                    durationMin = Integer.parseInt(this.tfDuree.getText());
                }
            } catch (Exception e) {
                this.showAlert("Format de durée invalide. Utilisez des minutes (ex: 120) ou HH:MM:SS.");
                return;
            }

            if (this.taDescription.getText().isEmpty()) {
                this.showAlert("La description du film est requise.");
                return;
            }

            if (this.tfReleaseYear.getText().isEmpty()
                || !this.tfReleaseYear.getText().matches("\\d{4}")) {
                this.showAlert("L'année de réalisation du film est requise au format (YYYY).");
                return;
            }

            int year = Integer.parseInt(tfReleaseYear.getText());
            int currentYear = Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                this.showAlert(String.format(
                    "L'année doit être entre 1900 et %d.", currentYear));
                return;
            }

            if (this.checkComboBoxCategory.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins une catégorie doit être sélectionnée.");
                return;
            }

            if (this.checkComboBoxActor.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins un acteur doit être sélectionné.");
                return;
            }

            if (this.checkComboBoxCinema.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins un cinéma doit être sélectionné.");
                return;
            }

            Film newFilm = new Film(
                this.tfNom.getText(),
                cloudinaryImageUrl,
                durationMin,
                this.taDescription.getText(),
                year,
                ""); // trailerUrl default empty

            final FilmService filmService = new FilmService();
            filmService.create(newFilm);

            final FilmCategoryService filmCategoryService = new FilmCategoryService();
            filmCategoryService.createFilmCategoryAssociation(
                newFilm,
                this.checkComboBoxCategory.getCheckModel().getCheckedItems());

            final ActorFilmService actorFilmService = new ActorFilmService();
            actorFilmService.createFilmActorAssociation(
                newFilm,
                this.checkComboBoxActor.getCheckModel().getCheckedItems());

            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film ajouté");
            alert.setContentText("Le film a été ajouté avec succès !");
            alert.show();
            this.readFilmTable();
            this.clear();
        } catch (IllegalArgumentException e) {
            this.showAlert("Erreur de validation : " + e.getMessage());
            LOGGER.log(Level.WARNING, "Validation error during film insertion", e);
        } catch (final Exception e) {
            this.showAlert("Erreur lors de l'ajout du film : " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error inserting film", e);
        }

    }

    public void switchForm(final ActionEvent event) {
        if (event.getSource() == this.AjouterFilm_Button) {
            this.filmCrudInterface.setVisible(true);
        }

    }

    void clear() {
        this.tfNom.setText("");
        this.tfDuree.setText("");
        this.taDescription.setText("");
        this.tfReleaseYear.setText("");
        if (this.imageFilm != null) {
            this.imageFilm.setImage(null);
        }
        this.cloudinaryImageUrl = null;
    }

    void updateActorlist() {
        final CheckComboBox<Actor> checkComboBox = new CheckComboBox<>(FXCollections.observableArrayList());
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Actor>() {
            @Override
            public void onChanged(final Change<? extends Actor> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (final Actor actor : c.getAddedSubList()) {
                            final String sql = "INSERT INTO film_actor (actor_id, film_id) VALUES (?, ?)";
                        }
                    }
                    if (c.wasRemoved()) {
                        for (final Actor actor : c.getRemoved()) {
                            final String sql = "DELETE FROM film_actor WHERE actor_id = ?";
                        }
                    }
                }
            }
        });
    }

    void readFilmTable() {
        try {
            this.tvFilms.setEditable(true);
            this.setupCellValueFactory();
            this.setupCellFactory();
            this.setupCellOnEditCommit();
            final FilmService filmService = new FilmService();
            PageRequest filmPageRequest = PageRequest.defaultPage();
            final ObservableList<Film> obF = FXCollections
                .observableArrayList(filmService.read(filmPageRequest).getContent());
            this.tvFilms.setItems(obF);
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    private void setupCellFactory() {
        // this.idFilm_tableColumn.setVisible(false); // Removed as column is removed
        this.colTitre.setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
            @Override
            public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("nom", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("nom");
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                } else if (!Character.isUpperCase(input.charAt(0))) {
                                    c.error("Please start with an uppercase letter.");
                                }
                            }).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(final ObservableValue<? extends String> observable,
                                                    final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        this.colDate
            .setCellFactory(new Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>>() {
                @Override
                public TableCell<Film, Integer> call(final TableColumn<Film, Integer> param) {
                    return new TextFieldTableCell<Film, Integer>(new IntegerStringConverter()) {
                        private Validator validator;

                        @Override
                        public void startEdit() {
                            super.startEdit();
                            final TextField textField = (TextField) this.getGraphic();
                            if (null != textField && null == validator) {
                                this.validator = new Validator();
                                this.validator.createCheck().dependsOn("annederalisation", textField.textProperty())
                                    .withMethod(c -> {
                                        final String input = c.get("annederalisation");
                                        if (null == input || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else {
                                            try {
                                                final int year = Integer.parseInt(input);
                                                if (1800 > year || year > Year.now().getValue()) {
                                                    c.error("Please enter a year between 1800 and "
                                                        + Year.now().getValue());
                                                }
                                            } catch (final NumberFormatException e) {
                                                c.error("Please enter a valid year.");
                                            }
                                        }
                                    }).decorates(textField).immediate();
                                final Window window = getScene().getWindow();
                                final Tooltip tooltip = new Tooltip();
                                final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                textField.textProperty().addListener(new ChangeListener<String>() {
                                    @Override
                                    public void changed(final ObservableValue<? extends String> observable,
                                                        final String oldValue, final String newValue) {
                                        if (validator.containsErrors()) {
                                            tooltip.setText(validator.createStringBinding().getValue());
                                            tooltip.setStyle("-fx-background-color: #f00;");
                                            textField.setTooltip(tooltip);
                                            textField.getTooltip().show(window, bounds.getMinX(),
                                                bounds.getMinY() - 30);
                                        } else {
                                            if (null != textField.getTooltip()) {
                                                textField.getTooltip().hide();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                }
            });
        this.colDuree.setCellFactory(new Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>>() {
            @Override
            public TableCell<Film, Integer> call(final TableColumn<Film, Integer> filmcategoryTimeTableColumn) {
                return new TextFieldTableCell<Film, Integer>(new IntegerStringConverter()) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("duree", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("duree");
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                } else {
                                    try {
                                        Integer.parseInt(input);
                                    } catch (NumberFormatException e) {
                                        c.error("Invalid number format. Please enter duration in minutes.");
                                    }
                                }
                            }).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(final ObservableValue<? extends String> observable,
                                                    final String oldValue, final String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        this.colDescription
            .setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
                @Override
                public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                    return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                        private Validator validator;

                        @Override
                        public void startEdit() {
                            super.startEdit();
                            final TextField textField = (TextField) this.getGraphic();
                            if (null != textField && null == validator) {
                                this.validator = new Validator();
                                this.validator.createCheck().dependsOn("description", textField.textProperty())
                                    .withMethod(c -> {
                                        final String input = c.get("description");
                                        if (null == input || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!Character.isUpperCase(input.charAt(0))) {
                                            c.error("Please start with an uppercase letter.");
                                        }
                                    }).decorates(textField).immediate();
                                final Window window = getScene().getWindow();
                                final Tooltip tooltip = new Tooltip();
                                final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                textField.textProperty().addListener(new ChangeListener<String>() {
                                    @Override
                                    public void changed(final ObservableValue<? extends String> observable,
                                                        final String oldValue, final String newValue) {
                                        if (validator.containsErrors()) {
                                            tooltip.setText(validator.createStringBinding().getValue());
                                            tooltip.setStyle("-fx-background-color: #f00;");
                                            textField.setTooltip(tooltip);
                                            textField.getTooltip().show(window, bounds.getMinX(),
                                                bounds.getMinY() - 30);
                                        } else {
                                            if (null != textField.getTooltip()) {
                                                textField.getTooltip().hide();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                }
            });
    }

    private void setupCellValueFactory() {
        this.colAction.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Film, Button>, ObservableValue<Button>>() {
                @Override
                public ObservableValue<Button> call(
                    final TableColumn.CellDataFeatures<Film, Button> filmcategoryButtonCellDataFeatures) {
                    final Button button = new Button("delete");
                    button.getStyleClass().add("sale");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent event) {
                            deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                        }
                    });
                    return new SimpleObjectProperty<Button>(button);
                }
            });
        this.colDate.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(
                    final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                    return new SimpleIntegerProperty(
                        filmcategoryIntegerCellDataFeatures.getValue().getReleaseYear()).asObject();
                }
            });
        this.colDuree
            .setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    @Override
                    public ObservableValue<Integer> call(
                        final TableColumn.CellDataFeatures<Film, Integer> filmcategoryTimeCellDataFeatures) {
                        return new SimpleObjectProperty<Integer>(
                            filmcategoryTimeCellDataFeatures.getValue().getDurationMin());
                    }
                });
        this.colImage
            .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, HBox>, ObservableValue<HBox>>() {
                @Override
                public ObservableValue<HBox> call(final TableColumn.CellDataFeatures<Film, HBox> param) {
                    final HBox hBox = new HBox();
                    try {
                        final ImageView imageView = new ImageView(new Image(param.getValue().getImageUrl()));
                        hBox.getChildren().add(imageView);
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(50);
                        hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(final MouseEvent event) {
                                try {
                                    final FileChooser fileChooser = new FileChooser();
                                    fileChooser.getExtensionFilters().addAll(
                                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                                        new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                    final File file = fileChooser.showOpenDialog(new Stage());
                                    if (null != file) {
                                        final Image image = new Image(file.toURI().toURL().toString());
                                        imageView.setImage(image);
                                        hBox.getChildren().clear();
                                        hBox.getChildren().add(imageView);
                                        final Film film = param.getValue();
                                        film.setImageUrl(file.toURI().toURL().toString());
                                        updateFilm(film);
                                    }
                                } catch (final Exception e) {
                                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                }
                            }
                        });
                    } catch (final Exception e) {
                        FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                    return new SimpleObjectProperty<HBox>(hBox);
                }
            });
        this.colDescription.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(
                    final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                    return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getDescription());
                }
            });
        this.colCategorie.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(
                    final TableColumn.CellDataFeatures<Film, String> p) {
                    // Simplified to just return string for now as FXML defines it as text column
                    // If we want CheckComboBox, we need to change FXML or use custom cell factory.
                    // For now, let's just show category names.
                    final FilmCategoryService fcs = new FilmCategoryService();
                    return new SimpleStringProperty(fcs.getCategoryNames(p.getValue().getId()));
                }
            });
        this.colTitre.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(
                    final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                    return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getTitle());
                }
            });
    }

    private void setupCellOnEditCommit() {
        this.colDate
            .setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Integer>>() {
                @Override
                public void handle(final TableColumn.CellEditEvent<Film, Integer> event) {
                    try {
                        event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setReleaseYear(event.getNewValue());
                        FilmController.this
                            .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                    } catch (final Exception e) {
                        FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            });
        this.colTitre.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setTitle(event.getNewValue());
                    FilmController.this
                        .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.colDescription.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setDescription(event.getNewValue());
                    FilmController.this
                        .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.colDuree.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Integer>>() {
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, Integer> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setDurationMin(event.getNewValue());
                    FilmController.this
                        .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    void updateFilm(final Film film) {
        try {
            final FilmService fs = new FilmService();
            fs.update(film);
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film modifiée");
            alert.setContentText("Film modifiée !");
            alert.show();
            this.readFilmTable();
        } catch (final Exception e) {
            this.showAlert("Erreur lors de la modification du Film : " + e.getMessage());
        }
        this.readFilmTable();
    }

    @FXML
    void importImage(final ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.setTitle("Sélectionner une image");
            final File selectedFile = fileChooser.showOpenDialog(null);
            if (null != selectedFile) {
                try {
                    CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                    cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);
                    final Image selectedImage = new Image(cloudinaryImageUrl);
                    this.imageFilm.setImage(selectedImage);
                    LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
                } catch (final IOException e) {
                    LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error in file chooser", e);
        }
    }

    @FXML
    public void switchtoajouterCinema(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.ajouterCinema_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Toggle 3D preview mode for film posters and trailers
     */
    @FXML
    public void toggle3DPreview(final ActionEvent event) {
        if (is3DPreviewActive) {
            disable3DPreview();
        } else {
            enable3DPreview();
        }
    }

    /**
     * Enable 3D preview mode with animated film posters
     */
    private void enable3DPreview() {
        if (preview3DContainer == null) {
            LOGGER.warning("3D Preview container not found in FXML");
            return;
        }

        try {
            // Create 3D scene components
            root3D = new Group();
            camera3D = new PerspectiveCamera(true);
            camera3D.setTranslateZ(-800);
            camera3D.setNearClip(0.1);
            camera3D.setFarClip(2000.0);

            // Create 3D film poster display
            create3DFilmDisplay();

            // Create SubScene for 3D content
            subScene3D = new SubScene(root3D, 600, 400, true, SceneAntialiasing.BALANCED);
            subScene3D.setFill(Color.BLACK);
            subScene3D.setCamera(camera3D);

            // Add to container
            preview3DContainer.getChildren().clear();
            preview3DContainer.getChildren().add(subScene3D);
            preview3DContainer.setVisible(true);

            // Start rotation animation
            startRotationAnimation();

            is3DPreviewActive = true;
            if (preview3D_Button != null) {
                preview3D_Button.setText("Exit 3D Preview");
            }

            LOGGER.info("3D Preview mode enabled");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enabling 3D preview", e);
            showAlert("3D Preview Error: " + e.getMessage());
        }
    }

    /**
     * Create 3D film display with rotating posters
     */
    private void create3DFilmDisplay() {
        // Get current films from table
        ObservableList<Film> films = tvFilms.getItems();
        if (films == null || films.isEmpty()) {
            // Create sample 3D objects if no films
            createSample3DObjects();
            return;
        }

        double radius = 200;
        int filmCount = Math.min(films.size(), 8); // Limit to 8 films for performance

        for (int i = 0; i < filmCount; i++) {
            Film film = films.get(i);
            double angle = (2 * Math.PI * i) / filmCount;

            // Create 3D poster plane
            Box posterBox = new Box(80, 120, 2);

            // Create material with film poster
            PhongMaterial material = new PhongMaterial();
            try {
                if (film.getImageUrl() != null && !film.getImageUrl().isEmpty()) {
                    Image posterImage = new Image(film.getImageUrl(), 80, 120, true, true);
                    material.setDiffuseMap(posterImage);
                } else {
                    material.setDiffuseColor(Color.DARKBLUE);
                }
            } catch (Exception e) {
                material.setDiffuseColor(Color.DARKBLUE);
            }

            material.setSpecularColor(Color.WHITE);
            posterBox.setMaterial(material);

            // Position in circle
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            posterBox.setTranslateX(x);
            posterBox.setTranslateZ(z);

            // Rotate to face center
            posterBox.getTransforms().add(new Rotate(-Math.toDegrees(angle), Rotate.Y_AXIS));

            // Add hover effects
            posterBox.setOnMouseEntered(e -> {
                posterBox.setScaleX(1.2);
                posterBox.setScaleY(1.2);
                posterBox.setScaleZ(1.2);
            });

            posterBox.setOnMouseExited(e -> {
                posterBox.setScaleX(1.0);
                posterBox.setScaleY(1.0);
                posterBox.setScaleZ(1.0);
            });

            // Add click handler for film selection
            posterBox.setOnMouseClicked(e -> {
                selectFilmFor3DView(film);
            });

            root3D.getChildren().add(posterBox);
        }

        // Add central title display
        addCentral3DTitle();
    }

    /**
     * Create sample 3D objects when no films are available
     */
    private void createSample3DObjects() {
        // Create sample geometric shapes
        Sphere sphere = new Sphere(50);
        PhongMaterial sphereMaterial = new PhongMaterial();
        sphereMaterial.setDiffuseColor(Color.BLUE);
        sphereMaterial.setSpecularColor(Color.LIGHTBLUE);
        sphere.setMaterial(sphereMaterial);
        sphere.setTranslateX(-100);

        Cylinder cylinder = new Cylinder(40, 80);
        PhongMaterial cylinderMaterial = new PhongMaterial();
        cylinderMaterial.setDiffuseColor(Color.RED);
        cylinderMaterial.setSpecularColor(Color.PINK);
        cylinder.setMaterial(cylinderMaterial);
        cylinder.setTranslateX(100);

        Box box = new Box(60, 60, 60);
        PhongMaterial boxMaterial = new PhongMaterial();
        boxMaterial.setDiffuseColor(Color.GREEN);
        boxMaterial.setSpecularColor(Color.LIGHTGREEN);
        box.setMaterial(boxMaterial);

        root3D.getChildren().addAll(sphere, cylinder, box);
    }

    /**
     * Add central 3D title display
     */
    private void addCentral3DTitle() {
        // Create a central display element
        Cylinder centralPillar = new Cylinder(20, 300);
        PhongMaterial pillarMaterial = new PhongMaterial();
        pillarMaterial.setDiffuseColor(Color.GOLD);
        pillarMaterial.setSpecularColor(Color.YELLOW);
        centralPillar.setMaterial(pillarMaterial);
        centralPillar.setTranslateY(-50);

        root3D.getChildren().add(centralPillar);
    }

    /**
     * Start rotation animation for 3D scene
     */
    private void startRotationAnimation() {
        if (rotationTimer != null) {
            rotationTimer.stop();
        }

        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        root3D.getTransforms().add(rotateY);

        rotationTimer = new AnimationTimer() {
            private long lastTime = 0;
            private double rotation = 0;

            @Override
            public void handle(long now) {
                if (lastTime != 0) {
                    double deltaTime = (now - lastTime) / 1_000_000_000.0;
                    rotation += deltaTime * 20; // 20 degrees per second
                    rotateY.setAngle(rotation);
                }
                lastTime = now;
            }
        };
        rotationTimer.start();
    }

    /**
     * Handle film selection in 3D view
     */
    private void selectFilmFor3DView(Film film) {
        // Highlight selected film in table
        tvFilms.getSelectionModel().select(film);

        // Show film details
        showAlert("Selected Film: " + film.getTitle() + "\n" +
            "Duration: " + film.getDurationMin() + " minutes\n" +
            "Year: " + film.getReleaseYear());

        LOGGER.info("Selected film in 3D view: " + film.getTitle());
    }

    /**
     * Disable 3D preview mode
     */
    private void disable3DPreview() {
        try {
            if (rotationTimer != null) {
                rotationTimer.stop();
                rotationTimer = null;
            }

            if (preview3DContainer != null) {
                preview3DContainer.getChildren().clear();
                preview3DContainer.setVisible(false);
            }

            is3DPreviewActive = false;
            if (preview3D_Button != null) {
                preview3D_Button.setText("3D Preview");
            }

            LOGGER.info("3D Preview mode disabled");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error disabling 3D preview", e);
        }
    }

    /**
     * Create enhanced 3D film poster with animations
     */
    public void create3DFilmPoster(Film film, double x, double y, double z) {
        if (root3D == null) return;

        // Create 3D poster frame
        Group posterGroup = new Group();

        // Main poster plane
        Box poster = new Box(100, 140, 5);
        PhongMaterial posterMaterial = new PhongMaterial();

        try {
            if (film.getImageUrl() != null && !film.getImageUrl().isEmpty()) {
                Image posterImage = new Image(film.getImageUrl(), 100, 140, true, true);
                posterMaterial.setDiffuseMap(posterImage);
            } else {
                posterMaterial.setDiffuseColor(Color.DARKSLATEGRAY);
            }
        } catch (Exception e) {
            posterMaterial.setDiffuseColor(Color.DARKSLATEGRAY);
        }

        posterMaterial.setSpecularColor(Color.WHITE);
        poster.setMaterial(posterMaterial);

        // Add frame
        Box frame = new Box(110, 150, 3);
        PhongMaterial frameMaterial = new PhongMaterial();
        frameMaterial.setDiffuseColor(Color.GOLD);
        frameMaterial.setSpecularColor(Color.YELLOW);
        frame.setMaterial(frameMaterial);
        frame.setTranslateZ(-4);

        posterGroup.getChildren().addAll(frame, poster);
        posterGroup.setTranslateX(x);
        posterGroup.setTranslateY(y);
        posterGroup.setTranslateZ(z);

        // Add floating animation
        Timeline floatAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(posterGroup.translateYProperty(), y)),
            new KeyFrame(Duration.seconds(2), new KeyValue(posterGroup.translateYProperty(), y - 20)),
            new KeyFrame(Duration.seconds(4), new KeyValue(posterGroup.translateYProperty(), y))
        );
        floatAnimation.setCycleCount(Timeline.INDEFINITE);
        floatAnimation.setAutoReverse(true);
        floatAnimation.play();

        root3D.getChildren().add(posterGroup);
    }
}
