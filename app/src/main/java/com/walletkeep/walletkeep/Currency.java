package com.walletkeep.walletkeep;

class Currency {
    public String Name;
    public String Ticker;
    public float Price;

    /**
     * Constructor: Creates a currency
     * @param name Name of the currency
     * @param ticker Ticker of the currency
     * @param price Price of the currency
     */
    public Currency(String name, String ticker, float price) {
        this.Name = name;
        this.Ticker = ticker;
        this.Price = price;
    }
}
