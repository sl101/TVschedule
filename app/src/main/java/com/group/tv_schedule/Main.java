package com.group.tv_schedule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.GregorianCalendar.DAY_OF_WEEK;

public class Main extends AppCompatActivity {

    public static final String LOG = "myLog";

    public static final String EXTRA_MESSAGE_NAME = "extra_message_name";
    private static final int URL_START = 25;

    //константа для преферанса
    final String SAVED_CHOISE = "saved_choise";

    public  static ProgressDialog progressDialog;
    MyTask mt;
    public static ListView channelListView;
    //период отображения расписания 2 недели
    public static String[] days = new String[14];

    //то в чем будем хранить данные пока не передадим адаптеру
    //список каналов
    public ArrayList<String> nameChannelList = new ArrayList<>();
    //список URL адресов каналов
    public ArrayList<String> urlChannelList = new ArrayList<>();
    //список каналов с указанием метки
//    public ArrayList<Integer> favouritesChannelList = new ArrayList<>();

    ArrayList<com.group.tv_schedule.OneChanelObject> oneChanelObjects;
    ArrayList<com.group.tv_schedule.SheduleObject> mySheduleObjects;

    //список списков дней
    public static ArrayList<ArrayList<String>> dataOneDayList = new ArrayList<>();
    //список списков телеканала
    public static ArrayList<ArrayList<String>> tvChannelProgrammsList = new ArrayList<>();
    //список списков  время передачи
    public static ArrayList<ArrayList<String>> timeProgrammsList = new ArrayList<>();
    //список списков передач в день
    public static ArrayList<ArrayList<String>> nameProgrammsList = new ArrayList<>();
    //список списков URL адресов передач
    public static ArrayList<ArrayList<String>> urlProgrammsList = new ArrayList<>();
    //список списков передач с указанием метки
    public static ArrayList<ArrayList<Integer>> channelFavouritesArr = new ArrayList<>();

    //база данных
    public static SQLiteDatabase dataBase;
//    private AdView mAdView;
    public com.group.tv_schedule.SQLDataProgress nameList;

    //преференс для запоминания варианта входа
    SharedPreferences sPref;

    boolean isItemClick = false;
    public  static boolean myScheduleMenu;

    public String tvChannelClick;


    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        myScheduleMenu = false;
        // создаем базу и таблицы если их еще нет
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataBase = com.group.tv_schedule.TvChannelDatabase.getInstance(getApplicationContext(), com.group.tv_schedule.TvChannelDatabase.DB_NAME).getWritableDatabase();
            }
        });

        // проверяем есть ли база, если есть:
        if (dataBase != null) {
            // загружаем весь список или список избранных
                getArrData(loadChoise());
        }

        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(LOG, "\nOnItemClickListener click " + position);
//                if menu "Моя программа" is chosen load personal schedule
                if(myScheduleMenu){

                    Intent intent = new Intent(getApplicationContext(), com.group.tv_schedule.ShowProgramm.class);
                    String message = mySheduleObjects.get(position).urlShedule;
                    if (message.length() != 0) {
//            Log.d(LOG, "\nmessage = " + message);
//            Log.d(LOG, "\nmessage.length() = " + message.length());
                        intent.putExtra(com.group.tv_schedule.PageFragment.EXTRA_MESSAGE, message);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_activated),Toast.LENGTH_LONG).show();
                    }
                }else{
                    startChannel(position);
                }

            }
        });
    }

    void getArrData(final boolean value) {
//                    Log.d(LOG, "\ndataBase created " + dataBase.getPath());
//        Log.d(LOG, "\nload getArrData with value = " + value);

        // работаем с таблицей №1 - список каналов

        nameList = new com.group.tv_schedule.SQLDataProgress(value);
        channelListView = (ListView) findViewById(R.id.listViewChannel);
//        Log.d(LOG, "nameList : \n"+nameList.tvNameArr);

//        Log.d(LOG, "\nnameList.tvNameArr = " + nameList.tvNameArr);
//            Log.d(LOG, "\nnameList.size() = \n" + nameList.tvNameArr.size());
        // если список не null загружаем список каналов
        if (nameList.tvNameArr.size() == 0 && !value) {

                // проверяем наличие интернета
                if (hasInternetConnection()) {
                    // запускаем AsyncTask
                    String url = getResources().getString(R.string.urlRoot);
//                    Log.d(LOG, "\nstart MyTask = " + url);
                     mt = new MyTask(Main.this);

                    mt.execute(url);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    finish();
//                    Log.d(LOG, "\n No Internet connection ");
                }
//            }
        }
        // иначе загружаем данные из интернета
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    fillData(value);
                    ListAdapter adapter = new ListAdapter(getApplicationContext(), oneChanelObjects);
                    channelListView.setAdapter(adapter);

                }
            });

        }
    }

    private void fillData(boolean value) {
        oneChanelObjects = new ArrayList<com.group.tv_schedule.OneChanelObject>();
        com.group.tv_schedule.SQLDataProgress objects = new com.group.tv_schedule.SQLDataProgress(value);
//        Log.d(LOG, "value in fillData = "+ value);
//        Log.d(LOG, "objects.tvNameArr.size = "+ objects.tvNameArr.size());
        for (int i = 0; i < objects.tvNameArr.size(); i++) {

            oneChanelObjects.add(new com.group.tv_schedule.OneChanelObject(objects.tvNameArr.get(i), objects.tvUrlArr.get(i), objects.tvFavouritesArr.get(i)));
//            Log.d(LOG, "\noneChanelObjects " + (i + 1) + " : " + oneChanelObjects.get(i).chanel + "  " + oneChanelObjects.get(i).box);
        }
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
//                    Log.d("myLog", "test: wifi conncetion found");
                    return true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
//                    Log.d("myLog", "test: mobile connection found");
                    return true;
                }
        }
        return false;
    }

    private void startChannel(int position) {
//       Log.d(LOG, "\nstart startChannel ="+position);
        channelListView.setEnabled(false);
        isItemClick = true;
        dataOneDayList.clear();
        tvChannelProgrammsList.clear();
        timeProgrammsList.clear();
        nameProgrammsList.clear();
        urlProgrammsList.clear();
        channelFavouritesArr.clear();

//        Log.d(LOG, "oneChanelObjects: "+oneChanelObjects.get(position).chanel);
//        Log.d(LOG, "nameChannelList size: "+nameChannelList.size());
        // the channel was chosen
        tvChannelClick = oneChanelObjects.get(position).chanel;
//       Log.d(LOG, "\ntvChannelClick = " + tvChannelClick);
        // data today
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        // найдем массив используемых дат, включающий сегодняшнюю дату в первой неделе
        int delta = -now.get(DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta);
//        Log.d(LOG, "\n" + format.format(now.getTime()));
        for (int i = 0; i < days.length; i++) {
            days[i] = format.format(now.getTime());
//            Log.d(LOG, "\n"+days[i]);
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (int i = 0; i < days.length; i++) {
            com.group.tv_schedule.SQLDataProgress sqlDataProgress = new com.group.tv_schedule.SQLDataProgress(tvChannelClick, days[i]);
//            Log.d(LOG, "\nsqlDataProgress = "+sqlDataProgress.nameArr);
//           Log.d(LOG, "\nday "+ i +" = "+tvChannelClick+ "  "+days[i]);
            // читаем расписание по дням выбранного канала из базы данных
//           Log.d(LOG, "\nnameList.nameArr.size() "+nameList.nameArr.size());

            dataOneDayList.add(sqlDataProgress.dataArr);
            tvChannelProgrammsList.add(sqlDataProgress.tvChannelArr);
            timeProgrammsList.add(sqlDataProgress.timeArr);
            nameProgrammsList.add(sqlDataProgress.nameArr);
            urlProgrammsList.add(sqlDataProgress.urlArr);
            channelFavouritesArr.add(sqlDataProgress.channelFavouritesArr);
        }
//        Log.d(LOG, "\nnameProgrammsList.get(0) = "+nameProgrammsList.get(0));
// if the schedule was saved in database
        if (nameProgrammsList.get(0).size() != 0) {

//           Log.d(LOG, "\nstart Intent  ");
            channelListView.setEnabled(true);
            Intent intentPut = new Intent(getApplicationContext(), com.group.tv_schedule.MyActivity.class);
            intentPut.putExtra(EXTRA_MESSAGE_NAME,tvChannelClick);
            startActivity(intentPut);
        }// иначе, загружаем из интернета расписание
        else {

            dataOneDayList.clear();
            tvChannelProgrammsList.clear();
            timeProgrammsList.clear();
            nameProgrammsList.clear();
            urlProgrammsList.clear();
            channelFavouritesArr.clear();
            // проверяем наличие интернета
            if (hasInternetConnection()) {
                isItemClick = true;

//                startProgressDialog();
//                MyTask mt = new MyTask();
//                startProgressDialog();
//                rotaded = true;
                 mt = new MyTask(Main.this);
//               Log.d(LOG, "\n tvChannelClick "+tvChannelClick);
//                mt.execute(urlChannelList.get(position), nameChannelList.get(position));

                SQLDataProgress urlFortvChannelClick = new SQLDataProgress(tvChannelClick);
                Log.d(LOG, "\n urlFortvChannelClick = "+ urlFortvChannelClick.getTvUrl);
//                mt.execute(urlChannelList.get(position), tvChannelClick);

                mt.execute(urlFortvChannelClick.getTvUrl, tvChannelClick);
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
//               Log.d(LOG, "\n No Internet connection ");
            }
        }
    }

    private void startProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    class MyTask extends AsyncTask<String, ArrayList<String>, Void> {

        Main mActivity;
        ArrayList<String> forHelp = new ArrayList<>();
        ArrayList<String> insertData = new ArrayList<>();
        String tvChannelClickInTask;

        public MyTask(Main activity) {

            mActivity = activity;
            lockScreenOrientation();
            mActivity.startProgressDialog();
        }


        @Override
        protected Void doInBackground(String... params) {

            //Тут парсим список каналов и их url
            //Здесь хранится будет разобранный html документ
            Document doc;
            try {
                //Считываем заглавную страницу
//                Log.d(LOG,"\nurl = "+params[0]);
                doc = Jsoup.connect(params[0]).get();

                //Если всё считалось, то вытаскиваем из считанного html документа данные
                if (doc != null) {
//                        Log.d(LOG, "doc = "+ doc);
                    // если был выбран канал работаем с таблицей №2
                    if (isItemClick) {

                        ArrayList<String> urlList = new ArrayList<>();
                        ArrayList<String> timeOneDayList = new ArrayList<>();
                        ArrayList<String> nameOneDayList = new ArrayList<>();
                        ArrayList<String> urlOneDayList = new ArrayList<>();
                        tvChannelClickInTask = params[1];

                        for (Element titles : doc.body().getElementsByAttributeValueContaining("id", "day-week-num")) {
                            for (Element dayUrl : titles.getElementsByAttribute("href")) {
                                urlList.add(dayUrl.attr("abs:href"));
                            }
                        }

                        for (int i = 0; i < urlList.size(); i++) {
//                            Log.d(LOG, "\n urlList.get(i) "+i+ " = "+urlList.get(i) );
//                            Log.d(LOG, "\n tvChannelClickInTask = "+tvChannelClickInTask );
                            String str = urlList.get(i).substring(params[0].length()+1,params[0].length()+ 9);
//                            String str = urlList.get(i).substring(30, 38);
//                            Log.d(LOG, "\n str "+str );
                            String year = str.substring(4, 8);
                            String month = str.substring(2, 4);
                            String day = str.substring(0, 2);
                            String string = day + "." + month + "." + year;

                            timeOneDayList.clear();
                            forHelp.clear();
                            nameOneDayList.clear();
                            urlOneDayList.clear();

                            Document newDoc = Jsoup.connect(urlList.get(i)).get();

                            if (newDoc != null) {

                                for (Element timeTag : newDoc.body().getElementsByClass("time")) {
                                    timeOneDayList.add(timeTag.text());
                                }

                                for (Element dayTag : newDoc.body().getElementsByClass("item")) {
                                    forHelp.add(dayTag.text());
                                }

                                Elements elements = newDoc.select("td.item");
                                for (int j = 0; j < elements.size(); j++) {
                                    Elements elem = elements.get(j).select("a");
                                    if (elem.attr("href") != null) {
                                        urlOneDayList.add(elements.get(j).select("a").attr("href"));
                                    } else {
                                        urlOneDayList.add("null");
                                    }
                                }
                            }

                            for (int j = 0; j < forHelp.size(); j++) {
                                nameOneDayList.add(timeOneDayList.get(j) + "   " + forHelp.get(j));
                            }
                            for (int k = 0; k <nameOneDayList.size(); k++) {
                                String date = string;
                                String tvChannel = tvChannelClickInTask;
                                String time = timeOneDayList.get(k);
                                String name = nameOneDayList.get(k);

                                if (nameOneDayList.get(k).contains("\'")) {
                                    name = nameOneDayList.get(k).replace("\'", "&");
                                }

                                String url = urlOneDayList.get(k);
                                int favourites = 0;
                                String insertTvChannel = "INSERT INTO tvShow( _data, _tvChannel, _time, _name, _url, _favourites) VALUES('" + date + "','" + tvChannel + "','" + time + "', '" + name + "','" + url + "','" + favourites + "');";
//            Log.d(LOG, "\n name "+name );
                                insertData.add(insertTvChannel);
                            }
//                            SQLDataProgress sqlDat = new SQLDataProgress(string, tvChannelClickInTask, timeOneDayList, nameOneDayList, urlOneDayList);
                        }

                        for (int j = 0; j <insertData.size() ; j++) {
                            Main.dataBase.execSQL(insertData.get(j));
//                            Log.d(LOG, "\n insertData "+insertData.get(j) );
                        }

                    }// работаем с таблицей №1
                    else {

                        for (Element dayTag : doc.body().getElementsByAttribute("value")) {

                            // записываем в аррей листы данные с интернета
                            nameChannelList.add(dayTag.text());
//                            Log.d(LOG, "\n nameChannelList "+nameChannelList);
                            urlChannelList.add(getResources().getString(R.string.urlRoot) + dayTag.val());
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_not_get), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_not_get), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            Log.d(LOG, "\nrotated = "+rotaded);
            if (progressDialog!=null) {
                progressDialog.dismiss();
                unlockScreenOrientation();
            }

            if (isItemClick) {

                Intent intent = new Intent(getApplicationContext(), com.group.tv_schedule.MyActivity.class);
                intent.putExtra(EXTRA_MESSAGE_NAME, tvChannelClickInTask);
//                Log.d(LOG, "\ntvChannelClickInTask = "+tvChannelClickInTask);
                startActivity(intent);
                channelListView.setEnabled(true);

                isItemClick = false;
            } else {

                // выбираем только каналы
                nameChannelList = chengeList(nameChannelList);
                urlChannelList = chengeList(urlChannelList);

//                Log.d(LOG, "\nnameChannelList: "+nameChannelList);
                // записываем в базу - таблица №1
                com.group.tv_schedule.SQLDataProgress dataList = new com.group.tv_schedule.SQLDataProgress(nameChannelList, urlChannelList, false);


                oneChanelObjects = new ArrayList<com.group.tv_schedule.OneChanelObject>();
                com.group.tv_schedule.SQLDataProgress objects = new com.group.tv_schedule.SQLDataProgress(false);
                for (int i = 0; i < objects.tvNameArr.size(); i++) {

                    oneChanelObjects.add(new com.group.tv_schedule.OneChanelObject(objects.tvNameArr.get(i), objects.tvUrlArr.get(i), objects.tvFavouritesArr.get(i)));
//            Log.d(LOG, "\noneChanelObjects " + (i + 1) + " : " + oneChanelObjects.get(i).chanel + "  " + oneChanelObjects.get(i).box);
                }
                        ListAdapter adapter = new ListAdapter(getApplicationContext(),oneChanelObjects);
                        channelListView.setAdapter(adapter);
            }
        }

        private ArrayList<String> chengeList(ArrayList<String> value) {
            forHelp.clear();
            for (int i = 5; i < value.size(); i++) {
                forHelp.add(value.get(i));
            }
            value.clear();
            for (int i = 0; i < forHelp.size() - 6; i++) {
                value.add(forHelp.get(i));
            }
            for (int i = 0; i < value.size(); i++) {

                if (value.get(i).contains("кабельные") || value.get(i).contains("спутниковые")){
                    value.remove(i);
                }
                if (value.get(i).contains("http://tvgid.ua/channels//")){
                    value.remove(i);
                }
            }

            forHelp.clear();
//                Log.d(LOG, "\n" + value.size()+value);
            return value;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_all:
                myScheduleMenu =false;
                    saveChoise(false);
                    getArrData(loadChoise());
                break;
            case R.id.menu_favourit:
                myScheduleMenu =false;
                    saveChoise(true);
                    getArrData(loadChoise());
                break;
            case R.id.my_shedule:
                loadMyShedule();

                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMyShedule() {
//        String result = "Выбраны каналы:";
//
        myScheduleMenu = true;
        mySheduleObjects = new ArrayList<com.group.tv_schedule.SheduleObject>();
//        channelListView = (ListView) findViewById(R.id.listViewChannel);
        com.group.tv_schedule.SQLDataProgress sqlDataProgress = new com.group.tv_schedule.SQLDataProgress();
//        Log.d(LOG, "MyProgramm :");
        for (int i = 0; i <sqlDataProgress.tvNameArr.size() ; i++) {
//            Log.d(LOG, "\n"+sqlDataProgress.tvMyData.get(i)+"  "+sqlDataProgress.tvMyChanel.get(i)+"  "+sqlDataProgress.tvNameArr.get(i)+"  "+sqlDataProgress.tvUrlArr.get(i)+"  "+sqlDataProgress.tvFavouritesArr.get(i));
            mySheduleObjects.add(new com.group.tv_schedule.SheduleObject(sqlDataProgress.tvMyData.get(i), sqlDataProgress.tvMyChanel.get(i), sqlDataProgress.tvNameArr.get(i), sqlDataProgress.tvMyTime.get(i), sqlDataProgress.tvUrlArr.get(i), sqlDataProgress.tvFavouritesArr.get(i)));
        }
        com.group.tv_schedule.ScheduleListAdapter mySheduleAdapter = new com.group.tv_schedule.ScheduleListAdapter(getApplicationContext(),mySheduleObjects);
//        PageFragment.lv.setAdapter(mySheduleAdapter);
        channelListView.setAdapter(mySheduleAdapter);
    }

    //сохраняем преференс
    void saveChoise(boolean value) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(SAVED_CHOISE, value);
        ed.commit();
    }

    //загружаем выбор из преферанса
    boolean loadChoise() {
        sPref = getPreferences(MODE_PRIVATE);
        boolean savedText = sPref.getBoolean(SAVED_CHOISE, false);
//        Log.d(LOG, "preferense is " +savedText);
        return savedText;
    }

}
