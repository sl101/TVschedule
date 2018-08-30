package com.group.tv_schedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public static final String EXTRA_MESSAGE = "extra_message";
    static final String ARGUMENT_PAGE_NAME = "arg_page_name";
    public static final String LOG = "myLog";


    // Listview Adapter для вывода данных
    public static com.group.tv_schedule.ScheduleListAdapter adapter;
    public ArrayList<com.group.tv_schedule.SheduleObject> sqlData;

    public static ListView lv;


    public int pageNumber;
    public String selectedChannel;
    int backColor;

    static PageFragment newInstance(int page, String tvName) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putString(ARGUMENT_PAGE_NAME, tvName);
        pageFragment.setArguments(arguments);
        return pageFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
//        Log.d(LOG, "\npageNumber = "+ pageNumber);
        selectedChannel = getArguments().getString(ARGUMENT_PAGE_NAME);
//        Log.d(LOG, "\n selectedChannel = "+selectedChannel);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

//        Log.d(LOG,"\nselectedChannel = "+selectedChannel+"\npageNumber = " +pageNumber+ "\n day = "+Main.days[pageNumber]);
        com.group.tv_schedule.SQLDataProgress sqlDataProgress = new com.group.tv_schedule.SQLDataProgress(selectedChannel, Main.days[pageNumber]);
        sqlData = new ArrayList<com.group.tv_schedule.SheduleObject>();
//        Log.d(LOG,"\nsheduleObjec = "+sqlDataProgress.nameArr+"\n");

                for (int j = 0; j <sqlDataProgress.nameArr.size() ; j++) {
                    sqlData.add(new com.group.tv_schedule.SheduleObject(sqlDataProgress.dataArr.get(j), sqlDataProgress.tvChannelArr.get(j), sqlDataProgress.nameArr.get(j), sqlDataProgress.timeArr.get(j), sqlDataProgress.urlArr.get(j), sqlDataProgress.channelFavouritesArr.get(j)));
                }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        lv = (ListView) view.findViewById(R.id.listView1);

       adapter = new com.group.tv_schedule.ScheduleListAdapter(getActivity().getApplicationContext(), sqlData);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                lv.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });

        lv.setBackgroundColor(backColor);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.d(LOG, "position = "+position);
                 showProgramm(position);

            }
        });

        return view;
    }

    public void showProgramm(int position) {

        try {
            Intent intent = new Intent(getActivity(), com.group.tv_schedule.ShowProgramm.class);
            String message = Main.urlProgrammsList.get(pageNumber).get(position);

            if (message.length() != 0) {
//                Log.d(LOG, "\n "+ Main.dataOneDayList.get(pageNumber).get(position)+"  " + Main.nameProgrammsList.get(pageNumber).get(position));
//                Log.d(LOG, "\nmessage.length() = " + message.length());
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
            else{
                Toast.makeText(getActivity(), getResources().getString(R.string.not_activated),Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), getResources().getString(R.string.not_activated),Toast.LENGTH_LONG).show();
        }

    }
}
