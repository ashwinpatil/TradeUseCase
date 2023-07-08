package com.ashwin.trade.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/* 
 * This is the main class which  will take request to add/validate trade .
 * I have Kept it simple Java class which will receive Trade add it in the ConcurrrentSkipListMap() which is thread-safe and 
 * synchronized considering we will get Concurrent request. 
 * 
 *  This could be a Service based on requirement to Validate the Trade 
 *  by fetching the trades from any RDBMS or Mongodb ..
 *  This Service can either be  a REST API Or can listen to  a topic on Kafka or 
 *  be a Listener over MQ */

public class TradeStore {
    private Map<String, Trade> trades;
    public static final String MATURITY_MESSAGE = "Maturity Date is not valid";
    public static final String LOWERVERSION_MESSAGE = "Incorrect version of trade received";

    public TradeStore() {
        trades = new ConcurrentSkipListMap<>();
    }

    public void addTrade(Trade trade) throws InvalidTradeException {
        String tradeId = trade.getTradeId();
        int version = trade.getVersion();

        if (trades.containsKey(tradeId)) {
            Trade existingTrade = trades.get(tradeId);
            if (version < existingTrade.getVersion()) {
                throw new InvalidTradeException(LOWERVERSION_MESSAGE);
            } else if (version == existingTrade.getVersion()) {
                trades.put(tradeId, trade);
            }
            else if (validateMaturityDate(trade))
            {
   
            	trades.put(tradeId, trade);
            }
        }
        
    }
/**
 * 
 * @return
 */
    public  List<Trade> getTradesWithLessMaturityDate() {
        List<Trade> result = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        Trade trade ;

        Iterator<Map.Entry<String , Trade>> iterator = trades.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<String , Trade> entry  =iterator.next();
            trade = entry.getValue();
               if (trade.getMaturityDate().isBefore(currentDate)) {
                result.add(trade);
            }
        }

        return result;
    }
  
    /**
     * This method validate the maturity date of  incoming trade 
     * @param trade
     * @return
     * @throws InvalidTradeException
     */
	private boolean validateMaturityDate(Trade trade) throws InvalidTradeException {
        LocalDate maturityDate = trade.getMaturityDate();
        LocalDate today = LocalDate.now();

        if (maturityDate.isBefore(today)) {
            throw new InvalidTradeException(MATURITY_MESSAGE);
        }
        else 
        	return true;
    }

  
    
/** This can be a Service with RDBMS to store the trades */

    public static void main(String[] args) {
        TradeStore tradeStore = new TradeStore();

        try {
            Trade trade1 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2020, 5, 20));
            Trade trade2 = new Trade("T2", 2, "CP-2", "B1", LocalDate.of(2021, 5, 20));
            Trade trade3 = new Trade("T2", 1, "CP-1", "B1", LocalDate.of(2021, 5, 20));
            Trade trade4 = new Trade("T3", 3, "CP-3", "B2", LocalDate.of(2014, 5, 20));

            tradeStore.addTrade(trade1);
            tradeStore.addTrade(trade2);
            tradeStore.addTrade(trade3);
            tradeStore.addTrade(trade4);
        } catch (InvalidTradeException e) {
            System.out.println("Invalid trade: " + e.getMessage());
        }
    }
}
