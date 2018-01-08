package com.walletkeep.walletkeep.api;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureGeneration {
    private ResponseHandler responseHandler;

    SignatureGeneration(ResponseHandler responseHandler){
        this.responseHandler = responseHandler;
    }


    public String encode(byte[] bytes){
        if (bytes == null) return null;

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;

        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] decode(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }

    public byte[] getBytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            responseHandler.returnError("UTF-8 not supported");
            return null;
        }
    }


    public byte[] hMac(byte[] data, byte[] decoded_key, String algorithm) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(decoded_key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            responseHandler.returnError("Creating signatures on this phone is not supported.");
        } catch (InvalidKeyException e) {
            responseHandler.returnError("Signature could not be created. Your secret is probably invalid.");
        }
        return null;
    }
}
