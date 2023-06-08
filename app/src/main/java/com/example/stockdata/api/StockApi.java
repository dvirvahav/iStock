package com.example.stockdata.api;

import com.example.stockdata.models.Stock;

import org.json.JSONException;
import org.json.JSONObject;

public interface StockApi {
    void fetchStockData(String symbol, StockDataCallback callback);
    void fetchHistoricalData(String symbol, HistoricalDataCallback callback);



    interface StockDataCallback {
        void onDataReceived(Stock stock);
        void onError(Exception e);
    }

    interface HistoricalDataCallback {
        void onDataReceived(JSONObject weeklyData) throws JSONException;
        void onError(Exception e);
    }
}
