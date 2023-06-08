package com.example.stockdata.models;

public class Stock {
    private final String symbol;
    private String price;
    private String dailyChange;
    private final String companyName;
    private final String companyIcon;  // Changed this line

    private Stock(String symbol, String price, String dailyChange, String companyName, String companyIcon) { // Changed this line
        this.symbol = symbol;
        this.price = price;
        this.dailyChange = dailyChange;
        this.companyName = companyName;
        this.companyIcon = companyIcon;  // Changed this line
    }

    // Factory method for creating instances
    public static Stock createStock(String symbol, String price, String dailyChange, String companyName, String companyIcon) { // Changed this line
        return new Stock(symbol, price, dailyChange, companyName, companyIcon); // Changed this line
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public String getDailyChange() {
        return dailyChange;
    }

    public String getCompanyIcon() {  // Changed this method
        return companyIcon;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDailyChange(String dailyChange) {
        this.dailyChange = dailyChange;
    }
}
