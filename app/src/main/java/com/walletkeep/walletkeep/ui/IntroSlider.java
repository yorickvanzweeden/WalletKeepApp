package com.walletkeep.walletkeep.ui;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.walletkeep.walletkeep.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroSlider extends AppCompatActivity {
    int clicks = 0;
     @BindView(R.id.introSlider_activity_viewFlipper) ViewFlipper viewFlipper;
     @BindView(R.id.introSlider_activity_button_next) Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.introslider_activity);
        ButterKnife.bind(this);
        setupIntroSlider();
    }

    /**
     * Setup IntroSlider
     */
    private void setupIntroSlider() {
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // Set the animation type to ViewFlipper
        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);

        // Switch view to the next view
        buttonNext.setOnClickListener(v -> {
            viewFlipper.showNext();
            clicks++;

            // Change action and text dependent on the view
            if (clicks > 2) {
                // Kill activity to return to main activity
                finish();
            } else if (clicks > 1) {
                buttonNext.setText("GOT IT");
            }
        });
    }
}
