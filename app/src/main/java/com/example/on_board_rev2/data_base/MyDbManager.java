package com.example.on_board_rev2.data_base;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.on_board_rev2.Adapter.ListItm;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {
    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;           // объект с помощью которого мы можем работать с БД считывать, записывать
    private String selection;


    public MyDbManager(Context context) {        // конструктор, передаем сюда контекст
        this.context = context;
        myDbHelper = new MyDbHelper(context);    // и создаем наш класс, который создает БД
    }

    public void openDb() {                       // откываем БД для записи/считывания
        db = myDbHelper.getWritableDatabase();
    }   // открыть базу

    public void insertToDb(String number, String massa_c, String ax1, String ax2, String ax3, String ax4,String num_ax4, String data ) {    // для записи в БД, сюда передаем то что будем записывать

        ContentValues cv = new ContentValues();           // класс для промежуточного сохранения данных, потому что БД принимает только такой формат
        cv.put(DataBase_const.NUMBER, number);
        cv.put(DataBase_const.MASSA_COMMON, massa_c);
        cv.put(DataBase_const.AXS1, ax1);
        cv.put(DataBase_const.AXS2, ax2);
        cv.put(DataBase_const.AXS3, ax3);
        cv.put(DataBase_const.AXS4, ax4);
        cv.put(DataBase_const.NUM_AXS4, num_ax4);
                cv.put(DataBase_const.DATA, data);
        db.insert(DataBase_const.TABLE_NAME, null, cv);  // передаем имя таблицы и данные из класса
    }

    @SuppressLint("Range")
    public void ReadDb(String title, OnDataResive onDataResive, int poz) {               // функция для считывания данных из БД
        List<ListItm> tempList = new ArrayList<>();       // создаем лист который можно вернуть
        switch (poz) {
            case 1:
                selection = DataBase_const.DATA + " like ?";
                break; // обращение для поиска разных элементов. зависит от выбранного фильтра
            case 2:
                selection = DataBase_const.NUMBER + " like ?";
                break; // обращение для поиска
        }
        Cursor cursor = db.query(DataBase_const.TABLE_NAME, null, selection, // date - имя колнки, DESC это сортировка по убыванию или ASC по возрастанию
                new String[]{"%" + title + "%"}, null, null, "date DESC");   // передаем название базы и еще можно фильтры % для совпадения по 1 букве
        while (cursor.moveToNext()) {          // будем двигаться по вайл пока есть элементы в cursor, когда закончились выйдем
            ListItm listItm = new ListItm();
            String text = cursor.getString(cursor.getColumnIndex(DataBase_const.NUMBER)); // получаем все стринг, а номер элемента получаем getColumnIndex
            // так же можно вытаскивать и инт и лонг достаточно указать getString другое гет инт
            listItm.setNumber(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.MASSA_COMMON));
            listItm.setMassa_common(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.AXS1));
            listItm.setAxsi1(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.AXS2));
            listItm.setAxsi2(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.AXS3));
            listItm.setAxsi3(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.AXS4));
            listItm.setAxsi4(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.NUM_AXS4));
            listItm.setNum_axsi4(text);
            text = cursor.getString(cursor.getColumnIndex(DataBase_const.DATA));
            listItm.setDate(text);
            int _id = cursor.getInt(cursor.getColumnIndex(DataBase_const._ID));  // для позиции в адаптере
            listItm.setId(_id);
            tempList.add(listItm);
        }
        cursor.close();
        onDataResive.orResive(tempList);
    }

    public void delete() {       // удаление из базы данных
        // удалить колону у которой id будет тот который передали
        db.delete(DataBase_const.TABLE_NAME, null, null);  // если null то удалить все
    }

   /* public void update (int id,String title, String disc, String uri){       //обновление БД
        String selection = DataBase_const._ID + "=" + id;        // удалить колону у которой id будет тот который передали
        ContentValues cv = new ContentValues();           // класс для промежуточного сохранения данных, потому что БД принимает только такой формат
        cv.put(MyConastants.TITLE, title);
        cv.put(MyConastants.DISC, disc);
        cv.put(MyConastants.URI, uri);
        db.update(MyConastants.TABLE_NAME,cv,selection,null );
    }*/

    public void CloseDb() {
        myDbHelper.close();
    }          // закрыть базу
}
