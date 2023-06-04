package com.example.on_board_rev2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Static_set_Activity extends AppCompatActivity {
    private EditText mass;
    private SharedPreferences shared_prf;
    private int edit;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_set);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Статична вага");                   // изменили надпись на Акшн баре можно
        mass = findViewById(R.id.editTextText_static);
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        edit = shared_prf.getInt(Constants.STATIC_MAS_AX1,6000);
        mass.setText("" + edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        if (item.getItemId() == R.id.id_save) {
            if (!mass.getText().toString().isEmpty()){
                SharedPreferences.Editor editor = shared_prf.edit();                           // создаем елемент для открытия таблицы на запись
                editor.putInt(Constants.STATIC_MAS_AX1, Integer.parseInt(mass.getText().toString()));                 // сохраняем по ключу значение
                editor.apply();                                         // сохранить
                Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Не збережено", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}