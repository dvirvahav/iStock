package com.example.stockdata.api;

import java.util.Iterator;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;
import android.annotation.SuppressLint;
import com.example.stockdata.models.Stock;
import com.example.stockdata.activities.MainActivity;
import com.example.stockdata.utils.MySharedPreferences;
import com.example.stockdata.adapters.StockRecyclerViewAdapter;


public class StockManager {

    private StockApi stockApi;
    private MySharedPreferences mySharedPreferences;
    private Handler handler;
    private Runnable fetchStocksRunnable;
    private List<Stock> stocks;
    private MainActivity mainActivity;


    public StockManager(MainActivity mainActivity, MySharedPreferences mySharedPreferences, Handler handler, Runnable fetchStocksRunnable, List<Stock> stocks, StockRecyclerViewAdapter adapter) {
        this.stockApi = StockApiImpl.getInstance();
        this.mySharedPreferences = mySharedPreferences;
        this.handler = handler;
        this.fetchStocksRunnable = fetchStocksRunnable;
        this.stocks = stocks;
        this.mainActivity = mainActivity;

    }

    public void fetchAndDisplayStock(String symbol) {
        stockApi.fetchStockData(symbol.toUpperCase(), new StockApi.StockDataCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataReceived(Stock stock) {
                if (stock != null) {
                    mainActivity.runOnUiThread(() -> {
                        // Check if the stock symbol already exists in the list
                        Stock existingStock = findStockBySymbol(symbol);
                        if (existingStock != null) {
                            // Stock symbol already exists, update price and daily change
                            existingStock.setPrice(stock.getPrice());
                            existingStock.setDailyChange(stock.getDailyChange());
                        } else {
                            // Stock symbol is new, create a new Stock object and add it to the list
                            stocks.add(stock);
                            mySharedPreferences.addToList(symbol);
                        }

                        mainActivity.refreshAdapter();
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println(e);
                mainActivity.runOnUiThread(() -> Toast.makeText(mainActivity, "No such stock symbol / too many requests", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeStock(Stock stock) {
        Iterator<Stock> iterator = stocks.iterator();
        while (iterator.hasNext()) {
            Stock temp_stock = iterator.next();
            if (temp_stock.getSymbol().equalsIgnoreCase(stock.getSymbol())) {
                iterator.remove();
            }
        }


        mySharedPreferences.removeFromList(stock.getSymbol().toLowerCase());
        mainActivity.refreshAdapter();
    }

    public void startFetchingStocks() {
        handler.postDelayed(fetchStocksRunnable, 0);
    }

    public void stopFetchingStocks() {
        handler.removeCallbacks(fetchStocksRunnable);
    }

    public Stock findStockBySymbol(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }
        return null;
    }
}

