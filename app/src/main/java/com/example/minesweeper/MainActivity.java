package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.security.SecureRandom;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private int clock = 0;
    private String icon = "pick";
    private boolean running = false;
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private boolean playerLost = false;
    private boolean playerWon = false;
    private ArrayList<Integer> mines = new ArrayList<Integer>();
    private ArrayList<Integer> flags = new ArrayList<Integer>();
    private int flagCount = 4;
    boolean[] visited = new boolean[ROW_COUNT * COLUMN_COUNT];




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
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
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
        final TextView flagView = (TextView) findViewById(R.id.textViewFlag);



        if(playerWon){
            setPlayerWon();
        }
        running=true;
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        if(icon.equals("flag")){

            if (tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                tv.setText("");
                flagCount++;
                flags.add(n);
            }
            else if(flagCount>0){
                tv.setText(R.string.flag);
                flagCount--;
            }


            flagView.setText(String.valueOf(flagCount));
            return;
        }

        if (tv.getTag()=="mine"){
            running = false;
            tv.setBackgroundColor(Color.BLUE);
            for (int mine:mines){
                cell_tvs.get(mine).setBackgroundColor(Color.BLUE);
            }
            setPlayerLost();
        }
        else if (tv.getCurrentTextColor() == Color.GREEN) {
            visited[n]=true;
            tv.setBackgroundColor(Color.GRAY);
            if(tv.getText().equals("")){
                revealEmpty(n);
            }
            else{
                revealNum(n);
            }
        }
        checkWin();
    }
    private void setPlayerWon(){
        Intent intent = new Intent(MainActivity.this, Results.class);
        intent.putExtra("seconds", clock);
        intent.putExtra("game", true);
        startActivity(intent);
        finish();
    }
    private void setPlayerLost(){
        Intent intent = new Intent(MainActivity.this, Results.class);
        intent.putExtra("seconds", clock);
        intent.putExtra("game", false);
        startActivity(intent);
        finish();
    }
    private void checkWin(){
        int counter=0;
        for(int i =0; i<visited.length; ++i){
            if (!visited[i] && !(cell_tvs.get(i).getTag()=="mine")){
                return;
            }
        }
        setPlayerWon();
//        final TextView flagView = (TextView) findViewById(R.id.textViewFlag);
//        flagView.setText(String.valueOf(counter));
    }
    private boolean checkMines(){

        for (int i : flags){
            if(!(cell_tvs.get(i).getTag()==("mine"))){
                return false;
            }
        }

        return true;
    }

    private void revealEmpty(int idx) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(idx);

        visited[idx] = true;

        while (!queue.isEmpty()) {
            int curr = queue.remove();
            TextView tv = cell_tvs.get(curr);

            // Set background to gray to indicate the cell is revealed
            tv.setBackgroundColor(Color.GRAY);

            if (!tv.getText().equals("")) {
                revealNum(curr);
                continue;
            }

            // Explore neighbors of the current cell
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {continue;}

                    int cRow = (curr / COLUMN_COUNT) + i;
                    int cCol = (curr % COLUMN_COUNT) + j;

                    if (cRow >= 0 && cRow < ROW_COUNT && cCol >= 0 && cCol < COLUMN_COUNT) {

                        int cIdx = cRow * COLUMN_COUNT + cCol;
                        TextView ctv = cell_tvs.get(cIdx);

                        if (!visited[cIdx] && ctv.getCurrentTextColor() == Color.GREEN) {
                            visited[cIdx] = true;

                            if (ctv.getText().equals("")) {
                                queue.add(cIdx);
                            }
                            else{
                                revealNum(cIdx);
                            }
                        }
                    }
                }
            }
        }
    }




    private void revealNum(int idx){
        cell_tvs.get(idx).setTextColor(Color.BLACK);
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
                int seconds = clock;
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