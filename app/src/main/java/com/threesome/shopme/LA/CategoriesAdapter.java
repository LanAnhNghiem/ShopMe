package com.threesome.shopme.LA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.List;

/**
 * Created by LanAnh on 24/12/2017.
 */

public class CategoriesAdapter extends ExpandableRecyclerViewAdapter<CategoriesHolder, ProductItemHolder> {
    private Context mContext;
    private Drawable mPlaceHolder;
    public CategoriesAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.mContext = context;
        mPlaceHolder = ContextCompat.getDrawable(mContext, R.drawable.product_default);
    }

    @Override
    public CategoriesHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories_header, parent, false);
        return new CategoriesHolder(view);
    }

    @Override
    public ProductItemHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_landscape, parent, false);
        return new ProductItemHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ProductItemHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Product product = (Product) group.getItems().get(childIndex);
        holder.txtName.setText(product.getName());
        holder.txtPrice.setText(product.getPrice()+" VND");
        GlideApp.with(mContext).load(product.getImage()).placeholder(scaleImage(mPlaceHolder, Constant.PRODUCT_LANDSCAPE, (int)(Constant.PRODUCT_LANDSCAPE)))
                .override(Constant.PRODUCT_LANDSCAPE, (int)(Constant.PRODUCT_LANDSCAPE))
                .centerCrop().into(holder.imgProduct);
    }


    @Override
    public void onBindGroupViewHolder(CategoriesHolder holder, int flatPosition, ExpandableGroup group) {
        holder.txtTitle.setText(group.getTitle());
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
