package com.threesome.shopme.AT.store;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Kunpark_PC on 12/24/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment = null;
      switch (position){
          case 0 :
              fragment = new ProfileStoreFragment();
              break;
          case 1:
              fragment = new HistoryOrderStoreFragment();
              break;
          case 2:
              fragment = new NotificationStoreFragment();
              break;
      }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
