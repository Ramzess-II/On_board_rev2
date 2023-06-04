package com.example.on_board_rev2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.on_board_rev2.Constants;
import com.example.on_board_rev2.R;
import com.example.on_board_rev2.Viev_Activity;

import java.util.ArrayList;
import java.util.List;

public class BDAdapter extends ArrayAdapter<ListItm> {
    private final List<ListItm> mainList;                           // это копия листа чтоб получать доступ во всем классе
    private final List<VievHolder> listViewHolders;                 // это список элементов которые нужно рисовать
    private Context context;

    public BDAdapter(@NonNull Context context, int resource, @NonNull List<ListItm> objects) {
        super(context, resource, objects);
        this.mainList = objects;
        this.context = context;
        listViewHolders = new ArrayList<>();        // массив списков из VievHolder_ов
    }

    static class VievHolder {               // это класс для хранения элементов в памяти, которые мы прокрутили
        TextView tvData;
        TextView tvNumbers;
        ConstraintLayout layouts;         // а это чисто для того чтоб отследить нажатие по элементу
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {  // переоприделяем метод создания шаблона под себя
        VievHolder vievHolder;              // создаем класс который описан выше
        if (convertView == null) {
            vievHolder = new VievHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exampl_list_view, null, false);
            // эта конструкция позволяет формировать элементы экскиз которых мы нарисовали в example_list
            vievHolder.tvData = convertView.findViewById(R.id.textView_dat);
            // помещаем в клас элемент который мы уже сконвертировали. конкретный элемент tvData
            vievHolder.tvNumbers = convertView.findViewById(R.id.textView_numb);
            vievHolder.layouts = convertView.findViewById(R.id.list_id);
            convertView.setTag(vievHolder);   // сохраняем в памяти ссылки на уже имеющиеся элементы
            listViewHolders.add(vievHolder);  // и записываем в массив листов новый элемент и так столько раз сколько позиций в БД
        } else {
            vievHolder = (VievHolder) convertView.getTag();  // если в convertView не пусто значет получаем из него данные
        }

        vievHolder.tvData.setText(mainList.get(position).getDate());   // тут мы уже заполняем созданный элемент текстом принятым из вне
        vievHolder.tvNumbers.setText(mainList.get(position).getNumber());   // тут мы уже заполняем созданный элемент текстом
        vievHolder.layouts.setOnClickListener(new View.OnClickListener() {   // слушатель нажатий на весь элемент
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Viev_Activity.class);      // переходим на лист просмотра данных и передаем ему все что можно
                i.putExtra(Constants.NUMBRS, mainList.get(position).getNumber());  // из mainList который получили при создании
                i.putExtra(Constants.DATA_TIM, mainList.get(position).getDate());
                i.putExtra(Constants.COMMON_MASSA, mainList.get(position).getMassa_common());
                i.putExtra(Constants.MAS_AX1, mainList.get(position).getAxsi1());
                i.putExtra(Constants.MAS_AX2, mainList.get(position).getAxsi2());
                i.putExtra(Constants.MAS_AX3, mainList.get(position).getAxsi3());
                i.putExtra(Constants.MAS_TRALER, mainList.get(position).getAxsi4());
                i.putExtra(Constants.NUM_MAS_TRALER, mainList.get(position).getNum_axsi4());
                context.startActivity(i);
            }
        });
        return convertView;
    }


}
