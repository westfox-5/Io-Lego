package com.iolego.io_lego.Tutorial;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iolego.io_lego.R;
import com.viewpagerindicator.CirclePageIndicator;

public class Tutorial extends AppCompatActivity {
    private ViewPager mPager;
    private TutorialPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mPager = (ViewPager) findViewById(R.id.tutorial_pager);
        mPager.setOffscreenPageLimit(3); // Helps to keep fragment alive, otherwise I will have to load again images
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        titleIndicator.setViewPager(mPager);
    }
}
