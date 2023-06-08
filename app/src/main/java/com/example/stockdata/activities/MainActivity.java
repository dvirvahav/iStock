package com.example.stockdata.activities;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stockdata.api.StockManager;
import com.example.stockdata.utils.MySharedPreferences;
import com.example.stockdata.R;
import com.example.stockdata.models.Stock;
import com.example.stockdata.adapters.StockRecyclerViewAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private StockRecyclerViewAdapter adapter;
    private  List<Stock> stocks = new ArrayList<>();
    private StockManager stockManager;
    private List<String> myList;
    private final Handler handler = new Handler();

    private final Runnable fetchStocksRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            LocalDateTime now = LocalDateTime.now();
            String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("Current update: " + currentTime);
            for (String symbol : myList) {
                System.out.println("Update stock: " + symbol);
                stockManager.fetchAndDisplayStock(symbol);
            }
            handler.postDelayed(this, 1*60000); // Schedule the next execution after 10 minutes.
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
        myList = new ArrayList<>();
        myList = mySharedPreferences.getList();
        System.out.println("Local memory: " +  myList);


        Button removeAllStocks = findViewById(R.id.removeAllStocks);

        EditText stockInput = findViewById(R.id.stockInput);
        Button addStock = findViewById(R.id.addStock);

        RecyclerView recyclerView = findViewById(R.id.stock_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StockRecyclerViewAdapter(stocks, this::removeStock);
        recyclerView.setAdapter(adapter);

        stockManager = new StockManager(this, mySharedPreferences, handler, fetchStocksRunnable, stocks, adapter);

        stockManager.startFetchingStocks();

        addStock.setOnClickListener(v -> {
            String symbol = stockInput.getText().toString().toLowerCase();
            if (!myList.contains(symbol.toLowerCase())) {
                stockManager.fetchAndDisplayStock(symbol);
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

    private void removeStock(Stock stock) {
        stockManager.removeStock(stock);
    }
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        super.onDestroy();
        stockManager.stopFetchingStocks();
    }

}






