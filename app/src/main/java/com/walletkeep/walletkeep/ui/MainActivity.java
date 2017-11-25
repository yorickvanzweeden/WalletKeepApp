package com.walletkeep.walletkeep.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.walletkeep.walletkeep.*;
import com.walletkeep.walletkeep.R;
import com.walletkeep.walletkeep.ui.portfolio.PortfolioActivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.walletkeep.walletkeep.R.layout.activity_main);

        checkFirstRun();

        // Call getData() on button click
        final Button button = findViewById(R.id.button_getData);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { getData();}
        });

        // Call saveCredentials() on button click
        final Button button2 = findViewById(R.id.button_saveCredentials);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { saveCredentials();}
        });

        final Button button3 = findViewById(R.id.button_portfolios);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(intent);
            }
        });


        readCredentials();

    }

    private void checkFirstRun() {
        Intent intent = new Intent(this, IntroSlider.class);
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = com.walletkeep.walletkeep.BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
            startActivity(intent);


        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void requestWithSomeHttpHeaders(final Credentials credentials) {
        final TextView textView_results = findViewById(R.id.textView_results);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.gdax.com/accounts";
        final StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        textView_results.setText(response);
                        textView_results.setMovementMethod(new ScrollingMovementMethod());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "No response data given";
                        String statusCode = Integer.toString(error.networkResponse.statusCode);
                        try{
                            errorMessage = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                        } catch (Exception e) {}
                        textView_results.setText("Error detected:\n\n" + errorMessage + "\nStatus code: " + statusCode);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("CB-ACCESS-KEY", credentials.key);
                params.put("CB-ACCESS-SIGN", credentials.signature);
                params.put("CB-ACCESS-TIMESTAMP", Long.toString(credentials.timestamp));
                params.put("CB-ACCESS-PASSPHRASE", credentials.passphrase);

                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
            @Override
            protected Response < String > parseNetworkResponse(NetworkResponse response) {

                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));

            }
        };
        queue.add(postRequest);
    }

    private void getData(){
        // Get credentials from field
        String key = ((EditText)findViewById(R.id.editText_key)).getText().toString();
        String passphrase = ((EditText)findViewById(R.id.editText_passphrase)).getText().toString();
        String secret = ((EditText)findViewById(R.id.editText_secret)).getText().toString();

        Toast toast = Toast.makeText(getApplicationContext(), "Getting data", Toast.LENGTH_SHORT);
        toast.show();

        final Credentials credentials = new Credentials(key,passphrase, secret);
        credentials.getSignature();
        requestWithSomeHttpHeaders(credentials);
    }

    private void saveCredentials() {
        Toast toast = Toast.makeText(getApplicationContext(), "Saving credentials", Toast.LENGTH_SHORT);
        toast.show();

        // Get credentials from field
        String key = ((EditText)findViewById(R.id.editText_key)).getText().toString();
        String secret = ((EditText)findViewById(R.id.editText_secret)).getText().toString();
        String passphrase = ((EditText)findViewById(R.id.editText_passphrase)).getText().toString();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_key), key);
        editor.putString(getString(R.string.preference_secret), secret);
        editor.putString(getString(R.string.preference_passphrase), passphrase);
        editor.commit();
    }

    private void readCredentials() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        String key = sharedPref.getString(getString(R.string.preference_key), "");
        String secret = sharedPref.getString(getString(R.string.preference_secret), "");
        String passphrase = sharedPref.getString(getString(R.string.preference_passphrase), "");
        ((EditText)findViewById(R.id.editText_key)).setText(key);
        ((EditText)findViewById(R.id.editText_secret)).setText(secret);
        ((EditText)findViewById(R.id.editText_passphrase)).setText(passphrase);
    }
}
