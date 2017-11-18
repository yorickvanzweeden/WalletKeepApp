package com.walletkeep.walletkeep;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Credentials credentials = new Credentials("","", "");

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Getting data!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                long timestamp = System.currentTimeMillis() / 1000;
                credentials.getSignature();

                requestWithSomeHttpHeaders(credentials);
            }
        });

    }

    private void requestWithSomeHttpHeaders(final Credentials credentials) {
        final TextView mTextView = (TextView) findViewById(R.id.textView);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.gdax.com/accounts";
        final StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        Log.d("ERROR","error => "+error.networkResponse.statusCode);
                        try{
                            Log.d("ERROR","error => "+ new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers)));
                        } catch (Exception e) {}

                        mTextView.setText("Whoops");
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
}
