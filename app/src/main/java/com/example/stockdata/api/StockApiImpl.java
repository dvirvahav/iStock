package com.example.stockdata.api;

import com.example.stockdata.models.Stock;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class StockApiImpl implements StockApi {

    private static StockApiImpl instance;
    private OkHttpClient httpClient;
    private static final String BASE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&apikey=QQOIJBZAG4BDQUGE";
    private static final String HISTORICAL_WEEKLY_BASE_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&apikey=QQOIJBZAG4BDQUGE";

    // Private constructor to prevent direct instantiation
    private StockApiImpl() {
        this.httpClient = new OkHttpClient();
    }

    // Public method to get the single instance
    public static synchronized StockApiImpl getInstance() {
        if (instance == null) {
            instance = new StockApiImpl();
        }
        return instance;
    }

    @Override
    public void fetchStockData(String symbol, StockDataCallback callback) {
        // implementation of fetchStockData
        String quoteUrl = BASE_URL + "&symbol=" + symbol;
        Request quoteRequest = new Request.Builder().url(quoteUrl).build();

        httpClient.newCall(quoteRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject quote = jsonResponse.getJSONObject("Global Quote");
                        String stockPrice = quote.getString("05. price");
                        String previousClose = quote.getString("08. previous close");
                        float dailyChange = Float.parseFloat(stockPrice) - Float.parseFloat(previousClose);
                        // format the dailyChange to 3 decimal places
                        String dailyChangeFormatted = String.format("%.3f", dailyChange);

                        // Now fetch the company name
                        String searchUrl = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + symbol + "&apikey=QQOIJBZAG4BDQUGE";
                        Request searchRequest = new Request.Builder().url(searchUrl).build();
                        httpClient.newCall(searchRequest).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                callback.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String searchResponseBody = response.body().string();
                                    try {
                                        JSONObject searchJsonResponse = new JSONObject(searchResponseBody);
                                        JSONArray bestMatches = searchJsonResponse.getJSONArray("bestMatches");
                                        for (int i = 0; i < bestMatches.length(); i++) {
                                            JSONObject match = bestMatches.getJSONObject(i);
                                            if (match.getString("1. symbol").equals(symbol)) {
                                                String companyName = match.getString("2. name");
                                                Stock stock = Stock.createStock(symbol, stockPrice, dailyChangeFormatted, companyName);
                                                callback.onDataReceived(stock);
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        callback.onError(e);
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }


    @Override
    public void fetchHistoricalWeeklyData(String symbol, WeeklyDataCallback callback) {
        // implementation of fetchHistoricalWeeklyData
        String url = HISTORICAL_WEEKLY_BASE_URL + "&symbol=" + symbol;
        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callback.onDataReceived(jsonResponse);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
