package com.walletkeep.walletkeep.ui.wallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {
    // Data of the recycler view
    private String[] tokens;

    private int walletId;

    private WalletViewModel viewModel;

    // Access to the view
    private Context context;

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
    public TokenAdapter(Context context, WalletViewModel viewModel, int walletId) {
        this.context = context;
        this.viewModel = viewModel;
        this.walletId = walletId;
    }

    /**
     * Update data of the list
     * @param Wallets containing assets, exchange credentials
     */
    public void updateTokens(String[] tokens){
        this.tokens = tokens;
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

        holder.mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String currency = holder.mToggleButton.getTextOff().toString();
                WalletToken token = new WalletToken(walletId, currency);

                if (isChecked) {
                    // The toggle is enabled
                    viewModel.insertToken(token);
                } else {
                    viewModel.deleteToken(token);
                }
            }
        });

        holder.mTextView.setText(tokens[position]);

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
