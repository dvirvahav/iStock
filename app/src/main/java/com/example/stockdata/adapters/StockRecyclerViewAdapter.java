package com.example.stockdata.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockdata.R;
import com.example.stockdata.models.Stock;
import com.example.stockdata.activities.GraphActivity;

import java.util.List;

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockRecyclerViewAdapter.ViewHolder> {
    private final List<Stock> stocks;
    private final OnItemClickListener listener;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView, priceTextView, dailyChangeTextView,fullNameTextView;
        ImageView stockIconImageView;
        Button removeStockButton;

        public ViewHolder(View view) {
            super(view);
            symbolTextView = view.findViewById(R.id.symbolTextView);
            priceTextView = view.findViewById(R.id.priceTextView);
            dailyChangeTextView = view.findViewById(R.id.dailyChangeTextView);
            fullNameTextView = view.findViewById(R.id.fullNameTextView);
            stockIconImageView = view.findViewById(R.id.stockIconImageView);
            removeStockButton = view.findViewById(R.id.removeStockButton);
        }
    }

    public StockRecyclerViewAdapter(List<Stock> stocks, OnItemClickListener listener) {
        this.stocks = stocks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.symbolTextView.setText(stock.getSymbol());
        holder.priceTextView.setText("$" + String.valueOf(stock.getPrice()));

        // Load the stock icon using Glide
        String iconUrl = stock.getCompanyIcon();
        Glide.with(holder.itemView).load(iconUrl).into(holder.stockIconImageView);

        // Display daily change and change text color based on positive or negative change
        String dailyChangeStr = stock.getDailyChange();
        double dailyChange = Double.parseDouble(dailyChangeStr);

        if (dailyChange > 0) {
            holder.dailyChangeTextView.setTextColor(Color.GREEN);
            holder.dailyChangeTextView.setText("+" + dailyChangeStr+"%");// set text color to green for positive change
        } else {
            holder.dailyChangeTextView.setTextColor(Color.RED);
            holder.dailyChangeTextView.setText(dailyChangeStr+"%");// set text color to red for negative change
        }

        holder.fullNameTextView.setText(String.valueOf(stock.getCompanyName()));

        // Remove stock functionality
        holder.removeStockButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(stock);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            // Prevents rapid double-clicks, using 1 second as the minimum click interval
            v.setEnabled(false);
            v.postDelayed(() -> v.setEnabled(true), 1000);

            Intent intent = new Intent(v.getContext(), GraphActivity.class);
            intent.putExtra("stockName", stock.getSymbol());
            intent.putExtra("companyIconUrl", stock.getCompanyIcon());
            v.getContext().startActivity(intent);
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

