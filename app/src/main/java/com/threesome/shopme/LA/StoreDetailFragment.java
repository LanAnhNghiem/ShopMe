package com.threesome.shopme.LA;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LanAnh on 23/12/2017.
 */

public class StoreDetailFragment extends Fragment {
    private static final String TAG = StoreDetailFragment.class.getSimpleName();
    private CarouselView carouselView;
    private String idStore = "";
    private RecyclerView rvCateSection;
    private LinearLayoutManager layoutManager;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mCategory, mProduct;
    private ArrayList<Category> mCateList = new ArrayList<>();
    private ArrayList<Product> mProList = new ArrayList<>();
    private HashMap<String, ArrayList<Product>> mList = new HashMap<>();
    private int[] samples = {R.drawable.product_default, R.drawable.product_default, R.drawable.product_default, R.drawable.product_default};
    private ArrayList<Categories> titleList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Drawable mPlaceHolder;
    private boolean isCalled = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idStore = getArguments().getString(com.threesome.shopme.AT.utility.Constant.ID_STORE);
            Log.d(TAG, idStore);
        }
        mCategory = mData.child(Constant.CATEGORIES_BY_STORE).child(idStore);
        mProduct = mData.child(Constant.PRODUCTS_BY_CATEGORY);
        progressDialog = new ProgressDialog(getActivity());
        mPlaceHolder = ContextCompat.getDrawable(getContext(),R.drawable.default_image);
        showProgress();
        new LoadDataTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_detail, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        rvCateSection = view.findViewById(R.id.rvCateSection);
        return view;
    }
    public void addCarouselView(){
        final ArrayList<Product> tmp = new ArrayList<>();
        for(int i=0;i<5;i++){
            tmp.add(mProList.get(i));
        }

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                GlideApp.with(getContext()).load(mProList.get(position).getImage()).placeholder(scaleImage(mPlaceHolder, Constant.SCREEN_WIDTH, (int)(Constant.SCREEN_WIDTH/ Constant.GOLDEN_RATIO)))
                        .override(Constant.SCREEN_WIDTH, (int)(Constant.SCREEN_WIDTH / Constant.GOLDEN_RATIO))
                        .centerCrop().into(imageView);
            }
        };
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(5);

//        carouselView.setImageListener(new ImageListener() {
//            @Override
//            public void setImageForPosition(int position, ImageView imageView) {
//
//            }
//        });
    }
    public void addListCategories(){
        if(isCalled == false){
            for(Map.Entry<String, ArrayList<Product>> entry: mList.entrySet()){
                titleList.add(new Categories(entry.getKey(), entry.getValue()));
                Log.d(TAG, entry.getKey()+ " /////"+entry.getValue());
            }
            layoutManager = new LinearLayoutManager(getContext());
            CategoriesAdapter adapter = new CategoriesAdapter(titleList, getContext());
            rvCateSection.setLayoutManager(layoutManager);
            rvCateSection.setAdapter(adapter);
        }
        isCalled = true;
    }

//    public ArrayList<Product> getData(){
//        ArrayList<Product> list = new ArrayList<>();
//        list.add(new Product("Áo", "150000", "áo thun Thái lan"));
//        list.add(new Product("Quần", "150000", "áo thun Thái lan"));
//        list.add(new Product("Váy", "150000", "áo thun Thái lan"));
//        list.add(new Product("Kẹo", "150000", "áo thun Thái lan"));
//        return list;
//    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mList.clear();
                        mProList.clear();
                        mCateList.clear();
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Log.d(TAG, String.valueOf(data.child("name").getValue()));
                            final Category category = data.getValue(Category.class);
                            mCateList.add(category);
                            mProduct = mData.child(Constant.PRODUCTS_BY_CATEGORY).child(category.getId());
                            final ArrayList<Product> tmpList = new ArrayList<>();
                            mProduct.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    tmpList.clear();
                                    //mList.clear();
                                    int index = 0;
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        Log.d(TAG, String.valueOf(data.getValue()));
                                        Product product = data.getValue(Product.class);
                                        mProList.add(product);
                                        if(index < 4){
                                            tmpList.add(product);
                                        }
                                        index++;
                                    }
//
                                    mList.put(category.getName(), tmpList);
                                    //publishProgress();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                publishProgress();
                            }
                        }, 500);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            hideProgress();
            addCarouselView();
            addListCategories();

        }
    }
    private void showProgress (){
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }
    public Drawable scaleImage (Drawable image, int sizeX, int sizeY) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(getResources(), bitmapResized);

        return image;

    }
}
