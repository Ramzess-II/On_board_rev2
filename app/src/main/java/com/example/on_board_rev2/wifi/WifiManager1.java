package com.example.on_board_rev2.wifi;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class WifiManager1 extends Thread {   // поток для создания сокета
    public boolean more_connect = false;
    public boolean connect_ok = false;
    private Context context;
    private Socket socket;
    private String _ip, _port;
    private WifiTransmiter wifiTransmiter;

    public WifiManager1(Context context, String ip, String port) {   // конструктор, сюда передаем ip и порт
        this.context = context;
        _ip = ip;
        _port = port;
        wifiTransmiter = new WifiTransmiter(context);               // создаем класс передатчика
    }


    final Handler h = new Handler();     // экземпляр класса для возможности добраться к основному потоку

    @Override
    public void run() {
        try {
            more_connect = true;              // защита от того чтоб я не мог несколько раз создать поток. проверяется этот флаг
            socket = new Socket(_ip, Integer.parseInt(_port));      // создаем сокет
            //Log.d("MyLog", "Connect");
            socket.setSoTimeout(3000);     // это тайм аут именно того сколько сервер может не отвечать. если дольше то выпадем в катч
            wifiTransmiter.output = new BufferedOutputStream(socket.getOutputStream());   // создаем переменную для передачи
            wifiTransmiter.input1 = new BufferedInputStream(socket.getInputStream());// и переменную для приема без всяких надстроек
            wifiTransmiter.start();        // запускаем поток для слушателя
            h.post(new Runnable() {         // это позволяет выводить информацию в главном потоке
                @Override
                public void run() {
                    Toast.makeText(context, "Connect", Toast.LENGTH_SHORT).show();   // пишем что подключились
                    //ProgressBar prg_bar = ((Activity) context).findViewById(R.id.progressBar); // это позволяло управлять прогресс баром
                    // prg_bar.setVisibility(View.GONE);
                }
            });
            connect_ok = true;         // это флаг того что есть подключение и можно слать данные
        } catch (IOException e) {
            e.printStackTrace();
            //Log.d("MyLog", "ERR Connect");
            h.post(new Runnable() {   // снова доступ к главному экрану
                @Override
                public void run() {
                    Toast.makeText(context, "Disconnect", Toast.LENGTH_SHORT).show();
                }
            });
            socket = null;         // если не удалось подключиться все сбросить и удалить сокет
            connect_ok = false;
            more_connect = false;
        }
    }

    public WifiTransmiter probros() {
        return wifiTransmiter;
    }   // чтоб можно было добраться к передатчику

    public void socketClose() {
        try {
            if (socket != null) {      // если сокет был создан
                socket.close();        // закрываем его
                connect_ok = false;    // и опять все сбрасываем
                more_connect = false;
                wifiTransmiter.stop_lisn = false;  // а так же вываливаемся из вайла который в приемнике
                Toast.makeText(context, "Disconnect", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}