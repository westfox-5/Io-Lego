package com.iolego.io_lego.Tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iolego.io_lego.R;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {
    private static final int COUNT = 4;

    // Images resources
    private static final int[] IMAGE_RES_IDS = {
            R.drawable.howto1, R.drawable.howto2, R.drawable.howto3, R.drawable.howto4 };

    // Text resources
    private static final int[] TITLES_RES_IDS = {
            R.string.tutorial_1, R.string.tutorial_2, R.string.tutorial_3, R.string.tutorial_4};

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialScreenFragment.newInstance(IMAGE_RES_IDS[position], TITLES_RES_IDS[position], position);
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}