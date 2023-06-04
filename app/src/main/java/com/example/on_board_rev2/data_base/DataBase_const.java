package com.example.on_board_rev2.data_base;

public class DataBase_const {
    public static final String TABLE_NAME = "my_table";         // создали таблицу в базе
    public static final String _ID = "_id";                     // _id элемента
    public static final String MASSA_COMMON = "massa";          // две колоны
    public static final String AXS1 = "axs1";
    public static final String AXS2 = "axs2";
    public static final String AXS3 = "axs3";
    public static final String AXS4 = "axs4";
    public static final String NUM_AXS4 = "num_axs4";
    public static final String NUMBER = "number";               // две колоны
    public static final String DATA = "date";                   // и это колона
    public static final String DB_NAME = "my_db1.db";           // создали базу данных
    public static final int DB_VERSION = 1;                     // это версия БД

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +       // это шаблон по тому как создавать таблицу в Базе данных если она пустая
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + MASSA_COMMON + " TEXT," + AXS1 + " TEXT,"  + AXS2 + " TEXT," + AXS3 + " TEXT," +
            AXS4 + " TEXT," + NUM_AXS4 + " TEXT," + DATA + " TEXT,"  +NUMBER + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;    // это шаблон для удаления таблицы с заданым именем если она существует

}


