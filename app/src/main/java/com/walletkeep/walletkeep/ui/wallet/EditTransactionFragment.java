package com.walletkeep.walletkeep.ui.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.util.Converters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditTransactionFragment extends Fragment implements EditWalletActivity.IWalletFragment {
    @BindView(R.id.editWallet_transaction_editText_currency) EditText editTextCurrency;
    @BindView(R.id.editWallet_transaction_editText_amount) EditText editTextAmount;
    Unbinder unbinder;

    private View view;
    private List<Asset> assets;
    private BigDecimal sum = BigDecimal.ZERO;

    public EditTransactionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editwallet_transaction_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = getView();
    }

    /**
     * Update form with saved wallet data
     *
     * @param wallet Wallet containing the data for the form
     */
    @Override
    public void updateForm(WalletWithRelations wallet) {
        this.assets = wallet.assets;

        editTextCurrency.setText(wallet.assets.get(wallet.assets.size() - 1).getCurrencyTicker());

        for (Asset asset : this.assets) {
            sum = sum.add(asset.getAmount());
        }
        editTextAmount.setText(sum.toString());
    }

    /**
     * Save form data to a wallet
     *
     * @param wallet Wallet to save data in
     * @return Updated wallet
     */
    @Override
    public WalletWithRelations updateWallet(WalletWithRelations wallet) {
        // Retrieve data from form
        String currency = editTextCurrency.getText().toString().trim().toUpperCase();
        String amountString = editTextAmount.getText().toString();
        // Check user input
        BigDecimal amount = Converters.userInputToBD(amountString);

        if (amount.compareTo(sum) == 0) {
            wallet.assets = null;
            return wallet;
        }

        // Assets have changed
        List<Asset> assets = new ArrayList<Asset>() {{
            add(new Asset(wallet.wallet.getId(), currency, amount));
        }};
        assets.get(0).setTimestamp(new Date());

        wallet.assets = assets;

        return wallet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
