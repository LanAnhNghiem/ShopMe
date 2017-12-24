package com.threesome.shopme.AT.store;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.threesome.shopme.R;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountStoreFragment extends Fragment implements View.OnClickListener {


    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private String[] colors;
    private ImageView imgInfomation, imgHistory, imgNotification;
    private TextView txtInfomation, txtHistory, txtNotification, txtCountNotification;
    private LinearLayout layoutInfomation, layoutHistory, layoutNotification;
    private int indexTabBar = 0;

    public AccountStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager);
        colors = getActivity().getResources().getStringArray(R.array.default_preview);
    }

    private void addControls(View view) {
        viewPager = view.findViewById(R.id.viewPageProfileStore);
        layoutInfomation = view.findViewById(R.id.layoutInfoStore);
        layoutHistory = view.findViewById(R.id.layoutHistory);
        layoutNotification = view.findViewById(R.id.layoutNotification);

        imgInfomation = view.findViewById(R.id.imgInfoStore);
        imgHistory = view.findViewById(R.id.imgHistoryStore);
        imgNotification = view.findViewById(R.id.imgNotification);
        txtInfomation = view.findViewById(R.id.txtInfoStore);
        txtHistory = view.findViewById(R.id.txtHistoryStore);
        txtNotification = view.findViewById(R.id.txtNotification);
        txtCountNotification = view.findViewById(R.id.txtCountNotification);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_store, container, false);
        // navigationTabBar = view.findViewById(R.id.tabBarNavigation);
        addControls(view);
        addEvents();
        setUpViewPager(view);
        return view;
    }

    private void addEvents() {
        layoutInfomation.setOnClickListener(this);
        layoutHistory.setOnClickListener(this);
        layoutNotification.setOnClickListener(this);
    }

    private void setUpViewPager(View view) {
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layoutInfoStore){
            indexTabBar = 0;
            setUpTabBar ();
        }if (id == R.id.layoutHistory){
            indexTabBar = 1;
            setUpTabBar ();
        }if (id == R.id.layoutNotification){
            indexTabBar = 2;
            setUpTabBar ();
        }
        viewPager.setCurrentItem(indexTabBar);
    }

    private void setUpTabBar() {
        if (indexTabBar == 0){
            imgInfomation.setImageResource(R.drawable.ic_info_selected);
            imgNotification.setImageResource(R.drawable.ic_earth_nonselected);
            imgHistory.setImageResource(R.drawable.ic_history_nonselected);

            txtInfomation.setTextColor(getResources().getColor(R.color.color_green));
            txtHistory.setTextColor(getResources().getColor(R.color.btnRegister));
            txtNotification.setTextColor(getResources().getColor(R.color.btnRegister));

        }else if (indexTabBar == 1){
            imgInfomation.setImageResource(R.drawable.icon_info_nonselect);
            imgNotification.setImageResource(R.drawable.ic_earth_nonselected);
            imgHistory.setImageResource(R.drawable.ic_history_selected);

            txtInfomation.setTextColor(getResources().getColor(R.color.btnRegister));
            txtHistory.setTextColor(getResources().getColor(R.color.color_green));
            txtNotification.setTextColor(getResources().getColor(R.color.btnRegister));

        }else if (indexTabBar == 2){
            imgInfomation.setImageResource(R.drawable.icon_info_nonselect);
            imgNotification.setImageResource(R.drawable.ic_notification_selected);
            imgHistory.setImageResource(R.drawable.ic_history_nonselected);

            txtInfomation.setTextColor(getResources().getColor(R.color.btnRegister));
            txtHistory.setTextColor(getResources().getColor(R.color.btnRegister));
            txtNotification.setTextColor(getResources().getColor(R.color.color_green));
        }
    }
}
