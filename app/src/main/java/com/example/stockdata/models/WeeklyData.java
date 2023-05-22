package com.example.stockdata.models;

public class WeeklyData {

    private float close;
    private float time;

    public WeeklyData(float close, float time) {
        this.close = close;
        this.time = time;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
