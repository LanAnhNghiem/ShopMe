package com.threesome.shopme.LA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;

//import com.bumptech.glide.annotation.GlideModule;
/**
 * Created by LanAnh on 21/12/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    private ArrayList<Product> mList = new ArrayList<>();
    private Context mContext;
    private Drawable mPlaceHolder;
    public ProductAdapter(ArrayList<Product> list, Context context){
        this.mList = list;
        this.mContext = context;
        mPlaceHolder = ContextCompat.getDrawable(mContext, R.drawable.product_default);
    }
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ProductHolder holder = new ProductHolder(inflatedView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        GlideApp.with(mContext).load(mList.get(position).getImage()).placeholder(scaleImage(mPlaceHolder, Constant.PRODUCT_WIDTH, (int)(Constant.PRODUCT_WIDTH/Constant.GOLDEN_RATIO)))
                .override(Constant.PRODUCT_WIDTH, (int)(Constant.PRODUCT_WIDTH/Constant.GOLDEN_RATIO))
                .centerCrop().into(holder.imgProduct);
        holder.txtName.setText(mList.get(position).getName());
        String price = String.valueOf(mList.get(position).getPrice());
        holder.txtPrice.setText(price+" VND");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName;
        TextView txtPrice;
        public ProductHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
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
