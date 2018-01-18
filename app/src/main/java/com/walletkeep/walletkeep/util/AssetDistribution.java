package com.walletkeep.walletkeep.util;


import android.graphics.Rect;

import com.walletkeep.walletkeep.db.entity.AggregatedAsset;

import java.util.ArrayList;
import java.util.List;

public class AssetDistribution {
    public List<DistributedElement> elements;

    public AssetDistribution(List<AggregatedAsset> assets, int width, int height) {
        // Calculate total value
        float sum = 0;
        for (AggregatedAsset asset: assets) sum += asset.getValueEur().floatValue();

        // Order on size
        //Collections.sort(assets, new AggregatedAsset.AssetComparator());

        // Calculate individual proportions
        int x = 0;
        elements = new ArrayList<>();

        for (AggregatedAsset asset: assets) {
            float portion = (asset.getValueEur().floatValue() / sum);
            int perc = (int)(portion * 100);
            int newX = x + (int)(portion * width);
            Rect rect = new Rect(x, 0, newX, height);
            x = newX;
            elements.add(new DistributedElement(asset.currencyTicker, perc, rect));
        }
    }


    public class DistributedElement {
        private String ticker;
        private Rect bounds;
        private int perc;

        DistributedElement(String ticker, int perc, Rect bounds) {
            this.ticker = ticker;
            this.perc = perc;
            this.bounds = bounds;
        }

        public String getTicker() { return this.ticker; }
        public Rect getBounds() { return this.bounds; }
        public int getPercentage() { return this.perc; }
    }
}
