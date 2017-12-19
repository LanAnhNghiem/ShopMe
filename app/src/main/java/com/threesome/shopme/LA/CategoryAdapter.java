package com.threesome.shopme.LA;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;

import java.util.ArrayList;

/**
 * Created by LanAnh on 18/12/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Category> mList = new ArrayList<>();
    private final static int ITEM = 0;
    private final static int ADD_ITEM = 1;
    private boolean mIsSelected = false;
    private Context mContext;
    public CategoryAdapter(ArrayList<Category> mList, Context context, boolean isSelected) {
        this.mContext =context;
        this.mList = mList;
        this.mIsSelected = isSelected;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view;
        switch (viewType){
            case ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                return new CategoryHolder(view);
            case ADD_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_category, parent, false);
                return new AddHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mList != null){
            switch (holder.getItemViewType()){
                case ITEM:
                {
                    ((CategoryHolder)holder).setText(mList.get(position).getName()+"("+mList.get(position).getQuantity()+")");
                }
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mList != null){
            int lastIndex = mList.size();
            if(position == lastIndex){
                return ADD_ITEM;
            }
        }
        return ITEM;
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }

    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        TextView txtName;

        public CategoryHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void setText(String text){
            txtName.setText(text);
        }

        @Override
        public boolean onLongClick(View view) {

            return false;
        }

//        public void displaySelection(boolean isShow){
//            if(isShow){
//                btnSelect.setVisibility(View.VISIBLE);
//                btnUnselect.setVisibility(View.VISIBLE);
//            }else{
//                btnSelect.setVisibility(GONE);
//                btnUnselect.setVisibility(GONE);
//            }
//        }

        @Override
        public void onClick(View view) {

       }
    }

    public class AddHolder extends  RecyclerView.ViewHolder{
        public AddHolder(View itemView){
            super(itemView);
        }
    }
}
