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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.on_board_rev2.wifi.ParsingData;
import com.example.on_board_rev2.wifi.WifiManager1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

public class common_setting extends AppCompatActivity {
    private int opros_send = 0;
    private int send_diskrt, send_filtr, send_tara, max_weight_int;
    private boolean opros = true, set_comand;
    private ActionBar actionBar;
    private EditText edit_max_auto_weight, edit_discrt, edit_filtr, edit_tara;
    private String max_veight, diskreta, filtr, tara;
    private SharedPreferences shared_prf;
    private WifiManager1 wifiManager;
    private ParsingData parsingData;
    private String _ip, _port;              // для передачи в сокет параметров
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private TextView tara_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_setting);
        init();
        runTimer();        // запускаем таймер для обратных отсчетов
        if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
            startSocet();
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(common_setting.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(common_setting.this, MainActivity.class);
                        startActivity(ooi);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    private void empty() {      // предупреждение
        Toast.makeText(this, "Необхідно ввести усі данні", Toast.LENGTH_SHORT).show();
    }

    private void savePref() {
        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
        editor.putInt(Constants.MAX_WEIGHT, max_weight_int);                 // сохраняем по ключу значение
        editor.putInt(Constants.TARA_KG, Integer.parseInt(edit_tara.getText().toString()));
        tara_read.setText(edit_tara.getText().toString());
        Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
        editor.apply();                                         // сохранить
    }

    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(common_setting.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WifiManager1(common_setting.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
    }


    private void init() {
        int mirror = 0;
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Загальні");
        edit_max_auto_weight = findViewById(R.id.editTextMaxAuto);
        edit_discrt = findViewById(R.id.editTextDisrt);
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        _ip = shared_prf.getString(Constants.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constants.PORT_SET, "1234");
        mirror = shared_prf.getInt(Constants.MAX_WEIGHT, 40000);   // предварительно считаем данные
        edit_max_auto_weight.setText(Integer.toString(mirror));         // инт в строку конвертим
        edit_discrt.setText(diskreta);                                 // считали все параметры которые были сохранены устанавливаем
        edit_filtr = findViewById(R.id.editTextFiltr);
        edit_filtr.setText(filtr);
        edit_tara = findViewById(R.id.editText_Tara);
        parsingData = new ParsingData(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        tara_read = findViewById(R.id.tara_read);
        tara_read.setText(Integer.toString(shared_prf.getInt(Constants.TARA_KG, 0)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.save_and_read, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();
        }
        if (item.getItemId() == R.id.id_saved) {                        // куча проверок перед тем как произвести калибровку
            if (edit_filtr.getText().toString().isEmpty()) {
                empty();
                return super.onOptionsItemSelected(item);             // брейк только с дополнением из за кнопкки
            }
            if (edit_discrt.getText().toString().isEmpty()) {
                empty();
                return super.onOptionsItemSelected(item);
            }
            if (edit_max_auto_weight.getText().toString().isEmpty()) {
                empty();
                return super.onOptionsItemSelected(item);
            }
            if (edit_tara.getText().toString().isEmpty()) {
                empty();
                return super.onOptionsItemSelected(item);
            }
            filtr = edit_filtr.getText().toString();
            diskreta = edit_discrt.getText().toString();
            max_veight = edit_max_auto_weight.getText().toString();
            tara = edit_tara.getText().toString();
            try {                                             // исключение для проверки если в строку ввели какую то дрочь кроме цифр
                send_filtr = Integer.parseInt(filtr);
                send_diskrt = Integer.parseInt(diskreta);
                send_tara = Integer.parseInt(tara);
                max_weight_int = Integer.parseInt(max_veight);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Помилка вводу", Toast.LENGTH_SHORT).show();
            }

            if (send_filtr > 40 || send_filtr < 1) {
                Toast.makeText(this, "Не вірне значення фільтру", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            if (send_diskrt == 10 || send_diskrt == 20 || send_diskrt == 50 || send_diskrt == 100) {
            } else {
                Toast.makeText(this, "Не вірне значення дискрету", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            if (send_tara < 0 || send_tara < 100000) {
            } else {
                Toast.makeText(this, "Не вірне значення тари", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            set_comand = true;
            savePref();
        }
        if (item.getItemId() == R.id.id_reads) {
            opros = true;
            Toast.makeText(this, "Зчитуємо дані", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200); // Запускаем код снова с задержкой в одну секунду
                if (wifiManager != null) {
                    if (wifiManager.probros().inpt_data) {       // через пустую функцию probros добираемся к флагу данных
                        wifiManager.probros().inpt_data = false; // и сбрасываем флаг, чтоб при следующей посылке увидеть новые данные
                        parsingData.Parsing_inpt(wifiManager.probros().data);// отправляем данные на парсинг
                        for (int i = 0; i < 19; i++) {                       // очищаем буфер чтоб не парсить тоже самое
                            wifiManager.probros().data[i] = 0x00;
                        }
                        wifiManager.probros().inpt_str = ""; // а так же очищаем ту строку по которой оприделяем данные (костыль)
                    }

                    if (wifiManager.connect_ok) {        // проверяем флаг наличия соеденения по сокету
                        if (set_comand) {
                            switch (opros_send) {
                                case 0:
                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x0E, send_diskrt));
                                    opros_send++;
                                    break;
                                case 1:
                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x1B, send_tara));
                                    opros_send++;
                                    break;
                                case 2:
                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x10, send_filtr));
                                    opros_send = 0;
                                    set_comand = false;
                                    break;
                            }
                        }
                        if (opros) {
                            switch (opros_send) {
                                case 0:
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_diskr));
                                    opros_send++;
                                    break;
                                case 1:
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_tara));
                                    opros_send++;
                                    break;
                                case 2:
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_filtr));
                                    opros_send = 0;
                                    opros = false;
                                    break;
                            }
                        }
                    }
                    if (parsingData.parsing_command_ok) {     // если пришли данные (в частности команда)
                        parsingData.parsing_command_ok = false;
                        edit_filtr.setText(String.valueOf(parsingData.read_filtr));
                        edit_discrt.setText(String.valueOf(parsingData.read_diskrt));
                    }
                    if (parsingData.parsing_ok) {            // если пришли данные (в частности посылка тары)
                        parsingData.parsing_ok = false;
                        edit_tara.setText(String.valueOf(parsingData.read_tara));
                    }
                }
            }
        });
    }

   /* @Override
    public void onBackPressed() {         //берем управление над кнопкой назад. (на клавиатуру не влияет)
        Intent i = new Intent(common_setting.this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        return;
    }*/

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