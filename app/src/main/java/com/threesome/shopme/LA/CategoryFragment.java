package com.threesome.shopme.LA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;

import java.util.ArrayList;

/**
 * Created by LanAnh on 18/12/2017.
 */

public class CategoryFragment extends Fragment {
    RecyclerView rvCategory;
    CategoryAdapter adapter;
    GridLayoutManager layoutManager;
    ArrayList<Category> mList = new ArrayList<>();
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    DatabaseReference categoriesByStore = mData.child(Constant.CATEGORIES_BY_STORE);
    private ProgressDialog progressDialog;
    private static final String mId = "9mbOtOnnPPg6jj4r0KPax48hspY2";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createData();
        progressDialog = new ProgressDialog(getActivity());
        categoriesByStore = categoriesByStore.child(mId);
        showProgress();
        new LoadDataTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        rvCategory = view.findViewById(R.id.rvCategory);
        layoutManager = new GridLayoutManager(getContext(), 3);
        rvCategory.setLayoutManager(layoutManager);
        adapter = new CategoryAdapter(mList, getContext(), false);
        rvCategory.setAdapter(adapter);
        rvCategory.addOnItemTouchListener(new ItemTouchListener(getContext(), rvCategory, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position == mList.size())
                    addCategory();
                else{
                    Intent intent = new Intent(getContext(), ListProductActivity.class);
                    intent.putExtra("cateId",mList.get(position).getId());
                    intent.putExtra("storeId", mList.get(position).getIdStore());
                    getContext().startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, final int position) {
                if(position != mList.size())
                    showDialog(position);

            }
        }));
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public void showDialog(final int position){
        final String id = mList.get(position).getId();
        CharSequence option[] = new CharSequence[]{"Edit","Remove"};
        final AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose option");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        dialogInterface.dismiss();
                        editNameDialog(position);
                        break;
                    case 1:
                        categoriesByStore.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Log.d("remove", String.valueOf(position));
                                    categoriesByStore.child(id).removeValue();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "Item has been removed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }
        });
        builder.show();
    }
    public void addCategory(){
        final EditText taskEditText = new EditText(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.setMargins(10,10,10,10);
        taskEditText.setLayoutParams(params);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("New category")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(taskEditText.getText().toString().trim().equals("")){
                            taskEditText.setError("Invalid name");
                        }else{
                            final String id = categoriesByStore.push().getKey();
                            final Category category = new Category(id, taskEditText.getEditableText().toString(),0, mId);
                            categoriesByStore.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("id nè", id);
                                    categoriesByStore.child(id).setValue(category);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }
    public void editNameDialog(final int position){
        final EditText taskEditText = new EditText(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.setMargins(10,10,10,10);
        taskEditText.setLayoutParams(params);
        taskEditText.setText(mList.get(position).getName());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("New category's name")
                .setView(taskEditText)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(taskEditText.getText().toString().trim().equals("")){
                            taskEditText.setError("Invalid name");
                            Toast.makeText(getContext(), "Invalid name.", Toast.LENGTH_SHORT).show();
                        }else{
                            mList.get(position).setName(taskEditText.getEditableText().toString());
                            categoriesByStore.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!= null){
                                        for(DataSnapshot data: dataSnapshot.getChildren()){
                                            Log.d("key",String.valueOf(data.getKey()));
                                            Log.d("key value", String.valueOf(data.getValue()));
                                            if(data.getKey().equals(mList.get(position).getId())){
                                                String id = String.valueOf(data.getKey());
                                                Category item = mList.get(position);
                                                categoriesByStore.child(id).setValue(item);
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            categoriesByStore.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mList.clear();
                    Log.d("datasnape", dataSnapshot.toString());
                    if(!dataSnapshot.exists()){
                        publishProgress();
                    }else{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Category category = data.getValue(Category.class);
                            mList.add(category);
                            adapter.notifyDataSetChanged();
                            publishProgress();
                        }
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
        }
    }
    private void showProgress (){
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }

}
