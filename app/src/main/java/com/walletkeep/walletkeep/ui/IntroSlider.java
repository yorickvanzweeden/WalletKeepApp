package com.walletkeep.walletkeep.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.walletkeep.walletkeep.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroSlider extends AppCompatActivity {

    private ViewFlipper simpleViewFlipper;
    Button buttonnext;
    int clicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Intent intent = new Intent(this, MainActivity.class);
        clicks =0;

        setContentView(R.layout.activity_welcome);

        // get The references of Button and ViewFlipper
        buttonnext = (Button) findViewById(R.id.buttonnext);
        simpleViewFlipper = (ViewFlipper) findViewById(R.id.simpleViewFlipper); // get the reference of ViewFlipper
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewFlipper
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);

        // ClickListener for NEXT button
        // When clicked on Button ViewFlipper will switch between views
        // The current view will go out and next view will come in with specified animation
        buttonnext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                // show the next view of ViewFlipper
                simpleViewFlipper.showNext();
                clicks++;
                if(clicks > 1){
                    buttonnext.setText("GOT IT" );
                };
                if (clicks > 2){
                    startActivity(intent);
                };
            }
        });

    }

}
