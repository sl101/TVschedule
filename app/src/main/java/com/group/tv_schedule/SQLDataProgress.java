package com.group.tv_schedule;

import android.database.Cursor;

import java.util.ArrayList;


public class SQLDataProgress {

    public static final String LOG = "myLog";

    public ArrayList<String> dataArr;
    public ArrayList<String> tvChannelArr;
    public ArrayList<String> timeArr;
    public ArrayList<String> nameArr;
    public ArrayList<String> urlArr;
    public ArrayList<Integer> channelFavouritesArr;


    public ArrayList<String> tvMyData;
    public ArrayList<String> tvMyChanel;
    public ArrayList<String> tvMyTime;

    public ArrayList<String> tvNameArr;
    public ArrayList<String> tvUrlArr;
    public ArrayList<Integer> tvFavouritesArr;

//    boolean box;
    String getTvUrl;


    SQLDataProgress(boolean value){
        String createTvShow = "CREATE TABLE IF NOT EXISTS tvShow(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, _data TEXT, _tvChannel TEXT,  _time TEXT, _name TEXT, _url TEXT, _favourites INTEGER);";
        Main.dataBase.execSQL(createTvShow);

        String createTvChannel = "CREATE TABLE IF NOT EXISTS tvChannel(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, _tvName TEXT, _urlArr TEXT, _favourites INTEGER);";
        Main.dataBase.execSQL(createTvChannel);

        // читаем таблицу каналов
        readTvChannel("tvChannel", value);
    }

    SQLDataProgress(String tvChannelClick){
        String selectTvUrl =  "SELECT * FROM tvChannel WHERE _tvName = '"+tvChannelClick+"';";
        Cursor cursor = Main.dataBase.rawQuery(selectTvUrl, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                getTvUrl = cursor.getString(2);

                cursor.moveToNext();
            }
        }
        cursor.close();
//        Log.d(LOG, "\n getTvUrl = " + getTvUrl);
    }

    SQLDataProgress(String data, String tvChannelClick, ArrayList<String> timeAr, ArrayList<String> nameAr, ArrayList<String> urlAr) {

        for (int i = 0; i <nameAr.size(); i++) {

            String date = data;
            String tvChannel = tvChannelClick;
            String time = timeAr.get(i);
//            long time = timeAr;
            String name = nameAr.get(i);

            if (nameAr.get(i).contains("\'")) {
               //name = "\""+nameAr.get(i)+"\"";
         name = nameAr.get(i).replace("\'", "&");
//                Log.d(LOG, "\n " + name);
            }

            String url = urlAr.get(i);
            int favourites = 0;
            String insertTvChannel = "INSERT INTO tvShow( _data, _tvChannel, _time, _name, _url, _favourites) VALUES('"+date+"','"+tvChannel+"','"+time+"', '"+name+"','"+url+"','"+favourites+"');";
////            Log.d(LOG, "\n "+insertTvChannel );
            Main.dataBase.execSQL(insertTvChannel);

        }

    }

    SQLDataProgress (String table, String tvCannel, String data, String name, boolean favoritClick){
        int favoritData = 0;
//        String tableName = table;
//        String tvName = name;
//        String isData = data;
        if (favoritClick) {
            favoritData = 1;
        }
        String insertTvChannel = "UPDATE "+table+" SET _favourites = '"+favoritData+"' WHERE _tvName = '"+tvCannel+"';";
        if (data != null) {
            insertTvChannel = "UPDATE "+table+" SET _favourites = '"+favoritData+"' WHERE _tvChannel = '"+tvCannel+"' AND _data = '"+data+"' AND _name = '"+name+"';";
        }
//        Log.d(LOG, "\n insertTvChannel "+insertTvChannel);
        Main.dataBase.execSQL(insertTvChannel);

    }

    SQLDataProgress(String tvChannelClick, String days){

        readTvShow(tvChannelClick, days);
    }

    public SQLDataProgress(String tvChannelClick, String day, boolean value) {
        dataArr = new ArrayList<>();
        tvChannelArr = new ArrayList<>();
        timeArr = new ArrayList<>();
        nameArr = new ArrayList<>();
        urlArr = new ArrayList<>();
        channelFavouritesArr = new ArrayList<>();
        String selectTvChannel =  "SELECT * FROM tvShow WHERE _data = '"+day+"' AND _tvChannel = '"+tvChannelClick+"' AND _favourites = '"+0+"';";
        if (value) {
            selectTvChannel =  "SELECT * FROM tvShow WHERE _data = '"+day+"' AND _tvChannel = '"+tvChannelClick+"' AND _favourites = '"+1+"';";
        }

        Cursor cursor = Main.dataBase.rawQuery(selectTvChannel, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                dataArr.add(cursor.getString(1));
                tvChannelArr.add(cursor.getString(2));
                timeArr.add(cursor.getString(3));
                if (cursor.getString(4).contains("&")) {
                    String name = cursor.getString(4).replace("&","\'");
                    nameArr.add(name);
                }else{
                    nameArr.add(cursor.getString(4));
                }
                urlArr.add(cursor.getString(5));
                channelFavouritesArr.add(cursor.getInt(6));
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public SQLDataProgress() {
        readTvChannel("tvShow", true);
    }


    private void readTvShow(String tvChannelClick, String days) {

        dataArr = new ArrayList<>();
        tvChannelArr = new ArrayList<>();
        timeArr = new ArrayList<>();
        nameArr = new ArrayList<>();
        urlArr = new ArrayList<>();
        channelFavouritesArr = new ArrayList<>();

//        Log.d(LOG, "\ntvChannelClick in readTvShow = " + tvChannelClick);

        String selectTvChannel =  "SELECT * FROM tvShow WHERE _data = '"+days+"' AND _tvChannel = '"+tvChannelClick+"';";
//        Log.d(LOG, "\nselectTvChannel = " + selectTvChannel);
        Cursor cursor = Main.dataBase.rawQuery(selectTvChannel, null);
//        Log.d(LOG, "\ncursor = " + cursor.getCount());?
        int columns = cursor.getColumnCount();
        StringBuilder stringBuilder = new StringBuilder();
//        if (columns == 0) {
//            Log.d(LOG, "\n no data ");
//        }
        if (cursor.moveToFirst()) {
//            Log.d(LOG, "\n read tvShow table ");
            while (!cursor.isAfterLast()) {
                stringBuilder.append("\n  ");
                for (int i = 0; i < columns; i++) {
                    stringBuilder.append(cursor.getString(i)+"   ");
                }
//                Log.d(LOG, "\nstringBuilder = " + stringBuilder);
                dataArr.add(cursor.getString(1));
                tvChannelArr.add(cursor.getString(2));
                timeArr.add(cursor.getString(3));
                if (cursor.getString(4).contains("&")) {
                    String name = cursor.getString(4).replace("&","\'");
                    nameArr.add(name);
//                    Log.d(LOG, "" + name);
                }else{
                    nameArr.add(cursor.getString(4));
                }
//                nameArr.add(cursor.getString(4));
//                Log.d(LOG,"\nnameArr: \n"+nameArr);
                urlArr.add(cursor.getString(5));
                channelFavouritesArr.add(cursor.getInt(6));
                cursor.moveToNext();
            }
        }
        cursor.close();
//                Log.d(LOG, "\n" + stringBuilder);
//        Log.d(LOG,"\nnameArr: \n"+nameArr);
    }


    SQLDataProgress(ArrayList<String> tvNameArr, ArrayList<String> tvUrlArr, boolean value){

        for (int i = 0; i < tvNameArr.size(); i++) {
            String nameValue = tvNameArr.get(i);
            String urlValue = tvUrlArr.get(i);
            int favourites = 0;
            if (value) {
                favourites = 1;
            }
            String insertTvChannel = "INSERT INTO tvChannel(_tvName, _urlArr, _favourites) VALUES('"+nameValue+"', '"+urlValue+"','"+favourites+"');";
            Main.dataBase.execSQL(insertTvChannel);
        }
        readTvChannel("tvChannel", value);
    }

    private void readTvChannel(String tableName, boolean value) {

        tvMyData = new ArrayList<>();
        tvMyChanel = new ArrayList<>();
        tvMyTime = new ArrayList<>();

        tvNameArr = new ArrayList<>();
        tvUrlArr = new ArrayList<>();
        tvFavouritesArr = new ArrayList<>();

        String selectTvChannel;
        if (value) {
            selectTvChannel =  "SELECT * FROM "+tableName+" WHERE _favourites = '"+1+"';";
        }else{
            selectTvChannel =  "SELECT * FROM "+tableName+";";
        }
//        Log.d(LOG, "\n selectTvChannel "+selectTvChannel);
        Cursor cursor = Main.dataBase.rawQuery(selectTvChannel, null);
        if(tableName.contains("tvShow")){
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    tvMyData.add(cursor.getString(1));
                    tvMyChanel.add(cursor.getString(2));
                    tvMyTime.add(cursor.getString(3));
                    tvNameArr.add(cursor.getString(4));
                    tvUrlArr.add(cursor.getString(5));
//                tvFavouritesArr.add(Integer.getInteger(cursor.getString(3)));
                    tvFavouritesArr.add(cursor.getInt(6));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }else{
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    tvNameArr.add(cursor.getString(1));
                    tvUrlArr.add(cursor.getString(2));
//                tvFavouritesArr.add(Integer.getInteger(cursor.getString(3)));
                    tvFavouritesArr.add(cursor.getInt(3));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

//        for (int i = 0; i <tvNameArr.size() ; i++) {
////            Log.d(LOG, "\n  " + tvNameArr.get(i)+"  " +tvUrlArr.get(i));
//        }

    }

}