package com.threesome.shopme.AT.singleton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.AT.utility.Constant;

/**
 * Created by Kunka on 12/21/2017.
 */

public class FirebaseDB {
    public static FirebaseDB instance = null;
    public DatabaseReference mData;

    public FirebaseDB() {
        mData = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseDB getInstance() {
        if (instance == null){
            instance = new FirebaseDB();
        }
        return instance;
    }
    public Store getStoreDetail (String idStore){
        final Store[] store = {new Store()};
        mData.child(Constant.STORE).child(idStore).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    store[0] = dataSnapshot.getValue(Store.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return store[0];
    }
}
