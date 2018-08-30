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

public class ListAdapter extends BaseAdapter{

//    public static final String LOG = "myLog";

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<com.group.tv_schedule.OneChanelObject> objects;

    CheckBox checkBox;
    com.group.tv_schedule.OneChanelObject oneChanelObject;
    int numberPosition;

     ListAdapter(Context context, ArrayList<com.group.tv_schedule.OneChanelObject> tvChanels){
         ctx = context;
         objects = tvChanels;
         lInflater = (LayoutInflater) ctx
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }

    // ���-�� ���������
    @Override
    public int getCount() {
        return objects.size();
    }

    // ������� �� �������
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // ������� �� ������
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ����� ������
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // ���������� ���������, �� �� ������������ view
        View view = convertView;
            if (view == null) {

                 view = lInflater.inflate(R.layout.list_item, parent, false);
            }

        oneChanelObject = getOneChanelObject(position);
        numberPosition = position;

            // ��������� View � ������ ������ �������
            TextView textView = (TextView) view.findViewById(R.id.tv_name);
            textView.setText(oneChanelObject.chanel);

            //��� ���� �������������� ��������� �������� � ����
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(myCheckChangList);
            checkBox.setTag(position);
            checkBox.setChecked(oneChanelObject.box);


        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (objects.get(position).box) {
//                    Log.d(LOG, "checkBox: "+objects.get(position).box);
//                    Log.d(LOG, "chanel= "+objects.get(position).chanel);
                    com.group.tv_schedule.SQLDataProgress checkin = new com.group.tv_schedule.SQLDataProgress("tvChannel", objects.get(position).chanel, null, null, true);

//                    SQLDataProgress sqlDataProgress = new SQLDataProgress(true);
//                Log.d(LOG, "sqlDataProgress: \n"+sqlDataProgress.tvNameArr);
//                        ListAdapter adapter = new ListAdapter(ctx.getApplicationContext(),value);
//                        Main.channelListView.setAdapter(adapter);

                } else {
//                    Log.d(LOG, "checkBox: "+objects.get(position).box);
//                    Log.d(LOG, "chanel= "+objects.get(position).chanel);
                    com.group.tv_schedule.SQLDataProgress checkin = new com.group.tv_schedule.SQLDataProgress("tvChannel", objects.get(position).chanel, null, null, false);

//                    SQLDataProgress sqlDataProgress = new SQLDataProgress(true);
//                Log.d(LOG, "sqlDataProgress: \n"+sqlDataProgress.tvNameArr);
//                        ListAdapter adapter = new ListAdapter(ctx.getApplicationContext(), value);
//                        Main.channelListView.setAdapter(adapter);
                }
            }
        });

        return view;
    }

    // ����� �� �������
    com.group.tv_schedule.OneChanelObject getOneChanelObject(int position) {
        return ((com.group.tv_schedule.OneChanelObject) getItem(position));
    }


    // ���������� ��� ���������
    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // ������ ������ ������ (� ������� ��� ���)
            getOneChanelObject((Integer) buttonView.getTag()).box = isChecked;

        }
    };
}
