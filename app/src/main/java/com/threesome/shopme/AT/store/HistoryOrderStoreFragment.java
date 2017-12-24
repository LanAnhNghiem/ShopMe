package com.threesome.shopme.AT.store;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryOrderStoreFragment extends Fragment {


    public HistoryOrderStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_order_store, container, false);
    }

}
