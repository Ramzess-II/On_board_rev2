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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.on_board_rev2.data_base.AppExecuter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Info_activity extends AppCompatActivity {
    private TextView version,mir_text,developer,pashalka_view;
    private EditText data, number, password;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private SharedPreferences shared_prf;
    private Boolean stop_change_dat,stop_change_num,err36;
    private ActionBar actionBar;
    private String mirror_dat, mirror_num;
    private Button aktiv;
    private int new_data = 0, pashalka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Інформація");
        version = findViewById(R.id.Version_text);
        number = findViewById(R.id.edit_number_id);
        developer = findViewById(R.id.developer);
        pashalka_view = findViewById(R.id.pashalka);
        data = findViewById(R.id.data_err36);
        mir_text = findViewById(R.id.textMirr);
        password = findViewById(R.id.editTextPassword);
        aktiv = findViewById(R.id.button_password);
        version.setText("Ver: SBV-04_2.6");
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        stop_change_dat = shared_prf.getBoolean(Constants.FLAG_CHANGE, false);
        stop_change_num = shared_prf.getBoolean(Constants.NUM_CHANGE, false);
        if(stop_change_dat) {                   // считали, если флаг даты сработал то
            data.setVisibility(View.GONE);      // спрятать ввод даты
            mir_text.setVisibility(View.VISIBLE);  // показать просто текст
            mir_text.setText(shared_prf.getString(Constants.DATA_ERR36, "01.01.2030"));
        }
        if(stop_change_num) {
            number.setClickable(false);
            number.setFocusable(false);
        }
        err36 = shared_prf.getBoolean(Constants.ERR36_OK, false);
        if (err36){
            password.setVisibility(View.VISIBLE);
            aktiv.setVisibility(View.VISIBLE);
        }
        number.setText(shared_prf.getString(Constants.NUMBER_DEVICE, "МОПД0001"));
        data.setText(shared_prf.getString(Constants.DATA_ERR36, "01.01.2030"));
        mirror_dat = data.getText().toString();     // временное хранилище, если мы изменили дату то она будет отличаться
        mirror_num  = number.getText().toString();
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Info_activity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(Info_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Info_activity.this, MainActivity.class);
                        startActivity(ooi);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
        aktiv.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int mount = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int code = day * 100;
            code += (mount + 1);
            code ^= 0xFFF;
            int pass = Integer.parseInt(password.getText().toString());
            if (code == pass){
                SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                editor.putBoolean(Constants.ERR36_OK, false);                 // сохраняем по ключу значение
                editor.putBoolean(Constants.FLAG_CHANGE, false);
                editor.putBoolean(Constants.NUM_CHANGE, false);
                editor.putString(Constants.DATA_ERR36, "01.01.2030");
                editor.apply();                                            // сохранить
                finish();
            } else {
                Toast.makeText(this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
            }
        });
        version.setOnClickListener(view -> {
            new_data ++;
            if (new_data >= 6){
                SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                editor.putBoolean(Constants.FLAG_CHANGE, false);
                editor.putBoolean(Constants.NUM_CHANGE, false);
                editor.apply();                                            // сохранить
                Toast.makeText(Info_activity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });
        developer.setOnClickListener(view -> {
            pashalka ++;
            if (pashalka >= 6){
                pashalka_view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        pashalka = 0;
        new_data = 0;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                   // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        if (item.getItemId() == R.id.id_save){
            if (!data.getText().toString().isEmpty()) {
                if (!data.getText().toString().equals(mirror_dat)) {      // если отличается от того что было в начале заблочить
                    SharedPreferences.Editor editor = shared_prf.edit();                           // создаем елемент для открытия таблицы на запись
                    editor.putBoolean(Constants.FLAG_CHANGE, true);                               // сохраняем по ключу значение
                    editor.putString(Constants.DATA_ERR36, data.getText().toString());
                    editor.apply();
                    Toast.makeText(Info_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Info_activity.this, "Заповніть поле дата", Toast.LENGTH_SHORT).show();
            }
            if (!number.getText().toString().equals(mirror_num)){
                SharedPreferences.Editor editor = shared_prf.edit();                           // создаем елемент для открытия таблицы на запись
                editor.putString(Constants.NUMBER_DEVICE, number.getText().toString());
                editor.putBoolean(Constants.NUM_CHANGE, true);
                editor.apply();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}