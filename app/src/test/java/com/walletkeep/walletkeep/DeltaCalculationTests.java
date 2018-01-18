package com.walletkeep.walletkeep;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.util.DeltaCalculation;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DeltaCalculationTests {
    int _id = 0;

    private ArrayList<Asset> generateCurrencies(int amount) {
        ArrayList< Asset> assetList = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < amount; i++) {
            String ticker = Integer.toString(r.nextInt(1300));
            assetList.add(new Asset(
                    1,
                    ticker,
                    new BigDecimal(r.nextInt(50))
            ));
        }

        return assetList;
    }

    @Test
    public void testPerformanceDeltaCalculation(){
        int amount = 1000000;
        ArrayList<Asset> oldList = generateCurrencies(amount);
        ArrayList<Asset> newList = generateCurrencies(amount);

        long startTime = System.nanoTime();
        DeltaCalculation.get(oldList, newList);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        long durationMilli = duration / 1000000;
        String output = "Testing the performance of Delta Calculation || Amount: %s | Time: %s ns / %s ms";
        System.out.println(String.format(output, amount, duration, durationMilli));
    }

    @Test
    public void testEqualOldListWithMultipleTickers(){
        ArrayList<Asset> oldList = listThreeTickers();
        ArrayList<Asset> newList = listTwoTickers();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas == null);
    }

    @Test
    public void testEqualNewListWithMultipleTickers(){
        ArrayList<Asset> oldList = listTwoTickers();
        ArrayList<Asset> newList = listThreeTickers();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas == null);
    }

    @Test
    public void testEqualLists(){
        ArrayList<Asset> oldList = listOneTicker();
        ArrayList<Asset> newList = listTwoTickers();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, oldList);
        Assert.assertTrue(deltas == null);
    }

    @Test
    public void testDifferentListsOldMore(){
        ArrayList<Asset> oldList = listTwoTickers();
        ArrayList<Asset> newList = listOneTicker();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas != null &&
                deltas.get(0).getCurrencyTicker().equals("ETH") &&
                deltas.get(0).getAmount().equals(new BigDecimal(-3)) &&
                deltas.size() == 1
        );
    }

    @Test
    public void testDifferentListsNewMore(){
        ArrayList<Asset> oldList = listOneTicker();
        ArrayList<Asset> newList = listTwoTickers();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas != null &&
                deltas.get(0).getCurrencyTicker().equals("ETH") &&
                deltas.get(0).getAmount().equals(new BigDecimal(3)) &&
                deltas.size() == 1
        );
    }

    @Test
    public void testDifferentListsOldZero(){
        ArrayList<Asset> oldList = new ArrayList<>();
        ArrayList<Asset> newList = listOneTicker();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas != null &&
                deltas.get(0).getCurrencyTicker().equals("BTC") &&
                deltas.get(0).getAmount().equals(new BigDecimal(3)) &&
                deltas.size() == 1
        );
    }

    @Test
    public void testDifferentListsNewZero(){
        ArrayList<Asset> oldList = listOneTicker();
        ArrayList<Asset> newList = new ArrayList<>();

        ArrayList<Asset> deltas = DeltaCalculation.get(oldList, newList);
        Assert.assertTrue(deltas != null &&
                deltas.get(0).getCurrencyTicker().equals("BTC") &&
                deltas.get(0).getAmount().equals(new BigDecimal(-3)) &&
                deltas.size() == 1
        );
    }


    private ArrayList<Asset> listOneTicker(){
        return new ArrayList<Asset>() {{
            add(new Asset(_id, "BTC", new BigDecimal(3)));
        }};
    }

    private ArrayList<Asset> listTwoTickers(){
        return new ArrayList<Asset>() {{
            add(new Asset(_id, "ETH", new BigDecimal(3)));
            add(new Asset(_id, "BTC", new BigDecimal(3)));
        }};
    }

    private ArrayList<Asset> listThreeTickers(){
        return new ArrayList<Asset>() {{
            add(new Asset(_id, "ETH", new BigDecimal(1)));
            add(new Asset(_id, "ETH", new BigDecimal(2)));
            add(new Asset(_id, "BTC", new BigDecimal(3)));
        }};
    }
}