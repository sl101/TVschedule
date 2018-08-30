package com.group.tv_schedule;

public class SheduleObject {

    String data;
    String nameChannel;
    String nameShedule;
    String time;
    String urlShedule;
    boolean box;

    public SheduleObject(String data, String nameChannel, String nameShedule, String time, String urlShedule, int favorite) {
        this.data = data;
        this.nameChannel = nameChannel;
        this.nameShedule = nameShedule;
        this.time = time;
        this.urlShedule = urlShedule;
        if (favorite==0){
            box = false;
        }else{
            box = true;
        }
    }
}
