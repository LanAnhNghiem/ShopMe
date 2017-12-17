package com.threesome.shopme.AT.createstore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;

import java.io.IOException;

public class ThirdFragment extends Fragment {


    private ImageView imgAvataStore;
    public static final int PICK_IMAGE = 1000;
    private Bitmap bitmap = null;
    public ThirdFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imgAvataStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        imgAvataStore = view.findViewById(R.id.imgAvataStore);
        return view;
    }
    private void chooseImage() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, Constant.REQUEST_CODE_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_LOAD_IMAGE && resultCode == getActivity().RESULT_OK) {
            if (data.getAction() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
             //   bitmap = cropImage(bitmap);
               // presenter.updatePhoto(bitmap, idUser);
                imgAvataStore.setImageBitmap(bitmap);
            } else {
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                  //  bitmap = cropImage(bitmap);
                  //  presenter.updatePhoto(bitmap, idUser);
                    imgAvataStore.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
