package com.walletkeep.walletkeep.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.ui.portfolio.PortfolioActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.walletkeep.walletkeep.R.layout.activity_main);

        // Check if introslider should be shown
        checkFirstRun();

        // Call getData() on button click
        final Button button3 = findViewById(R.id.button_portfolios);
        button3.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, PortfolioActivity.class)));
    }

    /**
     * Check if Introslider should be shown
     */
    private void checkFirstRun() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean show_intro = sharedPref.getBoolean("show_intro", true);

        if (show_intro) {
            startActivity(new Intent(this, IntroSlider.class));
            sharedPref.edit().putBoolean("show_intro", false).apply();
        }
    }
}
