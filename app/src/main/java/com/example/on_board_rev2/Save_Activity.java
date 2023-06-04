package com.example.on_board_rev2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.on_board_rev2.data_base.AppExecuter;
import com.example.on_board_rev2.data_base.MyDbManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Save_Activity extends AppCompatActivity {
    TextView axis1, axis2, axis3, axis4, common_massa;
    EditText number;
    private ActionBar actionBar;
    private MyDbManager myDbManager;
    private String massa_common, massa_ax1, massa_ax2, massa_ax3, massa_ax4, number_trailer;
    private BottomNavigationView bottomNavigationView; // это нижнее меню
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        myDbManager = new MyDbManager(this);
        init();
        context = this;
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {  // слушатель нижнего меню
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {     // в зависимости от того какой элемент передали в item выбираем действие
                    case R.id.navigation_setting:
                        Intent i = new Intent(Save_Activity.this, Setting_Activity.class);  // открываем новую активити
                        startActivity(i);
                        return true;
                    case R.id.navigation_data_base:
                        Intent oi = new Intent(Save_Activity.this, DataBaseActivity.class);
                        startActivity(oi);
                        return true;
                    case R.id.navigation_home:
                        Intent ooi = new Intent(Save_Activity.this, MainActivity.class);
                        startActivity(ooi);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();   // открыть базу данных
        calendars();
    }

    private void init() {
        Intent i = getIntent();
        massa_common = i.getStringExtra(Constants.MASSA_OUT_COMN);
        massa_ax1 = i.getStringExtra(Constants.MASSA_AX1);
        massa_ax2 = i.getStringExtra(Constants.MASSA_AX2);
        massa_ax3 = i.getStringExtra(Constants.MASSA_AX3);
        massa_ax4 = i.getStringExtra(Constants.MASSA_AX4);
        number_trailer = i.getStringExtra(Constants.NUM_TRAILER);
        actionBar = getSupportActionBar();                 // соеденяем акшн бар и переменную
        actionBar.setDisplayHomeAsUpEnabled(true);         // и устанавливаем у нее стандартный метод показать кнопку назад
        actionBar.setTitle("Зважування");
        axis1 = findViewById(R.id.axis_1);
        axis2 = findViewById(R.id.axis_2);
        axis3 = findViewById(R.id.axis_3);
        axis4 = findViewById(R.id.axis_4);
        common_massa = findViewById(R.id.axsis_common);
        number = findViewById(R.id.edit_number);
        common_massa.setText(massa_common);
        axis1.setText(massa_ax1);
        axis2.setText(massa_ax2);
        axis3.setText(massa_ax3);
        axis4.setText(massa_ax4);
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // подвязываем нижнее меню к джаве
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {            // создать меню (при включении)
        getMenuInflater().inflate(R.menu.shared_menu, menu);   // добавляем меню в наше активити
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {     // отслеживаем клацание по элементам меню
        if (item.getItemId() == android.R.id.home) {                     // если была нажата кнопка назад
            finish();                                                  // закрыть текущую активити
        }
        if (item.getItemId() == R.id.id_shared) {
            // создаем календарь, чтоб дата автоматом сохранялась
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String formattedDate = df.format(calendar.getTime());

            /*
            boolean found = false;
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain"); // gets the list of intents that can be loaded.
            List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    if (info.activityInfo.packageName.toLowerCase(
                            Locale.getDefault()).contains("com.viber.voip")
                            || info.activityInfo.name.toLowerCase(
                            Locale.getDefault()).contains("com.viber.voip")) {
                        share.putExtra(Intent.EXTRA_TEXT, "Звіт від системи On_board:\n" + formattedDate + "\n" + "Загальна вага:" + "\n" + massa_common + "\n" + "Вісь 1:" + "\n" +
                                massa_ax1 + "\n" + "Вісь 2:" + "\n" + massa_ax2 + "\n" + "Вісь 3:" + "\n" + massa_ax3 + "\n" + "Причіп:" + "\n" + massa_ax4);
                        share.setPackage(info.activityInfo.packageName);
                        found = true;
                        context.startActivity(Intent.createChooser(share, "Select"));
                        break;
                    }
                }
                if (!found) {
                    Uri marketUri = Uri.parse("market://details?id=" + "com.viber.voip");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    context.startActivity(marketIntent);
                }
            } */

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                //intent.setPackage("com.viber.voip"); //имя пакета приложения
                //intent.setPackage("org.telegram.messenger"); //имя пакета приложения
                intent.putExtra(Intent.EXTRA_TEXT, "Звіт від системи On_board:\n" + formattedDate + "\n" + "Загальна вага:" + "\n" + massa_common + "\n" + "Вісь 1:" + "\n" +
                        massa_ax1 + "\n" + "Вісь 2:" + "\n" + massa_ax2 + "\n" + "Вісь 3:" + "\n" + massa_ax3 + "\n" + "Причіп:" + "\n" + massa_ax4); // текст отправки
                startActivity(Intent.createChooser(intent, "Відправити файл"));
                //https://stackoverflow.com/questions/59926208/launch-telegram-app-from-android-application-with-message-and-recipient

        }
        if (item.getItemId() == R.id.id_saves) {
            if (!number.getText().toString().isEmpty()) {
                // создаем календарь, чтоб дата автоматом сохранялась
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(calendar.getTime());
                Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {    // запускаем запись на второстепенном потоке
                        myDbManager.insertToDb(number.getText().toString(), " " + massa_common, " " + massa_ax1, " " + massa_ax2, " " + massa_ax3, " " + massa_ax4, " " + number_trailer,
                                formattedDate);             // записываем в базу
                        finish();
                    }
                });
            } else Toast.makeText(this, "Введіть номер", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void calendars() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();    // закрыть базу данных
    }
}