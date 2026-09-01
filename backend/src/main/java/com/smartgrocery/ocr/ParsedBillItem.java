package com.smartgrocery.ocr;

public class ParsedBillItem {
    private String productName;
    private double quantity;
    private double price;
    private String normalizedName;
    private String category;
    private double confidence;
    private Integer defaultShelfLifeDays;
    private String matchSource;

    public ParsedBillItem() {
    }

    public ParsedBillItem(String productName, double quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public ParsedBillItem(String productName, double quantity, double price, String normalizedName,
                         String category, double confidence, Integer defaultShelfLifeDays, String matchSource) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.normalizedName = normalizedName;
        this.category = category;
        this.confidence = confidence;
        this.defaultShelfLifeDays = defaultShelfLifeDays;
        this.matchSource = matchSource;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Integer getDefaultShelfLifeDays() {
        return defaultShelfLifeDays;
    }

    public void setDefaultShelfLifeDays(Integer defaultShelfLifeDays) {
        this.defaultShelfLifeDays = defaultShelfLifeDays;
    }

    public String getMatchSource() {
        return matchSource;
    }

    public void setMatchSource(String matchSource) {
        this.matchSource = matchSource;
    }
}
