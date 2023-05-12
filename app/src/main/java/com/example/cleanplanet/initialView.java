package com.example.cleanplanet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class initialView extends AppCompatActivity {
    Button buttonOnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_view);

        buttonOnMain = findViewById(R.id.button_main);
        buttonOnMain.setTextSize(10);
        buttonOnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButton();
            }
        });
    }

    private void onClickButton() {
        Intent intent = new Intent(this, mainView.class);
        startActivity(intent);
    }

}