package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Setting_Activity extends AppCompatActivity {
    private ActionBar actionBar;
    private int counter_set = 0;
    private TextView button;
    private Button common_set,net,info,trailer;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
        button.setOnClickListener(v -> {
                counter_set++;
            if (counter_set == 5) {
                Intent i = new Intent(Setting_Activity.this, common_kalib.class);  // открываем новую активити
                startActivity(i);
            }
        });
        common_set.setOnClickListener(view -> {
            Intent i = new Intent(Setting_Activity.this, common_setting.class);  // открываем новую активити
            startActivity(i);
        });
        net.setOnClickListener(view -> {
            Intent i = new Intent(Setting_Activity.this, Net_Activity.class);  // открываем новую активити
            startActivity(i);
        });
        info.setOnClickListener(view -> {
            Intent i = new Intent(Setting_Activity.this, Info_activity.class);  // открываем новую активити
            startActivity(i);
        });
        trailer.setOnClickListener(view -> {
            Intent i = new Intent(Setting_Activity.this, Trailer_activity.class);  // открываем новую активити
            startActivity(i);
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent i = new Intent(Setting_Activity.this, MainActivity.class);
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_data_base:
                        Intent ii = new Intent(Setting_Activity.this, DataBaseActivity.class);
                        startActivity(ii);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {  //берем управление над кнопкой назад. (на клавиатуру не влияет)
        Intent i = new Intent(Setting_Activity.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter_set = 0;
    }

    private void init() {
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Налаштування");                   // изменили надпись на Акшн баре можно
        net = findViewById(R.id.button_net);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        button = findViewById(R.id.button_kalib);
        info  = findViewById(R.id.button_info);
        common_set = findViewById(R.id.button_common_set);
        trailer = findViewById(R.id.button_trailer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.seting_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            Intent ooi = new Intent(Setting_Activity.this, MainActivity.class);
            startActivity(ooi);
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}