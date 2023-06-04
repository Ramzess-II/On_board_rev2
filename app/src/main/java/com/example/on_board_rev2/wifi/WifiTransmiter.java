package com.example.on_board_rev2.wifi;

import android.content.Context;

import com.example.on_board_rev2.data_base.AppExecuter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class WifiTransmiter extends Thread {           // клас для приема данных из сокета

    private Context context;  // хз зачем тут
    public boolean stop_lisn = false, inpt_data = false;
    public String inpt_str;
    public OutputStream output;
    public InputStream input1;


    public byte[] data = new byte[20];  // буфер приема

    public WifiTransmiter(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        while (!stop_lisn) {               // когда закрываем сокет вываливаемся из цикла
            try {
                input1.read(data);        // считываем в буффер входящие данные
                inpt_str = Arrays.toString(data);  // преобразуем в строку. осталось с прошлого раза
                    if (inpt_str.length() > 2) {   // если строка больше 2х символов
                        inpt_data = true;  // если они больше 2 то поднимаем флаг можно парсить
                }
            } catch (SocketTimeoutException ignored) {            // это короче исключение тайм аута, если долго нету посылки попадаем сюда
                //Log.d("MyLog", "Timeout");
            } catch (IOException e) {                             // а это когда отвалился вай фай чтоб вылететь из цикла
                e.printStackTrace();
                //Log.d("MyLog", "Disconnect");
                break;
            }
        }
    }

    public void senMesage(byte [] buf) {                  // отправка сообщений
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {  // на второстепенном потоке
            @Override
            public void run() {
                try {
                    output.write(buf);   // передачу тоже надо на второстепенный поток вынести иначе вылет
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}