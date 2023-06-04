package com.example.on_board_rev2.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.math.BigInteger;

public class ParsingData {
    private Context context;  // хз зачем))
    public boolean parsing_ok;
    public boolean parsing_command_ok;
    public float read_koef, write_koef;
    public byte read_diskrt, read_filtr;
    public byte write_diskrt, write_filtr;
    public int axis_1, axis_2, axis_3, axis_trailer, read_tara,read_num_trailer;
    public byte[] adc1 = {0x02, 0x01,  0x03};   //АЦП1
    public byte[] adc2 = {0x02, 0x02,  0x03};   //АЦП2
    public byte[] adc3 = {0x02, 0x03,  0x03};   //АЦП3
    public byte[] adc4 = {0x02, 0x04,  0x03};   //АЦП4
    public byte[] zero = {0x02, 0x05,  0x03};   //обнулить
    public byte[] get_diskr = {0x02, 0x0F,  0x03};   //считать дискрет
    public byte[] get_filtr = {0x02, 0x11,  0x03};   //считать фильтр
    public byte[] get_koef1 = {0x02, 0x12,  0x03};   //считать коэф1
    public byte[] get_koef2 = {0x02, 0x13,  0x03};   //считать коэф2
    public byte[] get_koef3 = {0x02, 0x14,  0x03};   //считать коэф3
    public byte[] get_koef4 = {0x02, 0x15,  0x03};   //считать коэф4
    public byte[] zero1 = {0x02, 0x16,  0x03};   //обнулить + еепром
    public byte[] zero2 = {0x02, 0x17,  0x03};   //обнулить + еепром
    public byte[] zero3 = {0x02, 0x18,  0x03};   //обнулить + еепром
    public byte[] zero4 = {0x02, 0x19,  0x03};   //обнулить + еепром
    public byte[] get_tara = {0x02, 0x1A,  0x03};   // считать тару
    public byte[] get_count_trailer = {0x02, 0x1D,  0x03};   // считать номер прицепа


    // данные которые нужно отправить
    public ParsingData(Context context) {
        this.context = context;
    }  // конструктор

    public byte[] Buf_to_string(@NonNull byte[] buf) {       // преобразовываем из буфера в строку данные для отправки
        byte[] out = new byte[buf.length];                  // так как в массиве указіваем не с 0 а просто количество
        for (int i = 0; i < buf.length; i++) {
                out[i] = buf[i];
            }
        return out;                          // вернуть готовый массив
    }

    public byte[] send32_bit(byte comand, int pars) {
        byte[] out = new byte[7];                  // размер массива
        out[0] = 0x02;
        out[1] = comand;
        out[2] = (byte) pars;                      // раскладываем инт по своим ячейкам
        pars = pars >> 8;
        out[3] = (byte) pars;
        pars = pars >> 8;
        out[4] = (byte) pars;
        pars = pars >> 8;
        out[5] = (byte) pars;
        out[6] = 0x03;
        return out;                          // вернуть готовый массив
    }


    public void Parsing_inpt(byte[] data) {         // парсинг входящих данных
        int mirror = 0;
        if (data[0] == 0x02 && data[6] == 0x03) {                    // ну и сам парсинг
                mirror = data[5] & 0xFF;                         // 0xFF позволяет убрать все лишнее из входящего байта
                mirror = (mirror << 8);
                mirror |= data[4] & 0xFF;
                mirror = (mirror << 8);
                mirror |= data[3] & 0xFF;
                mirror = (mirror << 8);
                mirror |= data[2] & 0xFF;                            // складываем число из юарта
                parsing_ok = true;                                   // поднимаем флаг что данные коректны
                switch (data[1]) {                                   // по первому символу раскладываем данные по переменным
                    case 0x01:
                        axis_1 = mirror;
                        break;
                    case 0x02:
                        axis_2 = mirror;
                        break;
                    case 0x03:
                        axis_3 = mirror;
                        break;
                    case 0x04:
                        axis_trailer = mirror;
                        break;
                    case 0x12:
                    case 0x13:
                    case 0x14:
                    case 0x15:
                        read_koef = Float.intBitsToFloat(mirror);  // считаный флоат преобразовываем в нормальный вид
                        break;
                    case 0x1A:
                        read_tara = mirror;
                        break;
                    case 0x1D:
                        read_num_trailer = mirror;
                        break;
                }
        }
        if (data[0] == 0x02 && data[3] == 0x03 && data[6] == 0x00) {  // это для мелких комманд
            switch (data[1]) {
                case 0x0F:
                    read_diskrt = (byte) (data[2] & 0xFF);
                    break;
                case 0x11:
                    read_filtr = (byte) (data[2] & 0xFF);
                    parsing_command_ok = true;
                    break;
            }
        }
        for (int i = 0; i < 10; i ++ ){
            data[i] = 0x00;
        }
    }

   /* byte calcCRC(byte[] buf, int len) {                     // расчет срс, взял из дини
        char r = 0;
        for (int i = 0; i < len; i++) {
            r -= buf[i] & 0xFF;
        }
        r &= 0x7F;
        if (r < 0x21) {
            r += 0x21;
        }
        return (byte) r;
    }*/

}


// код начала фрейма 0x02, код команды, (данные или пустота) код конца фрейма  0x03, 0x0D
// два типа посылки, или просто команда или команда + данные (всегда 32бита)
// 0x01 - adc1
// 0x02 - adc2
// 0x03 - adc3
// 0x04 - adc4
// 0x05 - zero
// 0x06 - k.k1
// 0x07 - k.k2
// 0x08 - k.k3
// 0x09 - k.k4
// 0x0A - massak1
// 0x0B - massak2
// 0x0C - massak3
// 0x0D - massak4
// 0x0Е - дискрет установить
// 0x0F - дискрет отправить
// 0x10 - фильтр установить
// 0x11 - фильтр отправить
// 0x12 - коэф1 отправить
// 0x13 - коэф2 отправить
// 0x14 - коэф3 отправить
// 0x15 - коэф4 отправить
// 0x16 - zero1
// 0x17 - zero2
// 0x18 - zero3
// 0x19 - zero4
//------------обновление 2.0--------//
// 0x1A - тара отправить
// 0х1В - тара записать
// 0х1С - установить номер коэф (32бита посылка)
// 0х1D - отправить номер коэф