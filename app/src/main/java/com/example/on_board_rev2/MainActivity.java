package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.on_board_rev2.wifi.ParsingData;
import com.example.on_board_rev2.wifi.WifiManager1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private int count_send = 0, tip_send = 0;      // это для обратного отсчета показа массы и типа посылки
    private int tara, blink,num_trailer_out;
    private boolean ok_zero, activiti_ok, send_tara, blink_flag;
    private int max_ax1, max_ax2, max_ax3, max_trailer, max_global, common_massa_int, static_mass;
    private ImageView img, connect, disconnect;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private TextView common_massa, axis1, axis2, axis3, trailer, tar, err36, num_trailers;
    private Button zero, activ;
    private ImageButton weight;
    private WifiManager1 wifiManager;
    private ParsingData parsingData;
    private String _ip, _port;              // для передачи в сокет параметров
    private String put_out_massa;        // переменная массы для отображения
    private String err36_data;
    private Boolean err36_stop = false;
    private SharedPreferences shared_prf;   // для сохранения
    private SwipeRefreshLayout mSwipeRefreshLayout;     // потянул вниз - обновил


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        small_display();
        runTimer();                                                            // запускаем таймер для обратных отсчетов
        zero.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // функция вызова диалогового окна
            builder.setMessage("Дійсно тарувати авто?");   // основная строка
            builder.setTitle("Тарування");    // заголовок
            builder.setPositiveButton("Так", new DialogInterface.OnClickListener() { // если нажали да
                @Override
                public void onClick(DialogInterface dialog, int which) {       // если нажали да
                    tara = common_massa_int + static_mass;
                    SharedPreferences.Editor editor = shared_prf.edit();       // создаем елемент для открытия таблицы на запись
                    editor.putInt(Constants.TARA_KG, tara);
                    editor.apply();                                            // сохранить
                    send_tara = true;
                }
            });
            builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        });

        weight.setOnClickListener(v -> {
            Intent oi = new Intent(MainActivity.this, Save_Activity.class);
            oi.putExtra(Constants.MASSA_OUT_COMN, common_massa.getText());
            oi.putExtra(Constants.MASSA_AX1, axis1.getText());
            oi.putExtra(Constants.MASSA_AX2, axis2.getText());
            oi.putExtra(Constants.MASSA_AX3, axis3.getText());
            oi.putExtra(Constants.MASSA_AX4, trailer.getText());
            oi.putExtra(Constants.NUM_TRAILER, Integer.toString(num_trailer_out));
            startActivity(oi);
        });
        activ.setOnClickListener(view -> {
            Intent n = new Intent(MainActivity.this, Password_Activity.class);
            startActivity(n);
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(MainActivity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        finishAffinity();
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(MainActivity.this, DataBaseActivity.class);
                        startActivity(oi);
                        finishAffinity();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        mSwipeRefreshLayout.setRefreshing(true);
        super.onResume();         // функция которая запускается после onCreate
        readSharedPrf();          // считаем из памяти данные
        err36_read();             // проверим не вышло ли время
        activiti_ok = shared_prf.getBoolean(Constants.ACTIVS, false);  // считываем значени флага блокировки
        if (activiti_ok)
            activ.setVisibility(View.GONE);                       // если тру то прячем текст не активирован
        if (Wifi_on_off()) {       // проверяем если вай фай включен то пробуем подключиться к сокету
            startSocet();
        }
    }

    private void readSharedPrf() {
        shared_prf = getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);  // инициализация настроек SharedPreferences  Context обязательно
        _ip = shared_prf.getString(Constants.IP_SET, "192.168.4.1");      // считываем из памяти данные сокета
        _port = shared_prf.getString(Constants.PORT_SET, "1234");
        max_ax1 = shared_prf.getInt(Constants.MAX_AX1, 10000);            // стандартные значения. в ходе программы изменяются
        max_ax2 = shared_prf.getInt(Constants.MAX_AX2, 10000);
        max_ax3 = shared_prf.getInt(Constants.MAX_AX3, 10000);
        max_trailer = shared_prf.getInt(Constants.MAX_TRAILER, 10000);
        max_global = shared_prf.getInt(Constants.MAX_WEIGHT, 40000);
        static_mass = shared_prf.getInt(Constants.STATIC_MAS_AX1, 6000);
        tara = shared_prf.getInt(Constants.TARA_KG, 0);
        err36_data = shared_prf.getString(Constants.DATA_ERR36, "01.01.2030");
    }

    void small_display() {     // проверяем размер экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        if (screenHeight < 1000) {  // если по длине меньше 1000 то прячем картинку
            img.setVisibility(View.GONE);
        }
    }

    private boolean Wifi_on_off() {     // спрашиваем у системы, а вай фай включен?
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);   // кидаем запрос
        if (!wifi.isWifiEnabled()) {      // если ложь то пишем что вай фай не включен
            Toast.makeText(MainActivity.this, "Wifi не ввімкнено", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;      // ну или возвращаем правду
        }
    }

    private void init() {
        activ = findViewById(R.id.button_activ);
        img = findViewById(R.id.img);
        common_massa = findViewById(R.id.text_common);
        axis1 = findViewById(R.id.text_axis1);
        axis2 = findViewById(R.id.text_axis2);
        axis3 = findViewById(R.id.text_axis3);
        trailer = findViewById(R.id.text_trailer);
        zero = findViewById(R.id.button_zero);
        weight = findViewById(R.id.button_weight);
        tar = findViewById(R.id.textViev_netto);
        num_trailers = findViewById(R.id.num_trailer);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);  // это потянул - обновил
        mSwipeRefreshLayout.setOnRefreshListener(this);  // и для него активити выбрали
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
        parsingData = new ParsingData(this);
        err36 = findViewById(R.id.text_err36);
        disconnect = findViewById(R.id.disconneckt);
        connect = findViewById(R.id.connect);
        // bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);  вот так можно предварительно установить выбранный элемент
    }

    private void startSocet() {          // запускаем сокет
        wifiManager = new WifiManager1(MainActivity.this, _ip, _port);    // создаем класс и передаем туда параметры
        wifiManager.start();             // запускаем второстепенный поток
        mSwipeRefreshLayout.setRefreshing(false);   // включаем крутилку
    }

    private void image_set() {      // функция для изменения картинки
        Calendar calendar = Calendar.getInstance();  // создаем класс календаря
        int minute = calendar.get(Calendar.MINUTE);  // в переменную минуты копируем значение
        if ((minute & 1) == 0) {  // если число четное то показывать одну надпись
            img.setImageResource(R.drawable.big_car_title);
        } else {   // не четное другую
            img.setImageResource(R.drawable.big_car321);
        }
    }

    private void err36_read() {   // бред пьяного ромы
        char[] mirror_dat_mount = new char[15];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());  // преобразовываем в нужный вид дату
        String currentDateandTime = sdf.format(new Date());
        mirror_dat_mount = err36_data.toCharArray();         // из памяти берем дату и разбиваем в массив
        err36.setVisibility(View.GONE);                      // прячем табличку, чтоб не перезаходить в приложение
        err36_stop = false;
        mirror_dat_mount = ArrayUtils.remove(mirror_dat_mount, 2);  // удаляем из массива точки, чтоб нормально преобразовать в инт
        mirror_dat_mount = ArrayUtils.remove(mirror_dat_mount, 4);
        char[] temp = new char[8];
        temp [0] = mirror_dat_mount [4];
        temp [1] = mirror_dat_mount [5];
        temp [2] = mirror_dat_mount [6];
        temp [3] = mirror_dat_mount [7];
        temp [4] = mirror_dat_mount [2];
        temp [5] = mirror_dat_mount [3];
        temp [6] = mirror_dat_mount [0];
        temp [7] = mirror_dat_mount [1];
        String prom = String.valueOf(temp);                   // массив в строку
        int dats_mirr = Integer.parseInt(currentDateandTime);             // строку в инт текущее значение
        int dats = Integer.parseInt(prom);                                // значение из памяти
        if(dats_mirr >= dats) {                                          // проверяем какое число больше
            err36.setVisibility(View.VISIBLE);                            // и если сохраненное значение меньше чем текущее то гг
            err36_stop = true;
            SharedPreferences.Editor editor = shared_prf.edit();                           // создаем елемент для открытия таблицы на запись
            editor.putBoolean(Constants.ERR36_OK, true);                 // сохраняем по ключу значение
            editor.apply();
        }
    }

    private void blink_wifi(){
        if(blink<10)blink ++;
        if(blink == 5) {
            blink = 0;
            blink_flag = !blink_flag;
        }
        if(blink_flag){
            disconnect.setVisibility(View.GONE);
        } else {
            disconnect.setVisibility(View.VISIBLE);
        }
    }

    private void runTimer() {                       // функция таймера который запускается через 1 секунду
        final Handler handler = new Handler();       // android os
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 90); // Запускаем код снова с задержкой в одну секунду
                image_set();         // функция для показа двух картинок
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
                        connect.setVisibility(View.VISIBLE);
                        disconnect.setVisibility(View.GONE);
                        float common = 0;     // объявим флоат для общих даннных
                        int common_int = 0;   // это для правильного обнуления переменная

                        if (parsingData.parsing_ok) {
                            float dats = ((float) static_mass / 1000);    // статическая ось
                            common += dats;
                            if (max_ax1 > 0) {     // если значение мах веса для оси не ноль и данные правильные
                                float dat = ((float) parsingData.axis_1 / 1000);    // преобразуем число в флоат
                                common += dat;  // так же прибавим к общему весу
                                String print = String.format("%.3f т", dat);        // форматнем в вид Тонны
                                axis1.setText(print);                               // покажем
                                common_int += parsingData.axis_1;                   // в нее складываем интовые значения
                                if (max_ax1 > parsingData.axis_1)
                                    axis1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                else
                                    axis1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }
                            // это игра с цветами, в зависимости от состояния разный цвет данных
                            if (max_ax2 > 0) {
                                float dat = ((float) parsingData.axis_2 / 1000);
                                common += dat;
                                String print = String.format("%.3f т", dat);
                                axis2.setText(print);
                                common_int += parsingData.axis_2;
                                if (max_ax2 > parsingData.axis_2)
                                    axis2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                else
                                    axis2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }

                            if (max_ax3 > 0) {
                                float dat = ((float) parsingData.axis_3 / 1000);
                                common += dat;
                                String print = String.format("%.3f т", dat);
                                axis3.setText(print);
                                common_int += parsingData.axis_3;
                                if (max_ax3 > parsingData.axis_3)
                                    axis3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                else
                                    axis3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }

                            if (max_trailer > 0) {
                                float dat = ((float) parsingData.axis_trailer / 1000);
                                common += dat;
                                String print = String.format("%.3f т", dat);
                                trailer.setText(print);
                                common_int += parsingData.axis_trailer;
                                if (max_trailer > parsingData.axis_trailer)
                                    trailer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                else
                                    trailer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }

                            if (max_ax1 != 0 || max_ax2 != 0 || max_ax3 != 0) {
                                String print = String.format("%.3f т", common);
                                common_massa.setText(print);  // тоже самое для общего веса
                                print = String.format("%.3f т", common - ((float) tara / 1000));
                                tar.setText(print);
                                if ((max_global / 1000) > common)
                                    common_massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                else
                                    common_massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            }
                            num_trailers.setText("№"+Integer.toString(parsingData.read_num_trailer));    // это для установки номера прицепа, считаного из модуля
                            num_trailer_out = parsingData.read_num_trailer;
                            parsingData.parsing_ok = false;   // сбрасываем флаг корректных данных
                            common_massa_int = common_int;    // и заносим в глобальную переменную чтоб каждый раз ее обновлять
                        }
                    }

                    if (count_send == 0) {  // если ноль то показываем что соеденение отстутствует
                        put_out_massa = "-------   т";  // везде прочерки
                        common_massa.setText(put_out_massa);
                        tar.setText(put_out_massa);
                        axis1.setText(put_out_massa);
                        axis2.setText(put_out_massa);
                        axis3.setText(put_out_massa);
                        trailer.setText(put_out_massa); //  и плюс все черным цветом
                        common_massa.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        axis1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        axis2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        axis3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        trailer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                        connect.setVisibility(View.GONE);
                        blink_wifi();
                    }

                    if (wifiManager.connect_ok && activiti_ok) {        // проверяем флаг наличия соеденения по сокету и активированого прриложения
                        switch (tip_send) {              // и отправляем разные посылки, №АЦП или обнулить
                            case 0:
                                if (max_ax1 != 0) {
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc1)); //
                                }
                                break;
                            case 1:
                                if (max_ax2 != 0) {
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc2));
                                }
                                break;
                            case 2:
                                if (max_ax3 != 0) {
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc3));
                                }
                                break;
                            case 3:
                                if (max_trailer != 0) {
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.adc4));
                                }
                                break;
                            case 4:
                                if (ok_zero) {
                                    ok_zero = false;
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.zero));
                                }
                                break;
                            case 5:
                                    wifiManager.probros().senMesage(parsingData.Buf_to_string(parsingData.get_count_trailer));
                                break;
                            case 6:
                                if (send_tara) {
                                    send_tara = false;
                                    wifiManager.probros().senMesage(parsingData.send32_bit((byte) 0x1B, tara));
                                }
                                break;
                        }
                        if (tip_send < 6) tip_send++;
                        else
                            tip_send = 0;  // и снова делаем переменную нулем чтоб передавать заново с первого АЦП
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

    @Override
    public void onRefresh() {  //если сразу делать setRefreshing(false); то єта функция тупо не вызывается
        mSwipeRefreshLayout.setRefreshing(true);
        if (Wifi_on_off()) {          // если есть вай фай то если тянем вниз
            if (wifiManager != null) wifiManager.socketClose();  //закрываем старый сокет
            if (!wifiManager.connect_ok && !wifiManager.more_connect) {  // если нет подключения и подключение к сокету не запущено
                startSocet();  // пробуем подключиться
            }
        }
    }
}