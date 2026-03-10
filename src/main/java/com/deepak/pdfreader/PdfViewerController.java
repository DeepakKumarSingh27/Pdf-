package com.deepak.pdfreader;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfViewerController {

    @FXML private ImageView pdfImageView;
    @FXML private Label statusLabel;
    @FXML private Label pageLabel;
    @FXML private Label zoomLabel;
    @FXML private TextField searchField;
    @FXML private ListView<String> bookmarkList;
    @FXML private ListView<SearchService.SearchResult> searchResultList;
    @FXML private ToggleButton themeToggle;
    @FXML private ScrollPane scrollPane;

    private PdfService pdfService;
    private SearchService searchService;
    private AnnotationService annotationService;

    private int currentPageIndex = 0;
    private float currentZoom = 1.0f;
    private float renderScale = 2.0f; 

    @FXML
    public void initialize() {
        pdfService = new PdfService();
        searchService = new SearchService();
        annotationService = new AnnotationService();
        
        searchResultList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                goToPage(newVal.pageIndex);
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
        File file = fileChooser.showOpenDialog(pdfImageView.getScene().getWindow());
        if (file != null) {
            try {
                pdfService.loadPdf(file);
                currentPageIndex = 0;
                currentZoom = 1.0f;
                renderScale = 2.0f;
                loadBookmarks();
                renderPage();
                statusLabel.setText("Opened " + file.getName());
            } catch (IOException e) {
                statusLabel.setText("Error loading PDF");
                e.printStackTrace();
            }
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
            annotationService.saveDocument(pdfService.getDocument(), file.getAbsolutePath());
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
            renderScale += 0.5f;
            currentZoom += 0.25f;
            renderPage();
        }
    }

    @FXML
    private void handleZoomOut() {
        if (pdfService.getDocument() != null && renderScale > 0.6f) {
            renderScale -= 0.5f;
            currentZoom -= 0.25f;
            renderPage();
        }
    }

    @FXML
    private void handleSearch() {
        if (pdfService.getDocument() == null) return;
        String keyword = searchField.getText();
        if (keyword != null && !keyword.isEmpty()) {
            statusLabel.setText("Searching...");
            new Thread(() -> {
                List<SearchService.SearchResult> results = searchService.search(pdfService.getDocument(), keyword);
                javafx.application.Platform.runLater(() -> {
                    searchResultList.getItems().clear();
                    searchResultList.getItems().addAll(results);
                    statusLabel.setText("Found " + results.size() + " matches");
                });
            }).start();
        }
    }

    @FXML
    private void toggleTheme() {
        boolean darkTheme = !ThemeManager.isDarkTheme();
        themeToggle.setText(darkTheme ? "☀️ Light Mode" : "🌙 Dark Mode");
        ThemeManager.applyTheme(pdfImageView.getScene(), darkTheme);
    }

    @FXML
    private void handleHighlightMode() {
        if (pdfService.getDocument() != null) {
            // Mock highlight on current page, coordinate approximation
            annotationService.addHighlight(pdfService.getDocument(), currentPageIndex, 100, 500, 200, 20);
            renderPage(); 
            statusLabel.setText("Added demo highlight. Save file to keep.");
        }
    }

    private void goToPage(int index) {
        if (pdfService.getDocument() != null && index >= 0 && index < pdfService.getPageCount()) {
            currentPageIndex = index;
            renderPage();
        }
    }

    private void loadBookmarks() {
        bookmarkList.getItems().clear();
        List<String> bookmarks = pdfService.getBookmarks();
        if (bookmarks.isEmpty()) {
            bookmarkList.getItems().add("No bookmarks found");
        } else {
            bookmarkList.getItems().addAll(bookmarks);
        }
    }

    private void renderPage() {
        try {
            Image image = pdfService.renderPage(currentPageIndex, renderScale);
            if (image != null) {
                pdfImageView.setImage(image);
                zoomLabel.setText(String.format("Zoom: %.0f%%", currentZoom * 100));
                pageLabel.setText("Page: " + (currentPageIndex + 1) + " / " + pdfService.getPageCount());
                scrollPane.setVvalue(0.0);
            }
        } catch (IOException e) {
            statusLabel.setText("Error rendering page");
            e.printStackTrace();
        }
    }
}
