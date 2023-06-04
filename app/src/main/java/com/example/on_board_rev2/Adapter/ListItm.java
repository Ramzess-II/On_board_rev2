package com.example.on_board_rev2.Adapter;

import java.io.Serializable;

public class ListItm implements Serializable {  // Serializable это для того чтоб можно было передать весь класс целиком через интент

    private int id = 0;
    private String massa_common;      // данные для упрощения работы с БД
    private String axsi1;
    private String axsi2;
    private String axsi3;
    private String axsi4;
    private String num_axsi4;

    public String getNum_axsi4() {
        return num_axsi4;
    }

    public void setNum_axsi4(String num_axsi4) {
        this.num_axsi4 = num_axsi4;
    }

    private String number;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMassa_common() {
        return massa_common;
    }

    public void setMassa_common(String massa_common) {
        this.massa_common = massa_common;
    }

    public String getAxsi1() {
        return axsi1;
    }

    public void setAxsi1(String axsi1) {
        this.axsi1 = axsi1;
    }

    public String getAxsi2() {
        return axsi2;
    }

    public void setAxsi2(String axsi2) {
        this.axsi2 = axsi2;
    }

    public String getAxsi3() {
        return axsi3;
    }

    public void setAxsi3(String axsi3) {
        this.axsi3 = axsi3;
    }

    public String getAxsi4() {
        return axsi4;
    }

    public void setAxsi4(String axsi4) {
        this.axsi4 = axsi4;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
