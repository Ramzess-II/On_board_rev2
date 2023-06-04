package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class Net_Activity extends AppCompatActivity {
    private EditText ed_ip1,ed_ip2,ed_ip3,ed_ip4,ed_ip5,ed_ip6,ed_ip7,ed_ip8;
    private EditText ed_port1, ed_port2, ed_port3, ed_port4, ed_port5, ed_port6,ed_port7,ed_port8;
    private EditText nam_auto1, nam_auto2, nam_auto3, nam_auto4, nam_auto5, nam_auto6, nam_auto7, nam_auto8;
    private CheckBox ch1, ch2, ch3, ch4, ch5, ch6,ch7,ch8;
    private ActionBar actionBar;
    private SharedPreferences shared_prf_pub;
    private int poz;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private Menu mMenuItem;
    private Boolean change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        init();           // инициализация  переменных
        read_sh();        // считать из памяти порты и ип
        set_check(poz);   // установить галку где надо
        small_display();
        change_nums (change);
        ch1.setOnClickListener(view -> {   // слушатели галок
            fals_check();                  // очистить все
            ch1.setChecked(true);          // установить только один
            poz = 1;                       // выбрать новую позицию
        });
        ch2.setOnClickListener(view -> {
            fals_check();
            ch2.setChecked(true);
            poz = 2;
        });
        ch3.setOnClickListener(view -> {
            fals_check();
            ch3.setChecked(true);
            poz = 3;
        });
        ch4.setOnClickListener(view -> {
            fals_check();
            ch4.setChecked(true);
            poz = 4;
        });
        ch5.setOnClickListener(view -> {
            fals_check();
            ch5.setChecked(true);
            poz = 5;
        });
        ch6.setOnClickListener(view -> {
            fals_check();
            ch6.setChecked(true);
            poz = 6;
        });
        ch7.setOnClickListener(view -> {
            fals_check();
            ch7.setChecked(true);
            poz = 7;
        });
        ch8.setOnClickListener(view -> {
            fals_check();
            ch8.setChecked(true);
            poz = 8;
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Net_Activity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(Net_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Net_Activity.this, MainActivity.class);
                        startActivity(ooi);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    void small_display() {     // проверяем размер экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenHeight = point.y;
        if (screenHeight < 1000) {  // если по длине меньше 1000 то прячем bottomNavigationView
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    private void init() {
        shared_prf_pub = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Мережа");                      // изменили надпись на Акшн баре можно
        ed_ip1 = findViewById(R.id.edit_ip1);
        ed_ip2 = findViewById(R.id.edit_ip2);
        ed_ip3 = findViewById(R.id.edit_ip3);
        ed_ip4 = findViewById(R.id.edit_ip4);
        ed_ip5 = findViewById(R.id.edit_ip5);
        ed_ip6 = findViewById(R.id.edit_ip6);
        ed_ip7 = findViewById(R.id.edit_ip7);
        ed_ip8 = findViewById(R.id.edit_ip8);
        ed_port1 = findViewById(R.id.edit_port1);
        ed_port2 = findViewById(R.id.edit_port2);
        ed_port3 = findViewById(R.id.edit_port3);
        ed_port4 = findViewById(R.id.edit_port4);
        ed_port5 = findViewById(R.id.edit_port5);
        ed_port6 = findViewById(R.id.edit_port6);
        ed_port7 = findViewById(R.id.edit_port7);
        ed_port8 = findViewById(R.id.edit_port8);
        ch1 = findViewById(R.id.checkBox1);
        ch2 = findViewById(R.id.checkBox2);
        ch3 = findViewById(R.id.checkBox3);
        ch4 = findViewById(R.id.checkBox4);
        ch5 = findViewById(R.id.checkBox5);
        ch6 = findViewById(R.id.checkBox6);
        ch7 = findViewById(R.id.checkBox7);
        ch8 = findViewById(R.id.checkBox8);
        nam_auto1 = findViewById(R.id.auto_name1);
        nam_auto2 = findViewById(R.id.auto_name2);
        nam_auto3 = findViewById(R.id.auto_name3);
        nam_auto4 = findViewById(R.id.auto_name4);
        nam_auto5 = findViewById(R.id.auto_name5);
        nam_auto6 = findViewById(R.id.auto_name6);
        nam_auto7 = findViewById(R.id.auto_name7);
        nam_auto8 = findViewById(R.id.auto_name8);
        poz = shared_prf_pub.getInt(Constants.POZ_SET, 1); // считать позицию
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
    }

    private void read_sh (){  // считать данные
        ed_ip1.setText(shared_prf_pub.getString(Constants.IP_SET1, "192.168.4.1"));
        ed_port1.setText(shared_prf_pub.getString(Constants.PORT_SET1, "1234"));
        ed_ip2.setText(shared_prf_pub.getString(Constants.IP_SET2, "192.168.4.2"));
        ed_port2.setText(shared_prf_pub.getString(Constants.PORT_SET2, "1234"));
        ed_ip3.setText(shared_prf_pub.getString(Constants.IP_SET3, "192.168.4.3"));
        ed_port3.setText(shared_prf_pub.getString(Constants.PORT_SET3, "1234"));
        ed_ip4.setText(shared_prf_pub.getString(Constants.IP_SET4, "192.168.4.4"));
        ed_port4.setText(shared_prf_pub.getString(Constants.PORT_SET4, "1234"));
        ed_ip5.setText(shared_prf_pub.getString(Constants.IP_SET5, "192.168.4.5"));
        ed_port5.setText(shared_prf_pub.getString(Constants.PORT_SET5, "1234"));
        ed_ip6.setText(shared_prf_pub.getString(Constants.IP_SET6, "192.168.4.6"));
        ed_port6.setText(shared_prf_pub.getString(Constants.PORT_SET6, "1234"));
        ed_ip7.setText(shared_prf_pub.getString(Constants.IP_SET7, "192.168.4.7"));
        ed_port7.setText(shared_prf_pub.getString(Constants.PORT_SET7, "1234"));
        ed_ip8.setText(shared_prf_pub.getString(Constants.IP_SET8, "192.168.4.8"));
        ed_port8.setText(shared_prf_pub.getString(Constants.PORT_SET8, "1234"));
        nam_auto1.setText(shared_prf_pub.getString(Constants.AUTO_NAME1,"AE0241BB"));
        nam_auto2.setText(shared_prf_pub.getString(Constants.AUTO_NAME2,"AK9265AK"));
        nam_auto3.setText(shared_prf_pub.getString(Constants.AUTO_NAME3,"AA4215AB"));
        nam_auto4.setText(shared_prf_pub.getString(Constants.AUTO_NAME4,"AN2202BA"));
        nam_auto5.setText(shared_prf_pub.getString(Constants.AUTO_NAME5,"BT5678AA"));
        nam_auto6.setText(shared_prf_pub.getString(Constants.AUTO_NAME6,"BH1488XA"));
        nam_auto7.setText(shared_prf_pub.getString(Constants.AUTO_NAME7,"BC3325AB"));
        nam_auto8.setText(shared_prf_pub.getString(Constants.AUTO_NAME8,"CB3378BK"));

    }

    private void set_check(int pozition){
        fals_check();   // сбросить все флажки
        switch (pozition){  // в зависимости от позиции установить новый
            case 1:
                ch1.setChecked(true);
                break;
            case 2:
                ch2.setChecked(true);
                break;
            case 3:
                ch3.setChecked(true);
                break;
            case 4:
                ch4.setChecked(true);
                break;
            case 5:
                ch5.setChecked(true);
                break;
            case 6:
                ch6.setChecked(true);
                break;
            case 7:
                ch7.setChecked(true);
                break;
            case 8:
                ch8.setChecked(true);
                break;
        }
    }

    private void fals_check(){   // сброс всех
        ch1.setChecked(false);
        ch2.setChecked(false);
        ch3.setChecked(false);
        ch4.setChecked(false);
        ch5.setChecked(false);
        ch6.setChecked(false);
        ch7.setChecked(false);
        ch8.setChecked(false);
    }

    private void save_poz(int pozt){   // сохранить новую позицию
        poz = pozt;
        SharedPreferences.Editor editor = shared_prf_pub.edit();       // создаем елемент для открытия таблицы на запись
        editor.putInt(Constants.POZ_SET, poz);
        editor.apply();
    }

    private void save_sh (){   // записать в память новые данные
        SharedPreferences.Editor editor = shared_prf_pub.edit();       // создаем елемент для открытия таблицы на запись
        editor.putString(Constants.IP_SET1, ed_ip1.getText().toString());                 // сохраняем по ключу значение
        editor.putString(Constants.PORT_SET1, ed_port1.getText().toString());
        editor.putString(Constants.IP_SET2, ed_ip2.getText().toString());
        editor.putString(Constants.PORT_SET2, ed_port2.getText().toString());
        editor.putString(Constants.IP_SET3, ed_ip3.getText().toString());
        editor.putString(Constants.PORT_SET3, ed_port3.getText().toString());
        editor.putString(Constants.IP_SET4, ed_ip4.getText().toString());
        editor.putString(Constants.PORT_SET4, ed_port4.getText().toString());
        editor.putString(Constants.IP_SET5, ed_ip5.getText().toString());
        editor.putString(Constants.PORT_SET5, ed_port5.getText().toString());
        editor.putString(Constants.IP_SET6, ed_ip6.getText().toString());
        editor.putString(Constants.PORT_SET6, ed_port6.getText().toString());
        editor.putString(Constants.IP_SET7, ed_ip7.getText().toString());
        editor.putString(Constants.PORT_SET7, ed_port7.getText().toString());
        editor.putString(Constants.IP_SET8, ed_ip8.getText().toString());
        editor.putString(Constants.PORT_SET8, ed_port8.getText().toString());
        editor.putString(Constants.AUTO_NAME1, nam_auto1.getText().toString());
        editor.putString(Constants.AUTO_NAME2, nam_auto2.getText().toString());
        editor.putString(Constants.AUTO_NAME3, nam_auto3.getText().toString());
        editor.putString(Constants.AUTO_NAME4, nam_auto4.getText().toString());
        editor.putString(Constants.AUTO_NAME5, nam_auto5.getText().toString());
        editor.putString(Constants.AUTO_NAME6, nam_auto6.getText().toString());
        editor.putString(Constants.AUTO_NAME7, nam_auto7.getText().toString());
        editor.putString(Constants.AUTO_NAME8, nam_auto8.getText().toString());
        editor.apply();
    }

    private void edit_new (int pozit) {       // сохраняем отдельно используемый элемент
        SharedPreferences.Editor editor = shared_prf_pub.edit();       // создаем елемент для открытия таблицы на запись
        switch (pozit){
            case 1:
                editor.putString(Constants.IP_SET, ed_ip1.getText().toString());                 // сохраняем по ключу значение
                editor.putString(Constants.PORT_SET, ed_port1.getText().toString());
                editor.apply();
                break;
            case 2:
                editor.putString(Constants.IP_SET, ed_ip2.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port2.getText().toString());
                editor.apply();
                break;
            case 3:
                editor.putString(Constants.IP_SET, ed_ip3.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port3.getText().toString());
                editor.apply();
                break;
            case 4:
                editor.putString(Constants.IP_SET, ed_ip4.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port4.getText().toString());
                editor.apply();
                break;
            case 5:
                editor.putString(Constants.IP_SET, ed_ip5.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port5.getText().toString());
                editor.apply();
                break;
            case 6:
                editor.putString(Constants.IP_SET, ed_ip6.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port6.getText().toString());
                editor.apply();
                break;
            case 7:
                editor.putString(Constants.IP_SET, ed_ip7.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port7.getText().toString());
                editor.apply();
                break;
            case 8:
                editor.putString(Constants.IP_SET, ed_ip8.getText().toString());
                editor.putString(Constants.PORT_SET, ed_port8.getText().toString());
                editor.apply();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_rename, menu);   // добавляем меню в наше активити
        mMenuItem = menu;                                      // переменная чтоб можно было получить доступ к картинке в меню
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        if (item.getItemId() == R.id.id_saveds){
            edit_new(poz);        // записываем новые данные в используемую ячейку
            save_poz(poz);        // сохраняем новую позицию
            save_sh();            // сохраняем все остальные данные
            Toast.makeText(Net_Activity.this, "Збережено", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (item.getItemId() == R.id.id_rename) {
            change = !change;
            change_nums (change);
            if (change) {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_red_24);    // через переменную изменяем картинку. индекс это позиция картинки
            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_24);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void change_nums ( boolean changes) {           // блокируем ввод данных. тупой код, согласен
        if (changes) {
            ed_ip1.setEnabled(true);
            ed_ip2.setEnabled(true);
            ed_ip3.setEnabled(true);
            ed_ip4.setEnabled(true);
            ed_ip5.setEnabled(true);
            ed_ip6.setEnabled(true);
            ed_ip7.setEnabled(true);
            ed_ip8.setEnabled(true);
            ed_port1.setEnabled(true);
            ed_port2.setEnabled(true);
            ed_port3.setEnabled(true);
            ed_port4.setEnabled(true);
            ed_port5.setEnabled(true);
            ed_port6.setEnabled(true);
            ed_port7.setEnabled(true);
            ed_port8.setEnabled(true);
            nam_auto1.setEnabled(true);
            nam_auto2.setEnabled(true);
            nam_auto3.setEnabled(true);
            nam_auto4.setEnabled(true);
            nam_auto5.setEnabled(true);
            nam_auto6.setEnabled(true);
            nam_auto7.setEnabled(true);
            nam_auto8.setEnabled(true);
        }else {
            ed_ip1.setEnabled(false);
            ed_ip2.setEnabled(false);
            ed_ip3.setEnabled(false);
            ed_ip4.setEnabled(false);
            ed_ip5.setEnabled(false);
            ed_ip6.setEnabled(false);
            ed_ip7.setEnabled(false);
            ed_ip8.setEnabled(false);
            ed_port1.setEnabled(false);
            ed_port2.setEnabled(false);
            ed_port3.setEnabled(false);
            ed_port4.setEnabled(false);
            ed_port5.setEnabled(false);
            ed_port6.setEnabled(false);
            ed_port7.setEnabled(false);
            ed_port8.setEnabled(false);
            nam_auto1.setEnabled(false);
            nam_auto2.setEnabled(false);
            nam_auto3.setEnabled(false);
            nam_auto4.setEnabled(false);
            nam_auto5.setEnabled(false);
            nam_auto6.setEnabled(false);
            nam_auto7.setEnabled(false);
            nam_auto8.setEnabled(false);
        }
    }
}