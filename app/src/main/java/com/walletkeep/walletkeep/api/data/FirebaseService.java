package com.walletkeep.walletkeep.api.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseService {
    private FirebaseDatabase mInstance;
    private UpdateListener mListener;
    private ValueEventListener mValueEventListenerInstance;
    private HashMap<String, DatabaseReference> watchList;

    public FirebaseService(UpdateListener listener) {
        mInstance = FirebaseDatabase.getInstance();
        mListener = listener;
        watchList = new HashMap<>();
        setupCurrencyListener();
    }

    public void updateWatchList(List<String> currencies) {
        DatabaseReference ref = mInstance.getReference("prices");

        // Add new to watch list
        for (String currency : currencies) {
            if (!watchList.containsKey(currency)) {
                DatabaseReference childRef = ref.child(currency);
                watchList.put(currency, childRef);
                childRef.addValueEventListener(getPriceListener());
            }
        }

        // Remove old
        for (String old : watchList.keySet()) {
            if (!currencies.contains(old)) {
                watchList.get(old).removeEventListener(getPriceListener());
                watchList.remove(old);
            }
        }
    }

    private void setupCurrencyListener() {
        DatabaseReference ref = mInstance.getReference("currencies");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { onDataChangeCurrencies(dataSnapshot); }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("###", "Failed to read value.", error.toException());
                mListener.onError(error);
            }
        });
    }

    private ValueEventListener getPriceListener() {
        if (mValueEventListenerInstance == null) {
            mValueEventListenerInstance = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    Log.w("###", dataSnapshot.toString());
                    try {
                        Price price = dataSnapshot.getValue(Price.class);
                        mListener.insertPrice(price.getCurrencyPrice(dataSnapshot.getKey()));
                    } catch (Exception ignored) {}

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("###", "Failed to read value.", error.toException());
                    mListener.onError(error);
                }
            };
        }
        return mValueEventListenerInstance;
    }
    private void onDataChangeCurrencies(DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        List<Currency> currencies = new ArrayList<>();
        for (DataSnapshot currencySnapshot: dataSnapshot.getChildren()) {
            currencies.add(new Currency("", currencySnapshot.getKey()));
        }
        mListener.insertCurrencies(currencies);
    }

    public interface UpdateListener {
        void insertPrice(CurrencyPrice price);
        void insertCurrencies(List<Currency> currencies);
        void onError(DatabaseError error);
    }
}
