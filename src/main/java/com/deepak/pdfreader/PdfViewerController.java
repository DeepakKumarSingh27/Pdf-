package com.deepak.pdfreader;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfViewerController {

    @FXML private ImageView pdfImageView;
    @FXML private Label statusLabel;
    @FXML private Label pageLabel;
    @FXML private Label zoomLabel;
    @FXML private ToggleButton themeToggle;
    @FXML private ScrollPane scrollPane;

    private PdfService pdfService;

    private List<File> loadedFiles = new ArrayList<>();
    private int currentFileIndex = -1;

    private int currentPageIndex = 0;
    private float currentZoom = 1.0f;

    @FXML
    public void initialize() {
        pdfService = new PdfService();
        
        // Handle Ctrl + Scroll to zoom in / zoom out
        scrollPane.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();
                if (pdfService.getDocument() != null) {
                    double deltaY = event.getDeltaY();
                    if (deltaY == 0) deltaY = event.getTextDeltaY();
                    
                    if (deltaY > 0) {
                        currentZoom *= 1.1f;
                    } else if (deltaY < 0) {
                        currentZoom /= 1.1f;
                    }
                    
                    if (currentZoom < 0.1f) currentZoom = 0.1f;
                    if (currentZoom > 10.0f) currentZoom = 10.0f;
                    
                    updateZoomView();
                }
            }
        });

        // Handle Touchpad pinch-to-zoom
        scrollPane.setOnZoom(event -> {
            if (pdfService.getDocument() != null) {
                event.consume();
                currentZoom *= event.getZoomFactor();
                if (currentZoom < 0.1f) currentZoom = 0.1f;
                if (currentZoom > 10.0f) currentZoom = 10.0f;
                updateZoomView();
            }
        });

        // Add scroll to change pages if reaching bottom/top (simple simulation)
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            // Simplified logic: user uses buttons or shortcuts for pagination primarily
        });
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        List<File> files = fileChooser.showOpenMultipleDialog(pdfImageView.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            loadedFiles.addAll(files);
            // Switch to the first newly added PDF
            loadDocument(loadedFiles.size() - files.size());
        }
    }

    private void loadDocument(int index) {
        if (index >= 0 && index < loadedFiles.size()) {
            File file = loadedFiles.get(index);
            try {
                pdfService.loadPdf(file);
                currentFileIndex = index;
                currentPageIndex = 0;
                currentZoom = 1.0f;
                renderPage();
                statusLabel.setText("Opened " + file.getName() + " (" + (currentFileIndex + 1) + " of " + loadedFiles.size() + ")");
            } catch (IOException e) {
                statusLabel.setText("Error loading PDF");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleNextDocument() {
        if (currentFileIndex < loadedFiles.size() - 1) {
            loadDocument(currentFileIndex + 1);
        }
    }

    @FXML
    private void handlePrevDocument() {
        if (currentFileIndex > 0) {
            loadDocument(currentFileIndex - 1);
        }
    }

    @FXML
    private void handleSave() {
        if (pdfService.getDocument() == null) {
            statusLabel.setText("No document to save");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(pdfImageView.getScene().getWindow());
        if (file != null) {
            // Optional: document saving logic could be added back if needed natively.
            statusLabel.setText("Saved " + file.getName());
        }
    }

    @FXML
    private void handleExit() {
        try {
            pdfService.closePdf();
        } catch (Exception e) {}
        System.exit(0);
    }

    @FXML
    private void handleNextPage() {
        if (pdfService.getDocument() != null && currentPageIndex < pdfService.getPageCount() - 1) {
            currentPageIndex++;
            renderPage();
        }
    }

    @FXML
    private void handlePrevPage() {
        if (pdfService.getDocument() != null && currentPageIndex > 0) {
            currentPageIndex--;
            renderPage();
        }
    }

    @FXML
    private void handleZoomIn() {
        if (pdfService.getDocument() != null) {
            currentZoom += 0.25f;
            updateZoomView();
        }
    }

    @FXML
    private void handleZoomOut() {
        if (pdfService.getDocument() != null && currentZoom > 0.25f) {
            currentZoom -= 0.25f;
            updateZoomView();
        }
    }
    
    private void updateZoomView() {
        zoomLabel.setText(String.format("Zoom: %.0f%%", currentZoom * 100));
        if (pdfImageView.getImage() != null) {
            pdfImageView.setFitWidth(pdfImageView.getImage().getWidth() * currentZoom);
        }
    }

    @FXML
    private void handleFullScreen() {
        if (pdfImageView.getScene() != null && pdfImageView.getScene().getWindow() instanceof javafx.stage.Stage) {
            javafx.stage.Stage stage = (javafx.stage.Stage) pdfImageView.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    @FXML
    private void toggleTheme() {
        boolean darkTheme = !ThemeManager.isDarkTheme();
        themeToggle.setText(darkTheme ? "☀️ Light Mode" : "🌙 Dark Mode");
        ThemeManager.applyTheme(pdfImageView.getScene(), darkTheme);
    }

    private void goToPage(int index) {
        if (pdfService.getDocument() != null && index >= 0 && index < pdfService.getPageCount()) {
            currentPageIndex = index;
            renderPage();
        }
    }

    private void renderPage() {
        try {
            Image image = pdfService.renderPage(currentPageIndex, 2.0f); // Fixed render scale for high-res
            if (image != null) {
                pdfImageView.setImage(image);
                updateZoomView();
                pageLabel.setText("Page: " + (currentPageIndex + 1) + " / " + pdfService.getPageCount());
                scrollPane.setVvalue(0.0);
            }
        } catch (IOException e) {
            statusLabel.setText("Error rendering page");
            e.printStackTrace();
        }
    }
}
