package com.example.stockdata.models;

public class Stock {
    private String symbol;
    private String price;
    private String dailyChange;
    private String companyName;
    private Stock(String symbol, String price, String dailyChange,String companyName) {
        this.symbol = symbol;
        this.price = price;
        this.dailyChange = dailyChange;
        this.companyName = companyName;
    }

    // Factory method for creating instances
    public static Stock createStock(String symbol, String price, String dailyChange,String companyName) {
        return new Stock(symbol, price, dailyChange,companyName);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDailyChange(String dailyChange) {
        this.dailyChange = dailyChange;
    }
}
