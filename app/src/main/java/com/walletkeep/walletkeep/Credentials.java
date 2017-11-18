package com.walletkeep.walletkeep;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Credentials {
    public String key = "";
    public String passphrase = "";
    private String secret = "";
    public String signature = "";
    public long timestamp = 0;

    //constructor
    public Credentials(String key, String passphrase, String secret) {
        this.key = key;
        this.passphrase = passphrase;
        this.secret = secret;
    }

    public void getSignature(){
        this.timestamp = System.currentTimeMillis() / 1000 + 3;
        String data = timestamp + "GET/accounts";
        try {
            this.signature = generateHmacSHA256Signature(data, this.secret);
        } catch (Exception e)
        {
            Log.d("ERROR","error => "+e.toString());
        }
    }

    private static String generateHmacSHA256Signature(String data, String secret)   throws GeneralSecurityException {
        try {
            byte[] decoded_key = Base64.decode(secret, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(decoded_key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.encodeToString(hmacData, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
            throw new GeneralSecurityException(e);
        }
    }
}