package com.walletkeep.walletkeep.ui.wallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.viewmodel.TokenViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {
    // Data of the recycler view
    private String[] tokens;

    // Wallet data
    private HashMap<String, WalletToken> walletTokens = new HashMap<>();
    private HashMap<String, Boolean> changeRecord = new HashMap<>();
    private int walletId;

    // Access to the view
    private Context context;
    private TokenViewModel viewModel;

    /**
     * Provide a reference to the views for each data item
     * Each data/list item gets access to its own elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI elements to use of the list item layout
        public TextView mTextView;
        public ToggleButton mToggleButton;

        public ViewHolder(View v) {
            super(v);
            // Initialise UI elements
            mTextView = v.findViewById(R.id.editwallet_naked_token_tickertext);
            mToggleButton = v.findViewById(R.id.editwallet_naked_token_togglebutton);
        }
    }

    /**
     * Constructor: Sets context
     * @param context Allows for referencing of UI elements
     */
    public TokenAdapter(Context context, TokenViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    /**
     * Update data of the list
     * @param tokens List of supported tokens
     */
    public void updateTokens(String[] tokens){
        this.tokens = tokens;
        notifyDataSetChanged();
    }
    public void setWalletId(int walletId){
        this.walletId = walletId;
    }

    public List<WalletToken> getWalletTokens(Boolean add) {
        List<WalletToken> list = new ArrayList<>();
        if (changeRecord.isEmpty()) return list;
        for (Map.Entry<String, Boolean> entry: changeRecord.entrySet()) {
            if (entry.getValue() == add) list.add(walletTokens.get(entry.getKey()));
        }
        return list;
    }



    public void setWalletTokens(List<WalletToken> walletTokenList) {
        for(WalletToken token: walletTokenList) {
            walletTokens.put(token.getCurrencyTicker(), token);
        }
        notifyDataSetChanged();
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
        holder.mTextView.setText(tokens[position]);
        holder.mToggleButton.setChecked(walletTokens.containsKey(tokens[position]));

        holder.mToggleButton.setOnClickListener((view) -> {
            String currency = tokens[position];
            if (!walletTokens.containsKey(currency)) {
                walletTokens.put(currency, new WalletToken(walletId, currency));
            }

            // Mark change, if already exists, undo change
            if (changeRecord.containsKey(currency)) changeRecord.remove(currency);
            else changeRecord.put(currency, holder.mToggleButton.isChecked());
        });

    }

    /**
     * Return item count of the data set
     * @return Item count of the data set
     */
    @Override
    public int getItemCount() {
        if (tokens == null) return 0;
        return tokens.length;
    }
}
