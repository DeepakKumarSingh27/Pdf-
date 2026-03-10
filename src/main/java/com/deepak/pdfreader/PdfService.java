package com.deepak.pdfreader;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfService {

    private PDDocument document;
    private PDFRenderer pdfRenderer;
    private String currentFilePath;

    public void loadPdf(File file) throws IOException {
        closePdf();
        this.document = PDDocument.load(file);
        this.pdfRenderer = new PDFRenderer(document);
        // caching can be added here if needed
        this.currentFilePath = file.getAbsolutePath();
    }

    public void closePdf() throws IOException {
        if (document != null) {
            document.close();
            document = null;
            pdfRenderer = null;
            currentFilePath = null;
        }
    }

    public int getPageCount() {
        if (document != null) {
            return document.getNumberOfPages();
        }
        return 0;
    }

    public Image renderPage(int pageIndex, float scale) throws IOException {
        if (document != null && pageIndex >= 0 && pageIndex < document.getNumberOfPages()) {
            // scale is DPI/72f, e.g scale=2 means 144 DPI
            BufferedImage bim = pdfRenderer.renderImage(pageIndex, scale);
            return SwingFXUtils.toFXImage(bim, null);
        }
        return null;
    }
    
    public List<String> getBookmarks() {
        List<String> bookmarks = new ArrayList<>();
        if (document != null) {
            PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
            if (outline != null) {
                for (PDOutlineItem item : outline.children()) {
                    bookmarks.add(item.getTitle());
                }
            }
        }
        return bookmarks;
    }

    public PDDocument getDocument() {
        return document;
    }
    
    public String getCurrentFilePath() {
        return currentFilePath;
    }
}
