package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("wallet_id")},
        foreignKeys = @ForeignKey(entity = Wallet.class,
        parentColumns = "id",
        childColumns = "wallet_id"))
public class ExchangeCredentials {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "wallet_id")
    private int wallet_id;

    @ColumnInfo(name = "key")
    private String key;

    @ColumnInfo(name = "secret")
    private String secret;

    @ColumnInfo(name = "passphrase")
    private String passphrase;

    @ColumnInfo(name = "nonce")
    private int nonce;

    // Alternative constructor to empty constructor
    public ExchangeCredentials(String key, String secret, String passphrase){
        this.key = key;
        this.secret = secret;
        this.passphrase = passphrase;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getSecret() { return secret; }

    public void setSecret(String secret) { this.secret = secret; }

    public String getPassphrase() { return passphrase; }

    public void setPassphrase(String passphrase) { this.passphrase = passphrase; }

    public int getNonce() { return nonce; }

    public void setNonce(int nonce) { this.nonce = nonce; }

    public int getWallet_id() { return wallet_id; }

    public void setWallet_id(int wallet_id) { this.wallet_id = wallet_id; }
}