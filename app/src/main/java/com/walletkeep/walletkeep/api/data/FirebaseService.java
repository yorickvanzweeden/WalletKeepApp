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
import java.util.List;

public class FirebaseService {
    private FirebaseDatabase mInstance;
    private UpdateListener mListener;

    public FirebaseService(UpdateListener listener) {
        mInstance = FirebaseDatabase.getInstance();
        mListener = listener;
        setupPriceListener();
        setupCurrencyListener();
    }

    private void setupPriceListener() {
        DatabaseReference ref = mInstance.getReference("prices");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { onDataChangePrices(dataSnapshot); }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("###", "Failed to read value.", error.toException());
                mListener.onError(error);
            }
        });
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

    private void onDataChangePrices(DataSnapshot dataSnapshot) {
        List<CurrencyPrice> prices = new ArrayList<>();
        for (DataSnapshot priceSnapshot: dataSnapshot.getChildren()) {
            Price price = priceSnapshot.getValue(Price.class);
            prices.add(price.getCurrencyPrice(priceSnapshot.getKey()));
        }
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        mListener.insertPrices(prices);
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
        void insertPrices(List<CurrencyPrice> prices);
        void insertCurrencies(List<Currency> currencies);
        void onError(DatabaseError error);
    }
}
