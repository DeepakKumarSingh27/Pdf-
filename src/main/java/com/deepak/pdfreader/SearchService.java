package com.deepak.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    public List<SearchResult> search(PDDocument document, String keyword) {
        List<SearchResult> results = new ArrayList<>();
        if (document == null || keyword == null || keyword.isEmpty()) {
            return results;
        }

        try {
            PDFTextStripper stripper = new PDFTextStripper();
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(document);
                if (text != null && text.toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(new SearchResult(i - 1, "Match found on page " + i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static class SearchResult {
        public final int pageIndex;
        public final String contextText;

        public SearchResult(int pageIndex, String contextText) {
            this.pageIndex = pageIndex;
            this.contextText = contextText;
        }
        
        @Override
        public String toString() {
            return contextText;
        }
    }
}
