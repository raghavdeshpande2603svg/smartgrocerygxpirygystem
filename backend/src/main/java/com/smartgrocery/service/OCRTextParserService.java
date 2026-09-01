package com.smartgrocery.service;

import com.smartgrocery.ocr.ParsedBillItem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class OCRTextParserService {

    private static final Pattern LEADING_QUANTITY_PATTERN = Pattern.compile(
            "(?i)^(\\d+(?:[.,]\\d+)?)\\s*(kg|g|l|ml|pcs|pack|bunch|piece|box)?$"
    );

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(?:[.,]\\d+)?$");

    public List<ParsedBillItem> parseRawText(String rawText) {
        List<ParsedBillItem> parsedItems = new ArrayList<>();
        if (rawText == null || rawText.isBlank()) {
            return parsedItems;
        }

        String[] lines = rawText.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = normalizeLine(line);
            if (trimmed.isEmpty() || isHeaderLine(trimmed)) {
                continue;
            }

            ParsedBillItem item = parseSingleLine(trimmed);
            if (item != null) {
                parsedItems.add(item);
            }
        }

        return parsedItems;
    }

    private ParsedBillItem parseSingleLine(String trimmed) {
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length < 4) {
            return null;
        }

        int index = 0;
        double quantity = 0.0;
        if (LEADING_QUANTITY_PATTERN.matcher(tokens[0]).matches()) {
            quantity = parseDoubleValue(extractNumber(tokens[0]));
            index = 1;
            if (index < tokens.length && isUnitToken(tokens[index])) {
                index++;
            }
        }

        int firstNumericIndex = -1;
        int lastNumericIndex = -1;
        for (int i = index; i < tokens.length; i++) {
            if (isNumericToken(tokens[i])) {
                if (firstNumericIndex == -1) {
                    firstNumericIndex = i;
                }
                lastNumericIndex = i;
            }
        }

        if (firstNumericIndex <= index || lastNumericIndex < firstNumericIndex) {
            return null;
        }

        String productName = joinTokens(tokens, index, firstNumericIndex);
        String amountToken = tokens[lastNumericIndex];
        if (lastNumericIndex > firstNumericIndex + 1 && isZeroLikeToken(amountToken)) {
            lastNumericIndex--;
            amountToken = tokens[lastNumericIndex];
        }

        productName = normalizeProductName(productName);
        if (productName == null || productName.isBlank()) {
            return null;
        }

        double price = parseDoubleValue(amountToken);
        if (quantity == 0.0 && index > 0) {
            quantity = parseDoubleValue(extractNumber(tokens[index - 1]));
        }

        ParsedBillItem item = new ParsedBillItem(productName, quantity, price);
        item.setNormalizedName(normalizeProductKey(productName));
        item.setConfidence(0.82);
        item.setCategory("Other");
        item.setMatchSource("manual_review");
        return item;
    }

    private String normalizeLine(String line) {
        String value = line.replaceAll("[^a-zA-Z0-9./\\-\\s]", " ");
        value = value.replaceAll("\\s+", " ").trim();
        return value;
    }

    private boolean isHeaderLine(String line) {
        String lower = line.toLowerCase();
        return lower.contains("cash bill")
                || lower.contains("particulars")
                || lower.contains("date:")
                || lower.contains("qty")
                || lower.contains("total")
                || lower.contains("rate")
                || lower.contains("amount");
    }

    private String joinTokens(String[] tokens, int start, int end) {
        StringBuilder name = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (name.length() > 0) {
                name.append(" ");
            }
            name.append(tokens[i]);
        }
        return name.toString();
    }

    private boolean isUnitToken(String token) {
        String lower = token.toLowerCase();
        return lower.matches("(?i)(kg|g|l|ml|pcs|pack|bunch|piece|box)");
    }

    private boolean isNumericToken(String token) {
        return NUMBER_PATTERN.matcher(token.replace(",", ".").replace("-", "")).matches();
    }

    private boolean isZeroLikeToken(String token) {
        return token != null && token.replace(",", ".").matches("0+(?:\\.0+)?|00+");
    }

    private String extractNumber(String token) {
        Matcher matcher = Pattern.compile("(\\d+(?:[.,]\\d+)?)").matcher(token);
        return matcher.find() ? matcher.group(1).replace(',', '.') : "0";
    }

    private String normalizeProductName(String productName) {
        String value = productName.replaceAll("\\s+", " ").trim();
        value = value.replaceFirst("^(?:\\d+\\s+)+", "");
        value = value.replaceAll("(?i)\\b(amt|qty|rate|total|mrp|kg|g|l|ml|pcs|pack|bunch|piece|box)\\b", "").trim();
        value = value.replaceAll("(?i)\\b\\d+(?:[.,]\\d+)?\\s*(kg|g|l|ml|pcs|pack|bunch|piece|box)\\b", "").trim();
        value = value.replaceAll("[-/]", " ").trim();
        return value;
    }

    private String normalizeProductKey(String productName) {
        String normalized = normalizeProductName(productName).toLowerCase();
        String[] brands = {"amul", "nestle", "britania", "aashirvaad", "parle", "tata", "dabur", "mcdonalds"};
        for (String brand : brands) {
            if (normalized.startsWith(brand + " ")) {
                normalized = normalized.substring(brand.length()).trim();
                break;
            }
        }
        return normalized;
    }

    private double parseDoubleValue(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
