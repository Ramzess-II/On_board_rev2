package com.example.on_board_rev2.data_base;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecuter {
    private static AppExecuter instance;
    private final Executor mainIO;             // класс для создания потока
    private final Executor subIO;

    public AppExecuter(Executor mainIO, Executor subIO) {   // конструктор создает два потока
        this.mainIO = mainIO;
        this.subIO = subIO;
    }

    public static AppExecuter getInstance() {  // функция для проверки создан ли поток
        // если класса еще нету то создаем его. Executors.newSingleThreadExecutor второстепенный поток. MainThreadHandler основной
        if (instance == null) instance = new AppExecuter( new MainThreadHandler(), Executors.newSingleThreadExecutor() );
        return instance;
    }

    public Executor getMainIO() {
        return mainIO;
    }

    public Executor getSubIO() {
        return subIO;
    }

    public static class MainThreadHandler implements Executor {    // клас для соеденения основного потока с второстепенным
        // создали хендлер который будет запускаться на основном потоке. getMainLooper основной поток
        private Handler mainHandler = new Handler(Looper.getMainLooper());     // android os

        @Override
        public void execute(Runnable runnable) {
            mainHandler.post(runnable);               // запускаем
        }
    }
}