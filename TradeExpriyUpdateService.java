package com.ashwin.trade.main;
/**
 * This is a Service class where i have used  Timer object to schedule tasks.
   The task is scheduled to run every 5 minutes by calling timer.schedule(task, 0, 300000) to update the Expiry flag of trade.
   This could also be implemented using Cache and do operations on TradeStore Cache 
   
 */
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TradeExpriyUpdateService {
   	private static TradeStore tradeStore ;  
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
           
            public void run() {
                
                          updateTrades();
            }
        };

        timer.schedule(task, 0, 300000);
    }

    private static void updateTrades() {
    	
    	List<Trade> tradesWithLessMaturityDate;
    	tradesWithLessMaturityDate = tradeStore.getTradesWithLessMaturityDate();
         for (Trade trade : tradesWithLessMaturityDate) {
    	
                 trade.setExpired(true);
        }
    }
}

