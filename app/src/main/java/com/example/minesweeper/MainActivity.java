package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int clock = 0;
    private String icon = "pick";
    private boolean running = false;
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private ArrayList<Integer> mines = new ArrayList<Integer>();

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    SecureRandom random = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<=11; i++) {
            for (int j=0; j<=9; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);


                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }
        placeMines();
        cellInit();
        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }
//        running=true;
        runTimer();

    }
    private void placeMines(){
        while(mines.size()<4){
            int mineLocation = random.nextInt(ROW_COUNT * COLUMN_COUNT);
            if(!mines.contains(mineLocation)){
                mines.add(mineLocation);

            }

            for(int curr:mines) {
                TextView mineView = cell_tvs.get(curr);
                mineView.setTag("mine");
                mineView.setBackgroundColor(Color.RED);
//                mineView.setBackgroundColor(Color.RED);
            }
        }
    }
    private void cellInit(){
        for (int i =0; i<cell_tvs.size(); ++i){
            if(!mines.contains(i)){
                int mineCount = countMines(i);
                if(mineCount==0){
                    cell_tvs.get(i).setText("");
                }
                else {
                    cell_tvs.get(i).setBackgroundColor(Color.BLACK);
                    cell_tvs.get(i).setText(String.valueOf(mineCount));
                }
                }
            }

        }



    private int countMines(int idx) {
        int counter = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;

                int cRow = (idx / COLUMN_COUNT) + i;
                int cCol = (idx % COLUMN_COUNT) + j;

                if (cRow >= 0 && cRow < ROW_COUNT && cCol >= 0 && cCol < COLUMN_COUNT) {
                    int cIdx = cRow * COLUMN_COUNT + cCol;

                    if (mines.contains(cIdx)) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }


    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }


    public void onClickTV(View view){
        running=true;
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        if(icon.equals("flag")){
            System.out.println(tv.getText());
            if (tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                tv.setText("");
//                tv.setBackgroundColor(Color.YELLOW);

            }
            else tv.setText(R.string.flag);
            return;
        }

        if (tv.getText()=="mine"){
            tv.setBackgroundColor(Color.RED);
            //add explosion logic
        }
        tv.setText(String.valueOf(i)+String.valueOf(j));
        if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
        }else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    public void onClickStart(View view) {
        running = true;
    }
    public void switchIcon(View view){
        TextView textView = findViewById(R.id.textView01);
        if(icon.equals("pick")){
            textView.setText(R.string.flag);
            icon="flag";
        }
        else{
            textView.setText(R.string.pick);
            icon="pick";
        }
    }

    public void onClickStop(View view) {
        running = false;
    }
    public void onClickClear(View view) {
        running = false;
        clock = 0;
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textView);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours =clock/3600;
                int minutes = (clock%3600) / 60;
                int seconds = clock%60;
                String time = String.format("%02d", seconds);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}