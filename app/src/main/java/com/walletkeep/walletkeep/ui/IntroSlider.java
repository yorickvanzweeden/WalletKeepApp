package com.walletkeep.walletkeep.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.walletkeep.walletkeep.R;

public class IntroSlider extends AppCompatActivity {
    int clicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome_activity);

        setupIntroSlider();
    }

    /**
     * Setup IntroSlider
     */
    private void setupIntroSlider(){
        // Get references of the Next button and the ViewFlipper
        Button nextButton = findViewById(R.id.buttonnext);
        ViewFlipper simpleViewFlipper = findViewById(R.id.simpleViewFlipper); // get the reference of ViewFlipper

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // Set the animation type to ViewFlipper
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);

        // Switch view to the next view
        nextButton.setOnClickListener(v -> {
            simpleViewFlipper.showNext();
            clicks++;

            // Change action and text dependent on the view
            if (clicks > 2){
                // Kill activity to return to main activity
                finish();
            } else if (clicks > 1){
                nextButton.setText("GOT IT" );
            }
        });
    }
}
