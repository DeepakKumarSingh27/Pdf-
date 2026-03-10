package com.deepak.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.io.File;
import java.io.IOException;

public class AnnotationService {
    
    public void addHighlight(PDDocument document, int pageIndex, float x, float y, float width, float height) {
        if (document == null) return;
        PDPage page = document.getPage(pageIndex);
        PDAnnotationTextMarkup textMarkup = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
        float[] quadPoints = new float[8];
        quadPoints[0] = x;
        quadPoints[1] = y + height;
        quadPoints[2] = x + width;
        quadPoints[3] = y + height;
        quadPoints[4] = x;
        quadPoints[5] = y;
        quadPoints[6] = x + width;
        quadPoints[7] = y;
        
        textMarkup.setQuadPoints(quadPoints);
        textMarkup.setRectangle(new PDRectangle(x, y, width, height));
        
        try {
            page.getAnnotations().add(textMarkup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDocument(PDDocument document, String savePath) {
        try {
            if (document != null) {
                document.save(new File(savePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
