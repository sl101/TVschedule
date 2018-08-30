package com.group.tv_schedule;

public class OneChanelObject {
    String chanel;
    String url;
    boolean box;

    OneChanelObject(String chanel, String url, int fav ){
        this.chanel = chanel;
        this.url = url;
        if (fav==0){
            box = false;
        }else{
            box = true;
        }
    }
}
