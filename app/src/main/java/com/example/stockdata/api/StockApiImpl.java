package com.example.stockdata.api;

import androidx.annotation.NonNull;

import com.example.stockdata.models.Stock;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

public class StockApiImpl implements StockApi {
//Ky4zmV-VoUsSTxnFn97k
    private static StockApiImpl instance;
    private final OkHttpClient httpClient;



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

public void fetchStockData(String symbol, StockDataCallback callback) {
    String quoteUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=chuvqd1r01qrqenfvnegchuvqd1r01qrqenfvnf0";
    Request quoteRequest = new Request.Builder().url(quoteUrl).build();

    httpClient.newCall(quoteRequest).enqueue(new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            System.out.println("Error in fetch stock data: IO");
            callback.onError(e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String stockPrice = jsonResponse.getString("c"); // Current price
                    String previousClose = jsonResponse.getString("pc"); // Previous close price
                    float dailyChange = Float.parseFloat(stockPrice) - Float.parseFloat(previousClose);
                    // format the dailyChange to 3 decimal places
                    String dailyChangeFormatted = String.format("%.3f", dailyChange);

                    fetchCompanyName(symbol, stockPrice, dailyChangeFormatted, callback);

                } catch (Exception e) {
                    System.out.println("Error in fetch stock data: JSON");
                    callback.onError(e);
                }
            }
        }
    });
}




    public void fetchCompanyName(String symbol, String stockPrice, String dailyChangeFormatted, StockDataCallback callback) {
        String searchUrl = "https://financialmodelingprep.com/api/v3/profile/" + symbol + "?apikey=23e420801dc101810bd3ea4c012c359d";
        Request searchRequest = new Request.Builder().url(searchUrl).build();
        httpClient.newCall(searchRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error in fetch company: IO"); callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String searchResponseBody = response.body().string();
                    try {
                        JSONArray searchJsonResponse = new JSONArray(searchResponseBody);
                        JSONObject companyProfile = searchJsonResponse.getJSONObject(0);
                        String companyName = companyProfile.getString("companyName");
                        String companyIcon = companyProfile.getString("image"); // Fetch image field from JSON
                        Stock stock = Stock.createStock(symbol, stockPrice, dailyChangeFormatted, companyName, companyIcon); // Pass companyIcon to createStock
                        callback.onDataReceived(stock);
                    } catch (Exception e) {
                        System.out.println("Error in fetch company: JSON");
                        callback.onError(e);
                    }
                }
            }
        });
    }



    public void fetchHistoricalData(String symbol, HistoricalDataCallback callback) {
        // Twelve Data API endpoint
        String url = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=1day&outputsize=5000&apikey=7fc036d97046440da628e1e7b97cc514";
        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error in fetch historic data: IO");
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody;
                    responseBody = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callback.onDataReceived(jsonResponse);
                    } catch (Exception e) {
                        System.out.println("Error in fetch historic data: JSON");
                        callback.onError(e);
                    }
                }
            }
        });
    }


}
