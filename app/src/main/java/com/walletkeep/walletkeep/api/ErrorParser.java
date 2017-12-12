package com.walletkeep.walletkeep.api;

import com.google.gson.JsonParser;

public class ErrorParser {
    private String key;
    public ErrorParser(String key) {
        this.key = key;
    }

    public String parse(Throwable t) {
        return parse(t.getMessage());
    }

    public String parse(String m){
        if (key == null) return m;
        return new JsonParser().parse(m).getAsJsonObject().get(key).getAsString();
    }

    public static ErrorParser getStandard(){
        return new ErrorParser(null);
    }
}
