package com.esprit.controllers.ar;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.ActorService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.PageRequest;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j2;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for AR Preview functionality that provides immersive movie and cinema experiences.
 * <p>
 * This controller integrates WebView with Three.js for AR/3D content, creates 360-degree theater tours,
 * and provides interactive hotspots for actors and films following existing web integration patterns.
 * <p>
 * Features:
 * - 360-degree theater tours using existing Cinema model
 * - Interactive AR movie trailers and posters
 * - Actor information hotspots using existing Actor model
 * - Film details integration using existing Film model
 * - WebView integration following existing web patterns
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class ARPreviewController implements Initializable {

    @FXML
    private WebView arWebView;
    @FXML
    private VBox controlPanel;
    @FXML
    private ComboBox<Cinema> cinemaSelector;
    @FXML
    private ComboBox<Film> filmSelector;
    @FXML
    private Button startTourButton;
    @FXML
    private Button showTrailerButton;
    @FXML
    private Button showActorInfoButton;
    @FXML
    private Button exitARButton;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressIndicator loadingIndicator;

    private WebEngine webEngine;
    private CinemaService cinemaService;
    private FilmService filmService;
    private ActorService actorService;
    private Cinema selectedCinema;
    private Film selectedFilm;
    private boolean isARActive = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupWebView();
        loadCinemaData();
        loadFilmData();
        setupEventHandlers();

        // Initially hide loading indicator
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }

        updateStatus("AR Preview ready. Select a cinema or film to begin.");
    }

    /**
     * Initialize service dependencies following existing service patterns
     */
    private void initializeServices() {
        cinemaService = new CinemaService();
        filmService = new FilmService();
        actorService = new ActorService();
    }

    /**
     * Setup WebView for AR content following existing web integration patterns
     */
    private void setupWebView() {
        if (arWebView == null) {
            log.warn("AR WebView not found in FXML");
            return;
        }

        webEngine = arWebView.getEngine();

        // Enable JavaScript
        webEngine.setJavaScriptEnabled(true);

        // Setup loading state handlers
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            Platform.runLater(() -> {
                if (loadingIndicator != null) {
                    loadingIndicator.setVisible(newState == Worker.State.RUNNING);
                }

                if (newState == Worker.State.SUCCEEDED) {
                    updateStatus("AR environment loaded successfully");
                    setupJavaScriptBridge();
                } else if (newState == Worker.State.FAILED) {
                    updateStatus("Failed to load AR environment");
                    log.error("Failed to load AR content");
                }
            });
        });

        // Load initial AR HTML content
        loadAREnvironment();
    }

    /**
     * Load cinema data into selector following existing Cinema model patterns
     */
    private void loadCinemaData() {
        if (cinemaSelector == null) return;

        try {
            PageRequest pageRequest = PageRequest.defaultPage();
            List<Cinema> cinemas = cinemaService.read(pageRequest).getContent();

            Platform.runLater(() -> {
                cinemaSelector.getItems().clear();
                cinemaSelector.getItems().addAll(cinemas);

                // Set display format
                cinemaSelector.setCellFactory(listView -> new javafx.scene.control.ListCell<Cinema>() {
                    @Override
                    protected void updateItem(Cinema cinema, boolean empty) {
                        super.updateItem(cinema, empty);
                        if (empty || cinema == null) {
                            setText(null);
                        } else {
                            setText(cinema.getName() + " - " + cinema.getAddress());
                        }
                    }
                });

                cinemaSelector.setButtonCell(new javafx.scene.control.ListCell<Cinema>() {
                    @Override
                    protected void updateItem(Cinema cinema, boolean empty) {
                        super.updateItem(cinema, empty);
                        if (empty || cinema == null) {
                            setText("Select Cinema");
                        } else {
                            setText(cinema.getName());
                        }
                    }
                });
            });

        } catch (Exception e) {
            log.error("Error loading cinema data", e);
            updateStatus("Error loading cinema data");
        }
    }

    /**
     * Load film data into selector following existing Film model patterns
     */
    private void loadFilmData() {
        if (filmSelector == null) return;

        try {
            PageRequest pageRequest = PageRequest.defaultPage();
            List<Film> films = filmService.read(pageRequest).getContent();

            Platform.runLater(() -> {
                filmSelector.getItems().clear();
                filmSelector.getItems().addAll(films);

                // Set display format
                filmSelector.setCellFactory(listView -> new javafx.scene.control.ListCell<Film>() {
                    @Override
                    protected void updateItem(Film film, boolean empty) {
                        super.updateItem(film, empty);
                        if (empty || film == null) {
                            setText(null);
                        } else {
                            setText(film.getTitle() + " (" + film.getReleaseYear() + ")");
                        }
                    }
                });

                filmSelector.setButtonCell(new javafx.scene.control.ListCell<Film>() {
                    @Override
                    protected void updateItem(Film film, boolean empty) {
                        super.updateItem(film, empty);
                        if (empty || film == null) {
                            setText("Select Film");
                        } else {
                            setText(film.getTitle());
                        }
                    }
                });
            });

        } catch (Exception e) {
            log.error("Error loading film data", e);
            updateStatus("Error loading film data");
        }
    }

    /**
     * Setup event handlers for UI controls
     */
    private void setupEventHandlers() {
        if (cinemaSelector != null) {
            cinemaSelector.setOnAction(e -> {
                selectedCinema = cinemaSelector.getSelectionModel().getSelectedItem();
                if (selectedCinema != null) {
                    updateStatus("Selected cinema: " + selectedCinema.getName());
                    enableTourButton();
                }
            });
        }

        if (filmSelector != null) {
            filmSelector.setOnAction(e -> {
                selectedFilm = filmSelector.getSelectionModel().getSelectedItem();
                if (selectedFilm != null) {
                    updateStatus("Selected film: " + selectedFilm.getTitle());
                    enableFilmButtons();
                }
            });
        }
    }

    /**
     * Load AR environment HTML with Three.js integration
     */
    private void loadAREnvironment() {
        String arHTML = generateARHTML();
        webEngine.loadContent(arHTML);
    }

    /**
     * Generate HTML content for AR environment with Three.js
     */
    private String generateARHTML() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>RAKCHA AR Preview</title>
                <style>
                    body { margin: 0; padding: 0; overflow: hidden; background: #000; }
                    #ar-container { width: 100vw; height: 100vh; position: relative; }
                    #info-panel {
                        position: absolute;
                        top: 10px;
                        left: 10px;
                        background: rgba(0,0,0,0.8);
                        color: white;
                        padding: 15px;
                        border-radius: 8px;
                        font-family: Arial, sans-serif;
                        max-width: 300px;
                        z-index: 100;
                    }
                    #hotspot {
                        position: absolute;
                        width: 30px;
                        height: 30px;
                        background: #ff6b35;
                        border-radius: 50%;
                        cursor: pointer;
                        animation: pulse 2s infinite;
                        z-index: 50;
                    }
                    @keyframes pulse {
                        0% { transform: scale(1); opacity: 1; }
                        50% { transform: scale(1.2); opacity: 0.7; }
                        100% { transform: scale(1); opacity: 1; }
                    }
                    .hotspot-tooltip {
                        position: absolute;
                        background: rgba(0,0,0,0.9);
                        color: white;
                        padding: 8px 12px;
                        border-radius: 4px;
                        font-size: 12px;
                        white-space: nowrap;
                        pointer-events: none;
                        z-index: 101;
                    }
                </style>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/controls/OrbitControls.js"></script>
            </head>
            <body>
                <div id="ar-container">
                    <div id="info-panel">
                        <h3>RAKCHA AR Preview</h3>
                        <p id="info-text">Welcome to the AR experience!</p>
                    </div>
                </div>

                <script>
                    let scene, camera, renderer, controls;
                    let currentEnvironment = null;
                    let hotspots = [];

                    function initAR() {
                        // Create scene
                        scene = new THREE.Scene();
                        scene.background = new THREE.Color(0x000000);

                        // Create camera
                        camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
                        camera.position.set(0, 5, 10);

                        // Create renderer
                        renderer = new THREE.WebGLRenderer({ antialias: true });
                        renderer.setSize(window.innerWidth, window.innerHeight);
                        renderer.shadowMap.enabled = true;
                        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
                        document.getElementById('ar-container').appendChild(renderer.domElement);

                        // Add controls
                        controls = new THREE.OrbitControls(camera, renderer.domElement);
                        controls.enableDamping = true;
                        controls.dampingFactor = 0.05;

                        // Add lighting
                        const ambientLight = new THREE.AmbientLight(0x404040, 0.6);
                        scene.add(ambientLight);

                        const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
                        directionalLight.position.set(10, 10, 5);
                        directionalLight.castShadow = true;
                        scene.add(directionalLight);

                        // Start render loop
                        animate();

                        updateInfo('AR Environment initialized. Ready for content.');
                    }

                    function animate() {
                        requestAnimationFrame(animate);
                        controls.update();
                        renderer.render(scene, camera);
                    }

                    function create360TheaterTour(cinemaData) {
                        clearScene();

                        // Create theater environment
                        const geometry = new THREE.SphereGeometry(50, 32, 16);
                        const material = new THREE.MeshBasicMaterial({
                            map: createTheaterTexture(),
                            side: THREE.BackSide
                        });
                        const theater = new THREE.Mesh(geometry, material);
                        scene.add(theater);

                        // Add theater seats
                        createTheaterSeats();

                        // Add screen
                        createTheaterScreen();

                        // Add interactive hotspots
                        addTheaterHotspots(cinemaData);

                        updateInfo('360° Theater Tour: ' + cinemaData.name);
                    }

                    function createTheaterTexture() {
                        const canvas = document.createElement('canvas');
                        canvas.width = 1024;
                        canvas.height = 512;
                        const ctx = canvas.getContext('2d');

                        // Create gradient background
                        const gradient = ctx.createLinearGradient(0, 0, 0, 512);
                        gradient.addColorStop(0, '#1a1a2e');
                        gradient.addColorStop(0.5, '#16213e');
                        gradient.addColorStop(1, '#0f3460');

                        ctx.fillStyle = gradient;
                        ctx.fillRect(0, 0, 1024, 512);

                        return new THREE.CanvasTexture(canvas);
                    }

                    function createTheaterSeats() {
                        const seatGeometry = new THREE.BoxGeometry(0.8, 0.5, 0.8);
                        const seatMaterial = new THREE.MeshLambertMaterial({ color: 0x8b0000 });

                        for (let row = 0; row < 8; row++) {
                            for (let seat = 0; seat < 12; seat++) {
                                if (seat === 6) continue; // Aisle

                                const seatMesh = new THREE.Mesh(seatGeometry, seatMaterial);
                                seatMesh.position.set(
                                    (seat - 6) * 1.2 + (seat > 6 ? 1.2 : 0),
                                    -2,
                                    (row - 4) * 1.5
                                );
                                scene.add(seatMesh);
                            }
                        }
                    }

                    function createTheaterScreen() {
                        const screenGeometry = new THREE.PlaneGeometry(20, 12);
                        const screenMaterial = new THREE.MeshLambertMaterial({
                            color: 0xffffff,
                            emissive: 0x222222
                        });
                        const screen = new THREE.Mesh(screenGeometry, screenMaterial);
                        screen.position.set(0, 2, -25);
                        scene.add(screen);
                    }

                    function addTheaterHotspots(cinemaData) {
                        // Add hotspot for cinema information
                        addHotspot(-15, 5, -20, 'Cinema Info', 'Click for cinema details');

                        // Add hotspot for facilities
                        addHotspot(15, 5, -20, 'Facilities', 'View cinema facilities');

                        // Add hotspot for screen technology
                        addHotspot(0, 8, -24, 'Screen Tech', 'Learn about our screen technology');
                    }

                    function showARTrailer(filmData) {
                        clearScene();

                        // Create AR movie poster
                        const posterGeometry = new THREE.PlaneGeometry(6, 9);
                        const posterMaterial = new THREE.MeshLambertMaterial({
                            map: loadFilmPoster(filmData.imageUrl),
                            transparent: true
                        });
                        const poster = new THREE.Mesh(posterGeometry, posterMaterial);
                        poster.position.set(0, 0, -5);
                        scene.add(poster);

                        // Add floating elements
                        createFloatingElements(filmData);

                        // Add actor hotspots
                        addActorHotspots(filmData.actors);

                        updateInfo('AR Trailer: ' + filmData.title);
                    }

                    function loadFilmPoster(imageUrl) {
                        if (imageUrl && imageUrl.trim() !== '') {
                            const loader = new THREE.TextureLoader();
                            return loader.load(imageUrl,
                                function(texture) {
                                    console.log('Poster loaded successfully');
                                },
                                function(progress) {
                                    console.log('Loading poster: ' + (progress.loaded / progress.total * 100) + '%');
                                },
                                function(error) {
                                    console.error('Error loading poster:', error);
                                }
                            );
                        } else {
                            // Create default poster texture
                            const canvas = document.createElement('canvas');
                            canvas.width = 300;
                            canvas.height = 450;
                            const ctx = canvas.getContext('2d');

                            ctx.fillStyle = '#1a1a2e';
                            ctx.fillRect(0, 0, 300, 450);

                            ctx.fillStyle = '#ffffff';
                            ctx.font = '24px Arial';
                            ctx.textAlign = 'center';
                            ctx.fillText('MOVIE', 150, 200);
                            ctx.fillText('POSTER', 150, 250);

                            return new THREE.CanvasTexture(canvas);
                        }
                    }

                    function createFloatingElements(filmData) {
                        // Create floating particles
                        const particleGeometry = new THREE.SphereGeometry(0.1, 8, 6);
                        const particleMaterial = new THREE.MeshBasicMaterial({
                            color: 0xff6b35,
                            transparent: true,
                            opacity: 0.7
                        });

                        for (let i = 0; i < 20; i++) {
                            const particle = new THREE.Mesh(particleGeometry, particleMaterial);
                            particle.position.set(
                                (Math.random() - 0.5) * 20,
                                (Math.random() - 0.5) * 10,
                                (Math.random() - 0.5) * 10
                            );
                            scene.add(particle);

                            // Animate particles
                            animateParticle(particle);
                        }
                    }

                    function animateParticle(particle) {
                        const startY = particle.position.y;
                        const animate = () => {
                            particle.position.y = startY + Math.sin(Date.now() * 0.001 + particle.position.x) * 2;
                            particle.rotation.y += 0.01;
                            requestAnimationFrame(animate);
                        };
                        animate();
                    }

                    function addActorHotspots(actors) {
                        if (!actors || actors.length === 0) return;

                        actors.slice(0, 3).forEach((actor, index) => {
                            const x = (index - 1) * 8;
                            addHotspot(x, -3, 0, actor.name, 'Click for actor information');
                        });
                    }

                    function addHotspot(x, y, z, title, description) {
                        const hotspotGeometry = new THREE.SphereGeometry(0.3, 16, 12);
                        const hotspotMaterial = new THREE.MeshBasicMaterial({
                            color: 0xff6b35,
                            transparent: true,
                            opacity: 0.8
                        });
                        const hotspot = new THREE.Mesh(hotspotGeometry, hotspotMaterial);
                        hotspot.position.set(x, y, z);
                        hotspot.userData = { title: title, description: description };

                        // Add pulsing animation
                        const animate = () => {
                            const scale = 1 + Math.sin(Date.now() * 0.005) * 0.2;
                            hotspot.scale.set(scale, scale, scale);
                            requestAnimationFrame(animate);
                        };
                        animate();

                        scene.add(hotspot);
                        hotspots.push(hotspot);
                    }

                    function clearScene() {
                        while(scene.children.length > 0) {
                            const child = scene.children[0];
                            scene.remove(child);
                            if (child.geometry) child.geometry.dispose();
                            if (child.material) {
                                if (child.material.map) child.material.map.dispose();
                                child.material.dispose();
                            }
                        }

                        // Re-add lighting
                        const ambientLight = new THREE.AmbientLight(0x404040, 0.6);
                        scene.add(ambientLight);

                        const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
                        directionalLight.position.set(10, 10, 5);
                        scene.add(directionalLight);

                        hotspots = [];
                    }

                    function updateInfo(text) {
                        const infoElement = document.getElementById('info-text');
                        if (infoElement) {
                            infoElement.textContent = text;
                        }
                    }

                    // Handle window resize
                    window.addEventListener('resize', () => {
                        camera.aspect = window.innerWidth / window.innerHeight;
                        camera.updateProjectionMatrix();
                        renderer.setSize(window.innerWidth, window.innerHeight);
                    });

                    // Handle mouse clicks for hotspots
                    renderer.domElement.addEventListener('click', (event) => {
                        const mouse = new THREE.Vector2();
                        mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
                        mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;

                        const raycaster = new THREE.Raycaster();
                        raycaster.setFromCamera(mouse, camera);

                        const intersects = raycaster.intersectObjects(hotspots);
                        if (intersects.length > 0) {
                            const hotspot = intersects[0].object;
                            if (window.javaConnector) {
                                window.javaConnector.onHotspotClick(hotspot.userData.title, hotspot.userData.description);
                            }
                        }
                    });

                    // Initialize AR when page loads
                    initAR();
                </script>
            </body>
            </html>
            """;
    }

    /**
     * Setup JavaScript bridge for communication between WebView and Java
     */
    private void setupJavaScriptBridge() {
        try {
            // Create Java connector object for JavaScript
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaConnector", new JavaScriptBridge());

            log.info("JavaScript bridge established");
        } catch (Exception e) {
            log.error("Error setting up JavaScript bridge", e);
        }
    }

    /**
     * Start 360-degree theater tour using selected cinema
     */
    @FXML
    private void startTheaterTour(ActionEvent event) {
        if (selectedCinema == null) {
            showAlert("No Cinema Selected", "Please select a cinema first.");
            return;
        }

        try {
            updateStatus("Starting theater tour for " + selectedCinema.getName());

            // Prepare cinema data for JavaScript
            String cinemaScript = String.format("""
                    create360TheaterTour({
                        name: '%s',
                        address: '%s',
                        halls: %d
                    });
                    """,
                escapeJavaScript(selectedCinema.getName()),
                escapeJavaScript(selectedCinema.getAddress()),
                selectedCinema.getCinemaHalls() != null ? selectedCinema.getCinemaHalls().size() : 0
            );

            webEngine.executeScript(cinemaScript);
            isARActive = true;

        } catch (Exception e) {
            log.error("Error starting theater tour", e);
            showAlert("Theater Tour Error", "Unable to start theater tour: " + e.getMessage());
        }
    }

    /**
     * Show AR trailer for selected film
     */
    @FXML
    private void showARTrailer(ActionEvent event) {
        if (selectedFilm == null) {
            showAlert("No Film Selected", "Please select a film first.");
            return;
        }

        try {
            updateStatus("Loading AR trailer for " + selectedFilm.getTitle());

            // Prepare film data for JavaScript
            String filmScript = String.format("""
                    showARTrailer({
                        title: '%s',
                        year: %d,
                        imageUrl: '%s',
                        description: '%s',
                        actors: %s
                    });
                    """,
                escapeJavaScript(selectedFilm.getTitle()),
                selectedFilm.getReleaseYear(),
                escapeJavaScript(selectedFilm.getImageUrl() != null ? selectedFilm.getImageUrl() : ""),
                escapeJavaScript(selectedFilm.getDescription() != null ? selectedFilm.getDescription() : ""),
                formatActorsForJS(selectedFilm.getActors())
            );

            webEngine.executeScript(filmScript);
            isARActive = true;

        } catch (Exception e) {
            log.error("Error showing AR trailer", e);
            showAlert("AR Trailer Error", "Unable to show AR trailer: " + e.getMessage());
        }
    }

    /**
     * Show actor information hotspots
     */
    @FXML
    private void showActorInfo(ActionEvent event) {
        if (selectedFilm == null || selectedFilm.getActors() == null || selectedFilm.getActors().isEmpty()) {
            showAlert("No Actor Data", "Please select a film with actor information first.");
            return;
        }

        try {
            // Show actor information in a dialog or overlay
            StringBuilder actorInfo = new StringBuilder("Actors in " + selectedFilm.getTitle() + ":\n\n");

            for (Actor actor : selectedFilm.getActors()) {
                actorInfo.append("• ").append(actor.getName());
                if (actor.getBiography() != null && !actor.getBiography().isEmpty()) {
                    actorInfo.append("\n  ").append(actor.getBiography(), 0, Math.min(100, actor.getBiography().length())).append("...");
                }
                actorInfo.append("\n\n");
            }

            showAlert("Actor Information", actorInfo.toString());

        } catch (Exception e) {
            log.error("Error showing actor info", e);
            showAlert("Actor Info Error", "Unable to show actor information: " + e.getMessage());
        }
    }

    /**
     * Exit AR mode and return to normal view
     */
    @FXML
    private void exitAR(ActionEvent event) {
        try {
            webEngine.executeScript("clearScene();");
            isARActive = false;
            updateStatus("Exited AR mode");

        } catch (Exception e) {
            log.error("Error exiting AR mode", e);
        }
    }

    /**
     * Enable tour button when cinema is selected
     */
    private void enableTourButton() {
        if (startTourButton != null) {
            startTourButton.setDisable(false);
        }
    }

    /**
     * Enable film-related buttons when film is selected
     */
    private void enableFilmButtons() {
        if (showTrailerButton != null) {
            showTrailerButton.setDisable(false);
        }
        if (showActorInfoButton != null) {
            showActorInfoButton.setDisable(selectedFilm.getActors() == null || selectedFilm.getActors().isEmpty());
        }
    }

    /**
     * Show hotspot information when clicked in AR view
     */
    private void showHotspotInfo(String title, String description) {
        updateStatus("Hotspot: " + title + " - " + description);

        // Could also show a more detailed dialog or overlay
        if ("Cinema Info".equals(title) && selectedCinema != null) {
            showCinemaDetails();
        } else if ("Screen Tech".equals(title)) {
            showScreenTechnology();
        } else if ("Facilities".equals(title) && selectedCinema != null) {
            showCinemaFacilities();
        }
    }

    /**
     * Show detailed cinema information
     */
    private void showCinemaDetails() {
        if (selectedCinema == null) return;

        StringBuilder details = new StringBuilder();
        details.append("Cinema: ").append(selectedCinema.getName()).append("\n");
        details.append("Address: ").append(selectedCinema.getAddress()).append("\n");
        details.append("Status: ").append(selectedCinema.getStatus()).append("\n");

        if (selectedCinema.getCinemaHalls() != null) {
            details.append("Halls: ").append(selectedCinema.getCinemaHalls().size()).append("\n");

            for (CinemaHall hall : selectedCinema.getCinemaHalls()) {
                details.append("  • ").append(hall.getName());
                if (hall.getCapacity() != null) {
                    details.append(" (").append(hall.getCapacity()).append(" seats)");
                }
                details.append("\n");
            }
        }

        showAlert("Cinema Details", details.toString());
    }

    /**
     * Show screen technology information
     */
    private void showScreenTechnology() {
        String techInfo = """
            Screen Technology Features:

            • 4K Ultra HD Resolution
            • Dolby Atmos Sound System
            • Premium Large Format Screen
            • Advanced Projection Technology
            • Immersive Audio Experience
            • Crystal Clear Picture Quality
            """;

        showAlert("Screen Technology", techInfo);
    }

    /**
     * Show cinema facilities information
     */
    private void showCinemaFacilities() {
        String facilities = """
            Cinema Facilities:

            • Comfortable Reclining Seats
            • Premium VIP Lounges
            • Concession Stand
            • Parking Available
            • Wheelchair Accessible
            • Air Conditioning
            • Restrooms
            • Emergency Exits
            """;

        showAlert("Cinema Facilities", facilities);
    }

    /**
     * Format actors list for JavaScript consumption
     */
    private String formatActorsForJS(List<Actor> actors) {
        if (actors == null || actors.isEmpty()) {
            return "[]";
        }

        StringBuilder js = new StringBuilder("[");
        for (int i = 0; i < actors.size(); i++) {
            Actor actor = actors.get(i);
            if (i > 0) js.append(",");
            js.append(String.format("""
                    {
                        name: '%s',
                        imageUrl: '%s',
                        biography: '%s'
                    }
                    """,
                escapeJavaScript(actor.getName()),
                escapeJavaScript(actor.getImageUrl() != null ? actor.getImageUrl() : ""),
                escapeJavaScript(actor.getBiography() != null ? actor.getBiography() : "")
            ));
        }
        js.append("]");
        return js.toString();
    }

    /**
     * Escape JavaScript strings to prevent injection
     */
    private String escapeJavaScript(String input) {
        if (input == null) return "";
        return input.replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Update status label
     */
    private void updateStatus(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
        log.info("AR Status: " + message);
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * JavaScript bridge class for handling communication from WebView
     */
    public class JavaScriptBridge {

        public void onHotspotClick(String title, String description) {
            Platform.runLater(() -> {
                showHotspotInfo(title, description);
            });
        }

        public void onEnvironmentLoaded(String environmentType) {
            Platform.runLater(() -> {
                updateStatus(environmentType + " environment loaded");
            });
        }

        public void onError(String error) {
            Platform.runLater(() -> {
                updateStatus("AR Error: " + error);
                log.warn("AR JavaScript error: " + error);
            });
        }
    }
}
