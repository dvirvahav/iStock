package com.example.stockdata.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stockdata.utils.MySharedPreferences;
import com.example.stockdata.R;
import com.example.stockdata.models.Stock;
import com.example.stockdata.adapters.StockAdapter;
import com.example.stockdata.api.StockApi;
import com.example.stockdata.api.StockApiImpl;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private StockAdapter adapter;
    private StockApi stockApi;
    private final List<Stock> stocks = new ArrayList<>();
    private MySharedPreferences mySharedPreferences ;
    private List<String> myList ;
    private final Handler handler = new Handler();

    private final Runnable fetchStocksRunnable = new Runnable() {
        @Override
        public void run() {
            for (String symbol : myList) {
                fetchAndDisplayStock(symbol);
            }
            handler.postDelayed(this, 10*60000); // Schedule the next execution after 10 minutes.
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockApi = StockApiImpl.getInstance();
        mySharedPreferences = new MySharedPreferences(getApplicationContext());
        myList = mySharedPreferences.getList();
        Button removeAllStocks = findViewById(R.id.removeAllStocks);
        EditText stockInput = findViewById(R.id.stockInput);
        Button addStock = findViewById(R.id.addStock);

        RecyclerView recyclerView = findViewById(R.id.stock_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StockAdapter(stocks, this::removeStock);
        recyclerView.setAdapter(adapter);

        startFetchingStocks();

        addStock.setOnClickListener(v -> {
            String symbol = stockInput.getText().toString();

            if (!myList.contains(symbol)) {
                fetchAndDisplayStock(symbol);
                stockInput.getText().clear();
            }
            else{
                Toast.makeText(MainActivity.this, "Stock is already on your list", Toast.LENGTH_SHORT).show();

            }
        });

        removeAllStocks.setOnClickListener(v -> {
            stocks.clear();
            myList.clear();
            mySharedPreferences.removeAll();
            adapter.notifyDataSetChanged();
        });
    }

    public void fetchAndDisplayStock(String symbol) {
        stockApi.fetchStockData(symbol, new StockApi.StockDataCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataReceived(Stock stock) {
                if (stock != null) {
                    runOnUiThread(() -> {
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

                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "No such stock symbol / too many requests", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeStock(Stock stock) {
        stocks.remove(stock);
        myList.remove(stock.getSymbol());
        mySharedPreferences.removeFromList(stock.getSymbol());
        adapter.notifyDataSetChanged();
    }

    // Call this method to start the execution
    private void startFetchingStocks() {
        handler.postDelayed(fetchStocksRunnable, 0); // Initial execution with no delay
    }
    // Call this method to stop the execution
    private void stopFetchingStocks() {
        handler.removeCallbacks(fetchStocksRunnable);
    }
    // Helper method to find a stock by symbol
    private Stock findStockBySymbol(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFetchingStocks();
    }


}



