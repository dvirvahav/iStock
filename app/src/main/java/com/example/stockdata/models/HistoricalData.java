package com.example.stockdata.models;

public class HistoricalData {

    private float close;
    private float time;

    public HistoricalData(float close, float time) {
        this.close = close;
        this.time = time;
    }

    public float getClose() {
        return close;
    }
    // Factory method for creating instances
    public static HistoricalData createHistoricalData(float close, float time) {
        return new HistoricalData(close,time);
    }
    public float getTime() {
        return time;
    }


}
