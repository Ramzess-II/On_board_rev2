package com.example.on_board_rev2.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class MyDbHelper extends SQLiteOpenHelper {                 // промежуточный класс для работы с БД

    public MyDbHelper(@Nullable Context context) {                 // конструктор, сюда хард кодом прописываем элементы БД
        super(context, DataBase_const.DB_NAME, null, DataBase_const.DB_VERSION);  // это создаст базу данных если ее нет с тем что мы передали
    }

    @Override
    public void onCreate(SQLiteDatabase db) {                     // когда БД создана запуститься этот метод
        db.execSQL(DataBase_const.TABLE_STRUCTURE);                 // передаем сюда шаблон как создавать таблицу в БД
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //обновление БД
        db.execSQL(DataBase_const.DROP_TABLE);                                    // удаляем старую таблицу
        onCreate(db);                                                           // создаем новую
    }
}
