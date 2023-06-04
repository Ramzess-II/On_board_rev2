package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.on_board_rev2.wifi.ParsingData;
import com.example.on_board_rev2.wifi.WifiManager1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Trailer_activity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ActionBar actionBar;
    private boolean change,send_data_read = true, send_data;
    private Menu mMenuItem;
    private CheckBox ch1, ch2, ch3, ch4, ch5;
    private EditText text1, text2, text3, text4, text5;
    private SharedPreferences shared_prf;
    private int count_trailer = 1,net_count_trailer;
    private WifiManager1 wifiManager;
    private ParsingData parsingData;
    private String _ip, _port;              // для передачи в сокет параметров


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        init();
        enable_disable(false);
        runTimer();                        // запускаем таймер для обратных отсчетов
        if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
            startSocet();
        }
        ch1.setOnClickListener(view -> {
            fals_check();
            ch1.setChecked(true);
            count_trailer = 1;
        });
        ch2.setOnClickListener(view -> {
            fals_check();
            ch2.setChecked(true);
            count_trailer = 2;
        });
        ch3.setOnClickListener(view -> {
            fals_check();
            ch3.setChecked(true);
            count_trailer = 3;
        });
        ch4.setOnClickListener(view -> {
            fals_check();
            ch4.setChecked(true);
            count_trailer = 4;
        });
        ch5.setOnClickListener(view -> {
            fals_check();
            ch5.setChecked(true);
            count_trailer = 5;
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Trailer_activity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(Trailer_activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Trailer_activity.this, MainActivity.class);
                        startActivity(ooi);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    private void fals_check(){
        ch1.setChecked(false);
        ch2.setChecked(false);
        ch3.setChecked(false);
        ch4.setChecked(false);
        ch5.setChecked(false);
    }

    private void init(){
        parsingData = new ParsingData(this);
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        _ip = shared_prf.getString(Constants.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constants.PORT_SET, "1234");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Причіпи");                     // изменили надпись на Акшн баре можно
        ch1 = findViewById(R.id.checkBox_1);
        ch2 = findViewById(R.id.checkBox_2);
        ch3 = findViewById(R.id.checkBox_3);
        ch4 = findViewById(R.id.checkBox_4);
        ch5 = findViewById(R.id.checkBox_5);
        text1 = findViewById(R.id.editText_note1);
        text2 = findViewById(R.id.editText_note2);
        text3 = findViewById(R.id.editText_note3);
        text4 = findViewById(R.id.editText_note4);
        text5 = findViewById(R.id.editText_note5);
        text1.setText(shared_prf.getString(Constants.NOTE1," "));
        text2.setText(shared_prf.getString(Constants.NOTE2," "));
        text3.setText(shared_prf.getString(Constants.NOTE3," "));
        text4.setText(shared_prf.getString(Constants.NOTE4," "));
        text5.setText(shared_prf.getString(Constants.NOTE5," "));
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
        if (item.getItemId() == R.id.id_saveds) {
            if (net_count_trailer != count_trailer){
                send_data = true;
            }
            SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
            editor.putString(Constants.NOTE1, text1.getText().toString());
            editor.putString(Constants.NOTE2, text2.getText().toString());
            editor.putString(Constants.NOTE3, text3.getText().toString());
            editor.putString(Constants.NOTE4, text4.getText().toString());
            editor.putString(Constants.NOTE5, text5.getText().toString());
            editor.apply();
            Toast.makeText(Trailer_activity.this, "Збережено", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.id_rename) {
            change = !change;
            enable_disable(change);
            if (change) {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_red_24);    // через переменную изменяем картинку. индекс это позиция картинки
            } else {
                mMenuItem.getItem(0).setIcon(R.drawable.ic_baseline_create_24);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void enable_disable(boolean boll){
        if(boll){
            text1.setEnabled(true);
            text2.setEnabled(true);
            text3.setEnabled(true);
            text4.setEnabled(true);
            text5.setEnabled(true);
        }else {
            text1.setEnabled(false);
            text2.setEnabled(false);
            text3.setEnabled(false);
            text4.setEnabled(false);
            text5.setEnabled(false);
        }
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 500); // Запускаем код снова с задержкой в одну секунду
                if (wifiManager != null) {
                    if (wifiManager.probros().inpt_data) {       // через пустую функцию probros добираемся к флагу данных
                        wifiManager.probros().inpt_data = false; // и сбрасываем флаг, чтоб при следующей посылке увидеть новые данные
                        parsingData.Parsing_inpt(wifiManager.probros().data);// отправляем данные на парсинг
                        for (int i = 0; i < 19; i++) {                       // очищаем буфер чтоб не парсить тоже самое
                            wifiManager.probros().data[i] = 0x00;
                        }
                        wifiManager.probros().inpt_str = ""; // а так же очищаем ту строку по которой оприделяем данные (костыль)
                    }

                    if (wifiManager.connect_ok ) {        // проверяем флаг наличия соеденения по сокету
                        if (send_data_read) {
                            send_data_read = false;
                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_count_trailer));
                        }
                        if(send_data){
                            send_data = false;
                            wifiManager.probros().senMesage(parsingData.send32_bit((byte)0x1C,count_trailer));
                        }
                    }
                    if (parsingData.parsing_ok) {            // если пришли данные (в частности посылка номера прицепа)
                        parsingData.parsing_ok = false;
                        net_count_trailer = parsingData.read_num_trailer;
                        count_trailer = net_count_trailer;
                        set_num(net_count_trailer);
                    }
                }
            }
        });
    }

    private void set_num(int num){
        switch (num){
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
        }
    }

    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(Trailer_activity.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WifiManager1(Trailer_activity.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wifiManager != null)
            wifiManager.socketClose();     // когда переходим в паузу убить сокет
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();              // когда выходим из активити убить сокет
        if (wifiManager != null) wifiManager.socketClose();
    }
}