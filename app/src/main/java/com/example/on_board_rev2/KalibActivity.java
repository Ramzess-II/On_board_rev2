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

public class KalibActivity extends AppCompatActivity {
    private byte[] to_send_buf = new byte[20];
    private int count_send, tip_send = 0, opros_send = 0;
    private int send_massa, send_koef;
    private boolean zero, opros = true, set_comand;
    private int number_axis, var;
    private ActionBar actionBar;
    private EditText edit_massa, edit_koef, edit_max_axis;
    private TextView out_massa, tx8, tx9;
    private Button zero_zero, read, kalib;
    private Switch massa_koef;
    private String massa, koef, max_axis;
    private SharedPreferences shared_prf;
    private WifiManager1 wifiManager;
    private ParsingData parsingData;
    private String _ip, _port;              // для передачи в сокет параметров
    private int number_koef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalib);
        init();
        runTimer();        // запускаем таймер для обратных отсчетов
        zero_zero.setOnClickListener(view -> {
            zero = true;
        });
        read.setOnClickListener(view -> {
            opros = true;
        });
        kalib.setOnClickListener(view -> {        // куча проверок перед тем как произвести калибровку
            if (edit_max_axis.getText().toString().isEmpty()) {
                Toast.makeText(this, "Необхідно ввести МАХ вагу", Toast.LENGTH_SHORT).show();
                return;
            }
            max_axis = edit_max_axis.getText().toString();
            if (var == 1) {       //что мы делаем калибруем/настраиваем
                if (massa_koef.isChecked()) {       // смотрим в каком положении переключатель
                    if (edit_koef.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Необхідно ввести калібрувальний коєфіціент", Toast.LENGTH_SHORT).show();
                        return;
                    }  // и проверяем на пустоту данные
                    koef = edit_koef.getText().toString();
                    float koefcnt = Float.valueOf(koef);  // преобразует строку во флоат
                    send_koef = Float.floatToIntBits(koefcnt); // делает из флоата инт так же как бы мы делали через юнион в с++
                } else {
                    if (edit_massa.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Необхідно ввести калібрувальну вагу", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    massa = edit_massa.getText().toString();   // проверить на отрицательное число и 0!!
                    send_massa = Integer.parseInt(massa);
                }
            }
            set_comand = true;
            savePref();
        });

        if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
            startSocet();
        }
    }

    private void empty() {      // предупреждение
        Toast.makeText(this, "Необхідно ввести усі данні", Toast.LENGTH_SHORT).show();
    }

    private void savePref() {
        SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
        switch (number_axis) {
            case 1:
                editor.putInt(Constants.MAX_AX1, Integer.parseInt(max_axis));             // сохраняем по ключу значение
                break;
            case 2:
                editor.putInt(Constants.MAX_AX2, Integer.parseInt(max_axis));             // сохраняем по ключу значение
                break;
            case 3:
                editor.putInt(Constants.MAX_AX3, Integer.parseInt(max_axis));             // сохраняем по ключу значение
                break;
            case 4:
                editor.putInt(Constants.MAX_TRAILER, Integer.parseInt(max_axis));             // сохраняем по ключу значение
                break;
        }
        Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
        editor.apply();                                         // сохранить
    }

    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(KalibActivity.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WifiManager1(KalibActivity.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
    }

    public void switchh(View view) {           // это слушатель нажатия по выключателю
        //edit_koef.setText("");                 // очистить поля при смене выключателя
        // edit_massa.setText("");
        if (massa_koef.isChecked()) {          // проверяем включен или выключен при нажатии
            massa_koef.setText("Коєф");        // устанавливаем новое имя
            edit_koef.setClickable(true);      // одни пункты ввода делаем доступными
            edit_koef.setFocusable(true);
            edit_koef.setFocusableInTouchMode(true);
            edit_massa.setFocusable(false);    // а другие запрещаем вводить
            edit_massa.setClickable(false);
            edit_massa.setFocusableInTouchMode(false);
        } else {
            massa_koef.setText("Вага");        // ну и в обратном порядке
            edit_koef.setClickable(false);
            edit_koef.setFocusable(false);
            edit_massa.setFocusable(true);
            edit_massa.setClickable(true);
            edit_massa.setFocusableInTouchMode(true);
        }
    }

    private void init() {
        int mirror = 0;
        Intent in = getIntent();
        number_axis = in.getIntExtra(Constants.NUMBER_AX, 1);  // получаем данные какую ось калибруем
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        if (number_axis != 4) actionBar.setTitle("Вісь №" + number_axis);
        if (number_axis == 4) actionBar.setTitle("Причіп");
        zero_zero = findViewById(R.id.button_sets);
        read = findViewById(R.id.button_read);
        kalib = findViewById(R.id.buttonkalib);
        edit_massa = findViewById(R.id.editTextMassa);
        edit_koef = findViewById(R.id.editTextKoef);
        edit_max_axis = findViewById(R.id.editTextMaxAxis);
        out_massa = findViewById(R.id.textViewMass);
        massa_koef = findViewById(R.id.switchkoef);
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences
        _ip = shared_prf.getString(Constants.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constants.PORT_SET, "1234");
        mirror = shared_prf.getInt(Constants.MAX_WEIGHT, 40000);   // предварительно считаем данные
        //diskreta = shared_prf.getString(Constants.DISKRET, "20");   // предварительно считаем данные ne nado!
        //filtr = shared_prf.getString(Constants.FILTR, "5");   // предварительно считаем данные
        switch (number_axis) {        /// проверяем что нам передали и в зависимости от этого выбираем какую ось показать
            case 1:
                mirror = shared_prf.getInt(Constants.MAX_AX1, 10000);   // предварительно считаем данные
                break;
            case 2:
                mirror = shared_prf.getInt(Constants.MAX_AX2, 10000);   // предварительно считаем данные
                break;
            case 3:
                mirror = shared_prf.getInt(Constants.MAX_AX3, 10000);   // предварительно считаем данные
                break;
            case 4:
                mirror = shared_prf.getInt(Constants.MAX_TRAILER, 22000);   // предварительно считаем данные
                break;
        }
        edit_max_axis.setText(Integer.toString(mirror));
        edit_koef.setClickable(false);     // и прячем ввод коэф
        edit_koef.setFocusable(false);
        tx8 = findViewById(R.id.textView8);
        tx9 = findViewById(R.id.textView9);
        var = in.getIntExtra(Constants.KALIB_SET, 1);  // есть два режима или чисто настройки или еще и калибровка
        if (var == 0) {         // если чисто настройки то спрятать часть пунктов
            tx8.setVisibility(View.GONE);
            tx9.setVisibility(View.GONE);
            edit_koef.setVisibility(View.GONE);
            edit_massa.setVisibility(View.GONE);
            massa_koef.setVisibility(View.GONE);
            kalib.setText("Зберегти");
        }
        parsingData = new ParsingData(this);
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
            if (edit_max_axis.getText().toString().isEmpty()) {
                Toast.makeText(this, "Необхідно ввести МАХ вагу", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            max_axis = edit_max_axis.getText().toString();
            savePref();
        }
        return super.onOptionsItemSelected(item);
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 100); // Запускаем код снова с задержкой в одну секунду
                if (wifiManager != null) {
                    if (wifiManager.probros().inpt_data) {       // через пустую функцию probros добираемся к флагу данных
                        count_send = 40;                          // взводим таймер чтоб данные сразу не пропали
                        wifiManager.probros().inpt_data = false; // и сбрасываем флаг, чтоб при следующей посылке увидеть новые данные
                        parsingData.Parsing_inpt(wifiManager.probros().data);// отправляем данные на парсинг
                        for (int i = 0; i < 19; i++) {                       // очищаем буфер чтоб не парсить тоже самое
                            wifiManager.probros().data[i] = 0x00;
                        }
                        wifiManager.probros().inpt_str = ""; // а так же очищаем ту строку по которой оприделяем данные (костыль)
                    }
                    // если счетчик больше нудя то показываем что есть соеденение
                    if (count_send > 0) {
                        count_send--;

                        if (parsingData.parsing_ok) {     // если значение мах веса для оси не ноль и данные правильные
                            float dat = 0;
                            switch (number_axis) {
                                case 1:
                                    dat = ((float) parsingData.axis_1 / 1000);    // преобразуем число в флоат
                                    break;
                                case 2:
                                    dat = ((float) parsingData.axis_2 / 1000);    // преобразуем число в флоат
                                    break;
                                case 3:
                                    dat = ((float) parsingData.axis_3 / 1000);    // преобразуем число в флоат
                                    break;
                                case 4:
                                    dat = ((float) parsingData.axis_trailer / 1000);    // преобразуем число в флоат
                                    break;
                            }
                            String print = String.format("%.3f т", dat);        // форматнем в вид Тонны
                            out_massa.setText(print);                               // покажем
                            number_koef = parsingData.read_num_trailer;
                            if (number_axis == 4) actionBar.setTitle("Причіп №" + number_koef);
                            parsingData.parsing_ok = false;   // сбрасываем флаг корректных данных
                        }
                    }
                    if (count_send == 0) {  // если ноль то показываем что соеденение отстутствует
                        out_massa.setText("-------   т");// везде прочерки
                    }

                    if (wifiManager.connect_ok /*&& activ*/) {        // проверяем флаг наличия соеденения по сокету и активированого прриложения
                        switch (tip_send) {              // и отправляем разные посылки, №АЦП или обнулить
                            case 0:
                                tip_send++;
                                switch (number_axis) {
                                    case 1:
                                        wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc1)); //
                                        break;
                                    case 2:
                                        wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc2));
                                        break;
                                    case 3:
                                        wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc3));
                                        break;
                                    case 4:
                                        wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc4));
                                        break;
                                }
                                break;

                            case 1:
                                tip_send++;
                                if (zero) {
                                    zero = false;
                                    switch (number_axis) {
                                        case 1:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.zero1));
                                            break;
                                        case 2:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.zero2));
                                            break;
                                        case 3:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.zero3));
                                            break;
                                        case 4:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.zero4));
                                            break;
                                    }
                                }
                                break;

                            case 2:
                                if (opros) {
                                    switch (opros_send) {
                                        case 0:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_count_trailer));
                                            opros_send++;
                                            break;
                                        case 1:
                                            switch (number_axis) {
                                                case 1:
                                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_koef1));
                                                    opros_send++;
                                                    break;
                                                case 2:
                                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_koef2));
                                                    opros_send++;
                                                    break;
                                                case 3:
                                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_koef3));
                                                    opros_send++;
                                                    break;
                                                case 4:
                                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_koef4));
                                                    opros_send++;
                                                    break;
                                            }
                                            break;
                                        case 2:
                                            wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_filtr));
                                            tip_send++;
                                            opros = false;
                                            opros_send = 0;
                                            break;
                                    }
                                } else tip_send++;
                                break;

                            case 3:
                                if (set_comand) {
                                    if (var != 0) {
                                        if (massa_koef.isChecked()) {
                                            switch (number_axis) {
                                                case 1:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x06, send_koef));
                                                    break;
                                                case 2:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x07, send_koef));
                                                    break;
                                                case 3:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x08, send_koef));
                                                    break;
                                                case 4:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x09, send_koef));
                                                    break;
                                            }
                                        } else {
                                            switch (number_axis) {
                                                case 1:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x0A, send_massa));
                                                    break;
                                                case 2:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x0B, send_massa));
                                                    break;
                                                case 3:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x0C, send_massa));
                                                    break;
                                                case 4:
                                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x0D, send_massa));
                                                    break;
                                            }
                                        }
                                    }
                                }
                                tip_send++;
                                set_comand = false;
                                break;

                        }
                        if (tip_send == 4)
                            tip_send = 0;  // и снова делаем переменную нулем чтоб передавать заново
                    }
                   if (parsingData.parsing_command_ok) {     // это работает по тому что мы используем опрос всего, и там есть посылки 8бит, после которых просматриваем к.к.
                        parsingData.parsing_command_ok = false;
                        edit_koef.setText(String.valueOf(parsingData.read_koef));
                        if (parsingData.read_koef == 0){
                            Toast.makeText(KalibActivity.this, "Увага, коефіцієнт = 0!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        });
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