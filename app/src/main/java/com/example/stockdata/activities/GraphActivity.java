package com.example.stockdata.activities;
import com.bumptech.glide.Glide;
import com.example.stockdata.api.GraphManager;
import com.github.mikephil.charting.formatter.ValueFormatter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.components.AxisBase;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockdata.R;
import com.example.stockdata.api.StockApi;
import com.example.stockdata.models.HistoricalData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    // Variables for the Line Chart and GraphManager
    private LineChart chart;
    private GraphManager graphManager;
    // Click listener for timeframe selection
    private final View.OnClickListener clickListener = this::handleTimeframeSelectionClick;
    private  LinearLayout timeframeSelection;
    private ImageView companyIconImageView; // Declare ImageView

    // Callback for the stock API's  data
    private final StockApi.HistoricalDataCallback historicalDataCallback = new StockApi.HistoricalDataCallback() {
        @Override
        public void onDataReceived(JSONObject historicData) {
            graphManager.handleHistoricalDataReceived(historicData);
        }

        @Override
        public void onError(Exception e) {
            handleDataError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Find the TextView by its id to display the stock name and the LineChart for data visualization
        TextView stockNameTextView = findViewById(R.id.stockNameTextView);
        chart = findViewById(R.id.chart);

// Initialize ImageView
        companyIconImageView = findViewById(R.id.companyIconImageView);

        // Gets the company icon URL from the intent's extras
        String companyIconUrl = getIntent().getStringExtra("companyIconUrl");

        // Load company icon into ImageView using Glide
        Glide.with(this)
                .load(companyIconUrl)
                .into(companyIconImageView);
        // Gets the stock symbol from the intent's extras
        String stockSymbol = getIntent().getStringExtra("stockName");

        // Sets the stock symbol as the text for the TextView we fetched earlier
        stockNameTextView.setText(stockSymbol);

        // Initialize the GraphManager with weekly data callback
        graphManager = new GraphManager(historicalDataCallback);

        // Fetches the historical weekly data for the given stock symbol
        graphManager.fetchHistoricalData(stockSymbol);

        // Fetches the LinearLayout that holds the different timeframes
        timeframeSelection = findViewById(R.id.timeframeSelection);

        // Sets the click listener for each TextView (representing a timeframe) in the LinearLayout
        for (int i = 0; i < timeframeSelection.getChildCount(); i++) {
            TextView textView = (TextView) timeframeSelection.getChildAt(i);
            textView.setOnClickListener(clickListener);
        }
        firstLoading();

    }
    private void firstLoading(){

        if(graphManager.isFlagFirstFetch()) {

            try {
                Thread.sleep(2000); // Sleep for 2000 milliseconds so the data will be loaded.
                graphManager.setFlagFirstFetch(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            graphManager.setFlagFirstFetch(false);
        }
        createGraph(graphManager.getHistoricalData(), graphManager.getHistoricalData().size());

    }

    private void handleTimeframeSelectionClick(View v) {
        // Reset all TextViews to normal style
        for (int i = 0; i < timeframeSelection.getChildCount(); i++) {
            TextView tv = (TextView) timeframeSelection.getChildAt(i);
            tv.setTypeface(null, Typeface.NORMAL);
        }

        // Make the clicked TextView bold
        TextView clickedTextView = (TextView) v;
        clickedTextView.setTypeface(null, Typeface.BOLD);

        int numPoints;
        switch (v.getId()) {
            case R.id.oneDayButton:
                numPoints = 1;
                break;
            case R.id.oneWeekButton:
                numPoints = 7;
                break;
            case R.id.oneMonthButton:
                numPoints = 30;
                break;
            case R.id.threeMonthsButton:
                numPoints = 30 * 3;
                break;
            case R.id.sixMonthsButton:
                numPoints = 30 * 6;
                break;
            case R.id.oneYearButton:
                numPoints = 30 * 12;
                break;
            case R.id.maxButton:
                numPoints = graphManager.getHistoricalData().size();
                break;
            default:
                numPoints = graphManager.getHistoricalData().size();
                break;
        }

        createGraph(graphManager.getHistoricalData(), numPoints);
    }
    private void createGraph(List<HistoricalData> dataPoints, int numPoints) {

        if (dataPoints.size() == 0) {
            Toast.makeText(this, "Error fetching data, please try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = numPoints - 1; i >= 0; i--) {
            entries.add(new Entry(numPoints - 1 - i,
                    dataPoints.get(i).getClose()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Stock Price");
        dataSet.setColors(new int[]{ContextCompat.getColor(this, R.color.gradientStart), ContextCompat.getColor(this, R.color.gradientEnd)}, (int) 0.5f);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ContextCompat.getColor(this, R.color.gradientStart), ContextCompat.getColor(this, R.color.gradientEnd)}));
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DateAxisValueFormatter(graphManager.getWeeklyDates().subList(graphManager.getWeeklyDates().size() - numPoints,graphManager.getWeeklyDates().size()-1))); // getting only the required date
        LineData lineData = new LineData(dataSet);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);

        chart.getAxisLeft().setEnabled(false);
        xAxis.setLabelCount(4, true);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }



    private void handleDataError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GraphActivity.this, "Error fetching historical data", Toast.LENGTH_LONG).show();
            }
        });
    }

    class DateAxisValueFormatter extends ValueFormatter {
        private List<String> dates;

        DateAxisValueFormatter(List<String> dates) {
            this.dates = dates;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < dates.size()) {
                return dates.get(index);
            } else {
                return "";
            }
        }
    }
}
