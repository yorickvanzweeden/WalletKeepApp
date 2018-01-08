package com.walletkeep.walletkeep.util;

import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeltaCalculation {

    /**
     * Calculates the difference in two lists of Assets
     *
     * Duplicate tickers in a list will be summed (i.e.: [(BTC,2),(BTC,3)] = [(BTC,5)]
     * If the old list contains ticker the new list doesn't, the delta will be negative
     * @param oldList Older version of assets
     * @param newList New version of assets
     * @return Difference in assets
     */
    public static ArrayList<Asset> get(List<Asset> oldList, @NonNull  List<Asset> newList) {
        // Convert lists to hashmaps (does not assume unique tickers)
        HashMap<String, Asset> oldHashMap;
        if (oldList != null) oldHashMap = new HashMap<>();
        else oldHashMap = convertToHashMap(oldList);
        HashMap<String, Asset> newHashMap = convertToHashMap(newList);

        // Check if they are equal (still faster than List.equals)
        if (oldHashMap.equals(newHashMap)) return null;

        //Define delta list
        return calculateDeltas(oldHashMap, newHashMap);
    }

    /**
     * Converts a list of assets to a HashMap(ticker, asset)
     * Duplicate tickers in a list will be summed (i.e.: [(BTC,2),(BTC,3)] = [(BTC,5)]
     * @param list List of assets
     * @return HashMap(ticker, asset)
     */
    private static HashMap<String, Asset> convertToHashMap(List<Asset> list){
        HashMap<String, Asset> hashMap = new HashMap<>();

        // Reuse to save on garbage collecting
        String ticker;
        Asset a;

        for (int i = 0; i < list.size(); i++) {
            a = list.get(i);
            ticker = a.getCurrencyTicker();

            // Overwrite entry with summed value
            if (hashMap.containsKey(ticker)) {
                Asset sum = new Asset(a.getWalletId(), ticker,
                        a.getAmount() + hashMap.get(ticker).getAmount());
                hashMap.put(ticker, sum);
            }
            // Normal insertion into hashMap
            else {
                hashMap.put(ticker, a);
            }
        }
        return hashMap;
    }

    /**
     * Calculates difference between two hashmaps
     * If the old list contains ticker the new list doesn't, the delta will be negative
     * @param oldHashMap Old version of the assets
     * @param newHashMap New version of the assets
     * @return Diffence in assets
     */
    private static ArrayList<Asset> calculateDeltas(HashMap<String, Asset> oldHashMap, HashMap<String, Asset> newHashMap) {
        ArrayList<Asset> delta = new ArrayList<>();

        // They are different --> Calculate deltas
        Iterator it = newHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Asset> pair = (Map.Entry)it.next();

            // Old and new contain the same --> Check if different amount
            Asset x = oldHashMap.get(pair.getKey());
            if (x != null) {
                //Remove from oldHashMap
                oldHashMap.remove(pair.getKey());

                // Values match --> No delta
                if (Float.floatToIntBits(pair.getValue().getAmount()) ==
                        Float.floatToIntBits(x.getAmount())) {
                    it.remove();
                    continue;
                }

                // Values don't match --> Calculate delta
                delta.add(new Asset(x.getWalletId(), x.getCurrencyTicker(),
                        pair.getValue().getAmount() - x.getAmount()));
            } else {
                delta.add(pair.getValue());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }

        // If oldHashMap has entries left, then these are unique to oldHashMap and should have negative deltas
        it = oldHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Asset> pair = (Map.Entry)it.next();
            Asset x = pair.getValue();
            x.setAmount(-1 * x.getAmount());
            delta.add(x);
            it.remove(); // avoids a ConcurrentModificationException
        }

        return delta;
    }

}
