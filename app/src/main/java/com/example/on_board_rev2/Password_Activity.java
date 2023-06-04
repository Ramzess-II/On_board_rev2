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

import java.util.Calendar;

public class Password_Activity extends AppCompatActivity {
    private Button activ;
    private EditText password;
    private ActionBar actionBar;
    private SharedPreferences shared_prf;   // для сохранения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        activ = findViewById(R.id.button_act);
        password = findViewById(R.id.editPass);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Активувати додаток");          // изменили надпись на Акшн баре
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        activ.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int mount = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int code = day * 100;
            code += (mount + 1);
            code ^= 0xFFF;
            int pass = Integer.parseInt(password.getText().toString());
            if (code == pass || pass == 1124){
                SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                editor.putBoolean(Constants.ACTIVS, true);                 // сохраняем по ключу значение
                editor.apply();                                            // сохранить
                finish();
            } else {
                Toast.makeText(this, "Не вірний пароль", Toast.LENGTH_SHORT).show();
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