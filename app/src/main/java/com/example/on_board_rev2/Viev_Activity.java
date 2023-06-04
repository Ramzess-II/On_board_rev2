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
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Viev_Activity extends AppCompatActivity {
    private TextView number, data, common_massa, axis_1, axis_2, axis_3, trailer,num_ax4;
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView; // это нижнее меню

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viev);
        number = findViewById(R.id.textView_number);
        data = findViewById(R.id.textViewdata_tim);
        common_massa = findViewById(R.id.textViewcommon);
        axis_1 = findViewById(R.id.textViewax1);
        axis_2 = findViewById(R.id.textViewax2);
        axis_3 = findViewById(R.id.textViewax3);
        trailer = findViewById(R.id.textViewtraler);
        num_ax4 = findViewById(R.id.num_ax4);
        Intent i = getIntent();
        number.setText(i.getStringExtra(Constants.NUMBRS));
        data.setText(i.getStringExtra(Constants.DATA_TIM));
        common_massa.setText(i.getStringExtra(Constants.COMMON_MASSA));
        axis_1.setText(i.getStringExtra(Constants.MAS_AX1));
        axis_2.setText(i.getStringExtra(Constants.MAS_AX2));
        axis_3.setText(i.getStringExtra(Constants.MAS_AX3));
        trailer.setText(i.getStringExtra(Constants.MAS_TRALER));
        num_ax4.setText("Причіп №" + i.getStringExtra(Constants.NUM_MAS_TRALER));
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("№ " + i.getStringExtra(Constants.NUMBRS));    // изменили надпись на Акшн баре
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Viev_Activity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(Viev_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Viev_Activity.this, MainActivity.class);
                        startActivity(ooi);
                        return true;
                }
                return false;
            }
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