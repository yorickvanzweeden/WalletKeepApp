package com.walletkeep.walletkeep.util;

public class SynchronisedTicket {
    private static final Object lock = new Object();

    private static int ticketCount;
    private static int ticket = 0;
    private static boolean resetAtLast = true;

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
    static void resetTicketCount(int ticketCount) {
        synchronized (lock) {
            SynchronisedTicket.ticketCount = ticketCount;
        }
    }
    // Set if the ticketing should reset to 0 at resetAtLast
    static void setResetAtLast(boolean resetAtLast) {
        synchronized (lock) {
            SynchronisedTicket.resetAtLast = resetAtLast;
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
}
