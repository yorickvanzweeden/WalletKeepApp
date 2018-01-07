package com.walletkeep.walletkeep.api;

import com.google.gson.JsonParser;

public class ErrorParser {
    private String key;
    private StringModifier stringModifier;


    public ErrorParser(String key) {
        this.key = key;
        this.stringModifier = null;
    }

    public ErrorParser(String key, StringModifier stringModifier) {
        this.key = key;
        this.stringModifier = stringModifier;
    }

    String parse(Throwable t) {
        return parse(t.getMessage());
    }

    String parse(String m){
        if (key == null) return m;
        if (stringModifier != null) m = stringModifier.modify(m);
        return new JsonParser().parse(m).getAsJsonObject().get(key).getAsString();
    }

    static ErrorParser getStandard() {
        return new ErrorParser(null);
    }

    public interface StringModifier {
        String modify(String s);
    }
}
