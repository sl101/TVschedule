package com.group.tv_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyActivity extends AppCompatActivity {

//    public static final String LOG = "myLog";
    public static final String EXTRA_MESSAGE_PAGENUMBER = "extra_message_pageNumber";
    static final int PAGE_COUNT = 14;

    public static ViewPager pager;
    public static PagerAdapter pagerAdapter;
    private Calendar calendar;
    private int tooday;


    private String choisedCannel;
    String [] dayOfWeek;
    private int pageFromAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dayOfWeek = getResources().getStringArray(R.array.days_of_week);
        calendar = Calendar.getInstance();

        Intent intent = getIntent();
        choisedCannel = intent.getStringExtra(Main.EXTRA_MESSAGE_NAME);
        setTitle(choisedCannel);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                pager.setAdapter(pagerAdapter);
            }
        });

        tooday = calendar.get(Calendar.DAY_OF_WEEK)+6;
        if(tooday>7)
            tooday = tooday-8;
        pager.setCurrentItem(tooday);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

//                Log.d(LOG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
//                Log.d(LOG, "onPageScrolled, position = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            Log.d(LOG, "getPageTitle position = " + position);
            int counter = position;
            if (position>6)
                counter = position-7;

//            Log.d(LOG, "getPageTitle counter = " + counter);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(dayOfWeek[counter]+"\n");
            stringBuilder.append("\n");
            stringBuilder.append(Main.days[position]);
            CharSequence value = stringBuilder;

            return value;
        }
        @Override
        public Fragment getItem(int position) {
            return com.group.tv_schedule.PageFragment.newInstance(position,choisedCannel);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

}
