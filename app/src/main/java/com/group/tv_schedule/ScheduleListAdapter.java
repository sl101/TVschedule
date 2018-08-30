package com.group.tv_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.ArrayList;

public class ScheduleListAdapter extends BaseAdapter {

//    public static final String LOG = "myLog";

    Context context;
    LayoutInflater lInflater;

    com.group.tv_schedule.SheduleObject sheduleObject;
    ArrayList<com.group.tv_schedule.SheduleObject> objects;

    int numberPosition;
    CheckBox checkBox;

    ScheduleListAdapter(Context context, ArrayList<com.group.tv_schedule.SheduleObject> sqlData){
        this.context = context;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        objects = sqlData;
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }

        sheduleObject = getOneChanelObject(position);
        numberPosition = position;


        // заполняем View в пункте списка данными
        TextView textView = (TextView) view.findViewById(R.id.tv_name);

        if (Main.myScheduleMenu){
            textView.setText(sheduleObject.data +"\t"+ sheduleObject.nameChannel+"\t"+sheduleObject.nameShedule);
        }else{
            textView.setText(sheduleObject.nameShedule);
        }


        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(myCheckChangList);
        checkBox.setTag(position);
        checkBox.setChecked(sheduleObject.box);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (objects.get(position).box) {
//                    Log.d(LOG,"\n"+position+"\n"
//                                    +objects.get(position).nameChannel
//                            + "\n"+ objects.get(position).data
//                            +"\n" +objects.get(position).nameShedule
//                    );
//                    Log.d(LOG,"\n day = "+Main.days[pageNumber]);
                    com.group.tv_schedule.SQLDataProgress checkin = new com.group.tv_schedule.SQLDataProgress("tvShow", objects.get(position).nameChannel, objects.get(position).data, objects.get(position).nameShedule, true);
//
//                    chengeArrayLists(position/*, false*/);


                } else {
                    com.group.tv_schedule.SQLDataProgress checkin = new com.group.tv_schedule.SQLDataProgress("tvShow", objects.get(position).nameChannel, objects.get(position).data, objects.get(position).nameShedule, false);
//                    SQLDataProgress checkin = new SQLDataProgress("tvShow", sqlDP.tvChannelArr.get(position), Main.days[pageNumber], sqlDP.nameArr.get(position), false);
//                   chengeArrayLists(position/*, true*/);

                }
            }
        });
        return view;
    }

    // обработчик для чекбоксов
    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            getOneChanelObject((Integer) buttonView.getTag()).box = isChecked;

        }
    };

    // обьект по позиции
    com.group.tv_schedule.SheduleObject getOneChanelObject(int position) {
        return ((com.group.tv_schedule.SheduleObject) getItem(position));
    }


}
