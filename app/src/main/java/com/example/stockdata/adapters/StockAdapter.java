package com.example.stockdata.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stockdata.R;
import com.example.stockdata.models.Stock;
import com.example.stockdata.activities.GraphActivity;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Stock> stocks;
    private OnItemClickListener listener;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView, priceTextView, dailyChangeTextView,fullNameTextView;
        Button removeStockButton;

        public ViewHolder(View view) {
            super(view);
            symbolTextView = view.findViewById(R.id.symbolTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            dailyChangeTextView = view.findViewById(R.id.dailyChangeTextView);
            fullNameTextView = view.findViewById(R.id.fullNameTextView);

            removeStockButton = view.findViewById(R.id.removeStockButton);
        }
    }

    public StockAdapter(List<Stock> stocks, OnItemClickListener listener) {
        this.stocks = stocks;
        this.listener = listener;
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.symbolTextView.setText(stock.getSymbol());
        holder.priceTextView.setText(String.valueOf(stock.getPrice()));
        holder.dailyChangeTextView.setText(String.valueOf(stock.getDailyChange()));
        holder.fullNameTextView.setText(String.valueOf(stock.getCompanyName()));
        // Remove stock functionality
        holder.removeStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(stock);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevents rapid double-clicks, using 1 second as the minimum click interval
                v.setEnabled(false);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 1000);

                Intent intent = new Intent(v.getContext(), GraphActivity.class);
                intent.putExtra("stockName", stock.getSymbol());
                v.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }
}

