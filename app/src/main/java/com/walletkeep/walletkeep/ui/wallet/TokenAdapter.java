package com.walletkeep.walletkeep.ui.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {
    // Data of the recycler view
    private String[] supportedTokens;

    // Wallet data
    private HashMap<String, WalletToken> walletTokens = new HashMap<>();
    private HashMap<String, Boolean> changeRecord = new HashMap<>();
    private int walletId;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        TextView tokenLabel;
        ToggleButton toggleButton;

        ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            tokenLabel = v.findViewById(R.id.editwallet_naked_token_tickertext);
            toggleButton = v.findViewById(R.id.editwallet_naked_token_togglebutton);
        }
    }

    /**
     * Constructor: Sets lists of supported supportedTokens
     * @param supportedTokens
     */
    TokenAdapter(String[] supportedTokens, int walletId) {
        this.supportedTokens = supportedTokens;
        this.walletId = walletId;
        notifyDataSetChanged();
    }

    void setWalletTokens(List<WalletToken> walletTokenList) {
        for(WalletToken token: walletTokenList)
            walletTokens.put(token.getCurrencyTicker(), token);
        notifyDataSetChanged();
    }

    List<WalletToken> getWalletTokens(Boolean add) {
        List<WalletToken> list = new ArrayList<>();
        if (changeRecord.isEmpty()) return list;
        for (Map.Entry<String, Boolean> entry: changeRecord.entrySet()) {
            if (entry.getValue() == add) list.add(walletTokens.get(entry.getKey()));
        }
        return list;
    }



    /**
     * Creates new view and specifies which layout to use
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public TokenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editwallet_naked_token_fragment, parent, false);

        return new com.walletkeep.walletkeep.ui.wallet.TokenAdapter.ViewHolder(v);
    }

    /**
     * Specifies the UI elements and actions per list item
     * @param holder View holder (list item) to use
     * @param position The index of the data item
     */
    @Override
    public void onBindViewHolder(TokenAdapter.ViewHolder holder, int position) {
        holder.tokenLabel.setText(supportedTokens[position]);
        holder.toggleButton.setChecked(walletTokens.containsKey(supportedTokens[position]));

        holder.toggleButton.setOnClickListener((view) -> {
            String currency = supportedTokens[position];
            if (!walletTokens.containsKey(currency)) {
                walletTokens.put(currency, new WalletToken(walletId, currency));
            }

            // Mark change, if already exists, undo change
            if (changeRecord.containsKey(currency)) changeRecord.remove(currency);
            else changeRecord.put(currency, holder.toggleButton.isChecked());
        });

    }

    /**
     * Return item count of the data set
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (supportedTokens == null) return 0;
        return supportedTokens.length;
    }
}
