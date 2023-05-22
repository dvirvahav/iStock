package com.example.stockdata.activities;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.Collections;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.components.AxisBase;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockdata.R;
import com.example.stockdata.api.StockApi;
import com.example.stockdata.api.StockApiImpl;
import com.example.stockdata.models.WeeklyData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private LineChart chart;
    private List<WeeklyData> weeklyDataa;
    private StockApi stockApi;
    private List<String> weeklyDates = new ArrayList<>();

    private final View.OnClickListener clickListener = this::handleTimeframeSelectionClick;

    private final StockApi.WeeklyDataCallback weeklyDataCallback = new StockApi.WeeklyDataCallback() {
        @Override
        public void onDataReceived(JSONObject weeklyData) {
            handleWeeklyDataReceived(weeklyData);
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

        TextView stockNameTextView = findViewById(R.id.stockNameTextView);
        chart = findViewById(R.id.chart);

        String stockSymbol = getIntent().getStringExtra("stockName");
        stockNameTextView.setText(stockSymbol);
        stockApi = StockApiImpl.getInstance();

        fetchHistoricalWeeklyData(stockSymbol);
        weeklyDataa = new ArrayList<>();

        LinearLayout timeframeSelection = findViewById(R.id.timeframeSelection);
        for (int i = 0; i < timeframeSelection.getChildCount(); i++) {
            TextView textView = (TextView) timeframeSelection.getChildAt(i);
            textView.setOnClickListener(clickListener);
        }
    }

    private void fetchHistoricalWeeklyData(String symbol) {
        stockApi.fetchHistoricalWeeklyData(symbol, weeklyDataCallback);
    }

    private static long parseTime(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void createGraph(List<WeeklyData> dataPoints, int numPoints) {
        if (dataPoints.size() == 0) {
            Toast.makeText(this, "Error fetching data, please try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = numPoints - 1; i >= 0; i--) {
            entries.add(new Entry(numPoints - 1 - i, dataPoints.get(i).getClose()));
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
        xAxis.setValueFormatter(new DateAxisValueFormatter(weeklyDates.subList(weeklyDates.size() - numPoints,weeklyDates.size()-1)));
        LineData lineData = new LineData(dataSet);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);

        chart.getAxisLeft().setEnabled(false);
        xAxis.setLabelCount(6, true);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }

    private void handleTimeframeSelectionClick(View v) {
        int numPoints;
        switch (v.getId()) {
            case R.id.oneDayButton:
                numPoints = 1;
                break;
            case R.id.oneWeekButton:
                numPoints = 1;
                break;
            case R.id.oneMonthButton:
                numPoints = 4;
                break;
            case R.id.threeMonthsButton:
                numPoints = 4 * 3;
                break;
            case R.id.sixMonthsButton:
                numPoints = 4 * 6;
                break;
            case R.id.oneYearButton:
                numPoints = 4 * 12;
                break;
            case R.id.maxButton:
                numPoints = weeklyDataa.size();
                break;
            default:
                numPoints = weeklyDataa.size();
                break;
        }

        createGraph(weeklyDataa, numPoints);
    }

    private void handleWeeklyDataReceived(JSONObject weeklyData) {
        try {
            JSONObject weekdata = weeklyData.getJSONObject("Weekly Time Series");
            Iterator<String> keys = weekdata.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject current = weekdata.getJSONObject(key);
                float close = Float.parseFloat(current.getString("4. close"));
                weeklyDataa.add(new WeeklyData(close, parseTime(key)));
                weeklyDates.add(key); // Store the dates.
            }
            Collections.reverse(weeklyDates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
