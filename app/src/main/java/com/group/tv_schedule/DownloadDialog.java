package com.group.tv_schedule;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class DownloadDialog extends DialogFragment {
//    public static TextView textView;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.progressbar_layout, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
//        progressBar.setTit
//        textView = (TextView) v.findViewById(R.id.textView2);
//        tw1.setText((MainActivity.numPosition + 1) + "/"
//                + MainActivity.imagesInWeb.length);
//        return null;
        return v;
    }
}
