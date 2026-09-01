package com.smartgrocery.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

@Service
public class CategoryMatchingService {

    private static final Map<String, String> KEYWORD_CATEGORY_MAP = new LinkedHashMap<>();
    private static final JaroWinklerSimilarity JARO_WINKLER = new JaroWinklerSimilarity();

    static {
        KEYWORD_CATEGORY_MAP.put("milk", "Dairy");
        KEYWORD_CATEGORY_MAP.put("curd", "Dairy");
        KEYWORD_CATEGORY_MAP.put("paneer", "Dairy");
        KEYWORD_CATEGORY_MAP.put("yogurt", "Dairy");

        KEYWORD_CATEGORY_MAP.put("tomato", "Vegetables");
        KEYWORD_CATEGORY_MAP.put("onion", "Vegetables");
        KEYWORD_CATEGORY_MAP.put("spinach", "Vegetables");
        KEYWORD_CATEGORY_MAP.put("potato", "Vegetables");

        KEYWORD_CATEGORY_MAP.put("rice", "Grains");
        KEYWORD_CATEGORY_MAP.put("wheat", "Grains");
        KEYWORD_CATEGORY_MAP.put("atta", "Grains");
        KEYWORD_CATEGORY_MAP.put("flour", "Grains");

        KEYWORD_CATEGORY_MAP.put("apple", "Fruits");
        KEYWORD_CATEGORY_MAP.put("banana", "Fruits");
        KEYWORD_CATEGORY_MAP.put("orange", "Fruits");
        KEYWORD_CATEGORY_MAP.put("mango", "Fruits");

        KEYWORD_CATEGORY_MAP.put("juice", "Beverages");
        KEYWORD_CATEGORY_MAP.put("cola", "Beverages");
        KEYWORD_CATEGORY_MAP.put("water", "Beverages");
        KEYWORD_CATEGORY_MAP.put("milkshake", "Beverages");

        KEYWORD_CATEGORY_MAP.put("chips", "Snacks");
        KEYWORD_CATEGORY_MAP.put("biscuits", "Snacks");
        KEYWORD_CATEGORY_MAP.put("cookies", "Snacks");
        KEYWORD_CATEGORY_MAP.put("namkeen", "Snacks");

        KEYWORD_CATEGORY_MAP.put("frozen", "Frozen");
        KEYWORD_CATEGORY_MAP.put("peas", "Frozen");
        KEYWORD_CATEGORY_MAP.put("veg", "Frozen");
    }

    public CategoryMatchResult matchCategory(String rawProductName) {
        String normalized = normalize(rawProductName);
        if (normalized == null || normalized.isBlank()) {
            return new CategoryMatchResult("Other", "manual_review", 0.0, 14);
        }

        for (Map.Entry<String, String> entry : KEYWORD_CATEGORY_MAP.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return new CategoryMatchResult(entry.getValue(), "keyword", 0.92, defaultShelfLifeDays(entry.getValue()));
            }
        }

        String fallbackCategory = fuzzyFallback(normalized);
        if (!"Other".equals(fallbackCategory)) {
            return new CategoryMatchResult(fallbackCategory, "fuzzy", 0.74, defaultShelfLifeDays(fallbackCategory));
        }

        return new CategoryMatchResult("Other", "manual_review", 0.45, 14);
    }

    private String fuzzyFallback(String normalized) {
        String[] categories = {"Dairy", "Vegetables", "Grains", "Fruits", "Beverages", "Snacks", "Frozen", "Other"};
        double bestScore = 0.0;
        String bestCategory = "Other";

        for (String category : categories) {
            String categoryAnchor = category.toLowerCase();
            double similarity = JARO_WINKLER.apply(normalized, categoryAnchor);
            if (similarity > bestScore) {
                bestScore = similarity;
                bestCategory = category;
            }
        }

        if (bestScore > 0.35) {
            return bestCategory;
        }

        return "Other";
    }

    private String normalize(String rawProductName) {
        if (rawProductName == null) {
            return "";
        }
        return rawProductName
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int defaultShelfLifeDays(String category) {
        return switch (category) {
            case "Dairy" -> 5;
            case "Vegetables" -> 7;
            case "Grains" -> 30;
            case "Fruits" -> 6;
            case "Beverages" -> 12;
            case "Snacks" -> 45;
            case "Frozen" -> 60;
            default -> 14;
        };
    }

    public record CategoryMatchResult(String category, String source, double confidence, int defaultShelfLifeDays) {}
}
