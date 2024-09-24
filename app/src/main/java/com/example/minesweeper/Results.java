package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Results extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        // Retrieve the seconds passed via Intent
        int seconds = getIntent().getIntExtra("seconds", 0);
        boolean game = getIntent().getBooleanExtra("game",false);

        TextView tv = findViewById(R.id.textView00);
        String win = String.format("Used %d seconds!\nYou won.\nGood job!", seconds);
        String lose = String.format("Used %d seconds!\nYou Lost.\nTry again!", seconds);
        if(game){
            tv.setText(win);
        }
        else tv.setText(lose);
//        tv.setText("L");

    }
    public void restart(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
