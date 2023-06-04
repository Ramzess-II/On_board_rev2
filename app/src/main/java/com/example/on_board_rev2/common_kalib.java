package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class common_kalib extends AppCompatActivity {

    private ActionBar actionBar;
    private Button  ax1, ax2, ax3, trailer,statics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_kalib);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Калібрування");
        ax1 = findViewById(R.id.button_set_ax1);
        ax2 = findViewById(R.id.button_set_ax2);
        ax3 = findViewById(R.id.button_set_ax3);
        trailer = findViewById(R.id.button_set_trailer);
        statics = findViewById(R.id.button_set_static);
        ax1.setOnClickListener(v -> {
            Intent i = new Intent(common_kalib.this, KalibActivity.class);  // открываем новую активити
            i.putExtra(Constants.NUMBER_AX, 1);
            startActivity(i);
        });
        ax2.setOnClickListener(v -> {
            Intent i = new Intent(common_kalib.this, KalibActivity.class);  // открываем новую активити
            i.putExtra(Constants.NUMBER_AX, 2);
            startActivity(i);
        });
        ax3.setOnClickListener(v -> {
            Intent i = new Intent(common_kalib.this, KalibActivity.class);  // открываем новую активити
            i.putExtra(Constants.NUMBER_AX, 3);
            startActivity(i);
        });
        trailer.setOnClickListener(v -> {
            Intent i = new Intent(common_kalib.this, KalibActivity.class);  // открываем новую активити
            i.putExtra(Constants.NUMBER_AX, 4);
            startActivity(i);
        });
        statics.setOnClickListener(v -> {
            Intent i = new Intent(common_kalib.this, Static_set_Activity.class);  // открываем новую активити
            startActivity(i);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.seting_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        return super.onOptionsItemSelected(item);
    }


}