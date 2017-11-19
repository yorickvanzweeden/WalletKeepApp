package com.walletkeep.walletkeep;

import java.util.HashMap;

abstract class Exchange {
    public String Name;

    public abstract void getData();
    public abstract void setCredentials();
}
