package com.threesome.shopme.LA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by LanAnh on 23/12/2017.
 */

public class CategorySection extends StatelessSection{
    private static final String TAG = CategorySection.class.getSimpleName();
    private String cateTitle;
    private ArrayList<Product> mList;
    private Context mContext;
    private Drawable mPlaceHolder;

    public CategorySection(String title, ArrayList<Product> list, Context context) {
        //super(R.layout.layout_category_header, R.layout.item_product);
        super(new SectionParameters.Builder(R.layout.item_product).headerResourceId(R.layout.layout_category_header).build());
        this.cateTitle = title;
        this.mList = list;
        this.mContext = context;
        mPlaceHolder = ContextCompat.getDrawable(mContext, R.drawable.product_default);
    }


    @Override
    public int getContentItemsTotal() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ProductAdapter.ProductHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProductAdapter.ProductHolder productHolder = (ProductAdapter.ProductHolder)holder;
        productHolder.txtName.setText(mList.get(position).getName());
        productHolder.txtPrice.setText(mList.get(position).getPrice());
        GlideApp.with(mContext).load(mList.get(position).getImage()).placeholder(scaleImage(mPlaceHolder, Constant.PRODUCT_WIDTH, (int)(Constant.PRODUCT_WIDTH/ Constant.GOLDEN_RATIO)))
                .override(Constant.PRODUCT_WIDTH, (int)(Constant.PRODUCT_WIDTH/ Constant.GOLDEN_RATIO))
                .centerCrop().into(productHolder.imgProduct);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        CategoryHeaderHolder headerHolder = (CategoryHeaderHolder) holder;
        headerHolder.txtTitle.setText(cateTitle);
        headerHolder.txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Section "+cateTitle, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new CategoryHeaderHolder(view);
    }

    public Drawable scaleImage (Drawable image, int sizeX, int sizeY) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(mContext.getResources(), bitmapResized);

        return image;

    }
}
