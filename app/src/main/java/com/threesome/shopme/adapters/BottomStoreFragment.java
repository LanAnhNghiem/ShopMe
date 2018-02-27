package com.threesome.shopme.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.R;

/**
 * Created by Nhan on 11/26/2017.
 */

public class BottomStoreFragment extends BottomSheetDialogFragment {

    String mTag;

    public static BottomStoreFragment newInstance(String tag) {
        BottomStoreFragment f = new BottomStoreFragment();
        Bundle args = new Bundle();
        args.putString("TAG", tag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString("TAG");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_store, container, false);
        return view;
    }
}
