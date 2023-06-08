package com.example.stockdata.api;

import com.example.stockdata.models.HistoricalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GraphManager {
    private StockApi stockApi;
    private List<HistoricalData> historicalData;
    private boolean flagFirstFetch;
    private StockApi.HistoricalDataCallback historicalDataCallback;
    private List<String> weeklyDates = new ArrayList<>();

    public GraphManager(StockApi.HistoricalDataCallback historicalDataCallback) {
        this.stockApi = StockApiImpl.getInstance();
        this.flagFirstFetch = true;
        this.historicalDataCallback = historicalDataCallback;
        this.historicalData = new ArrayList<>();
    }

    public void fetchHistoricalData(String symbol) {
        stockApi.fetchHistoricalData(symbol, historicalDataCallback);
    }

    public List<HistoricalData> getHistoricalData() {
        return historicalData;
    }

    public List<String> getWeeklyDates() {
        return weeklyDates;
    }

    public void setFlagFirstFetch(boolean flag) {
        this.flagFirstFetch = flag;
    }

    public boolean isFlagFirstFetch() {
        return flagFirstFetch;
    }

    public void handleHistoricalDataReceived(JSONObject weeklyData) {
        try {
            JSONArray dataArray = weeklyData.getJSONArray("values");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject current = dataArray.getJSONObject(i);
                float close = Float.parseFloat(current.getString("close"));
                this.historicalData.add(HistoricalData.createHistoricalData(close, parseTime(current.getString("datetime"))));
                weeklyDates.add(current.getString("datetime")); // Store the dates.
            }
            Collections.reverse(weeklyDates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long parseTime(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

