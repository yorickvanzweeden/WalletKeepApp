package com.walletkeep.walletkeep.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walletkeep.walletkeep.R;

public class TopBarPortfolioFragment extends Fragment {

    public TopBarPortfolioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.top_bar_portfolio_fragment, container, false);
    }

}
