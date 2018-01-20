package com.walletkeep.walletkeep.util;

import java.util.ArrayList;
import java.util.List;

public class SynchronisedTicket<T> {
    private final Object lock = new Object();

    private int ticketCount;
    private int ticket = 0;
    private boolean resetAtLast = true;
    private List<T> list = new ArrayList<>();

    /**
     * Constructor: Set the amount of tickets to be distributed
     * @param ticketCount Amount of tickets
     */
    public SynchronisedTicket(int ticketCount, boolean resetAtLast) {
        resetTicketCount(ticketCount);
        setResetAtLast(resetAtLast);
    }
    public SynchronisedTicket(int ticketCount) {
        resetTicketCount(ticketCount);
    }

    // Reset synchronisedTicker
    private void resetTicketCount(int ticketCount) {
        synchronized (lock) {
            this.ticketCount = ticketCount;
            this.ticket = 0;
        }
    }
    // Set if the ticketing should reset to 0 at resetAtLast
    private void setResetAtLast(boolean resetAtLast) {
        synchronized (lock) {
            this.resetAtLast = resetAtLast;
        }
    }

    public boolean isFirst() {
        synchronized (lock) {
            boolean result = ticket == 0;
            ticket++;

            // Ticketing round is over
            if (resetAtLast && ticket == ticketCount) ticket = 0;
            return result;
        }
    }
    public boolean isLast() {
        synchronized (lock) {
            ticket++;
            boolean result = ticket == ticketCount;

            // Ticketing round is over
            if (resetAtLast && ticket == ticketCount) ticket = 0;
            return result;
        }
    }

    public void add(List<T> list) {
        try{ this.list.addAll(list); }
        catch (Exception ignored) {}
    }
    public <T> List<T> get() {
        try { return (ArrayList<T>) list; }
        catch (Exception e) { return null; }
    }
}
