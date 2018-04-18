
import com.forex.jExpertAdvisor.candles.Candle;
import com.forex.jExpertAdvisor.main.MarketMgr;
import com.forex.jExpertAdvisor.stoplosses.StopLoss;
import com.forex.jExpertAdvisor.trades.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class MacdOscilator extends IStrategy {




    public void OnDenit() {
        // TODO Auto-generated method stub

    }

    public void OnInit() {


    }

    private BigDecimal getAvg(int duration) {
        BigDecimal result = new BigDecimal(0);
        List<Candle> candles = MarketMgr.getInstance(this.getSymbol()).getHistoricView().subList(MarketMgr.getInstance(this.getSymbol()).getHistoricView().indexOf(MarketMgr.getInstance(this.getSymbol()).getCurrentCandle())-(duration+1),MarketMgr.getInstance(this.getSymbol()).getHistoricView().indexOf(MarketMgr.getInstance(this.getSymbol()).getCurrentCandle())-1 );
        for (Candle candle : candles) {
            result = result.add(candle.getClose());
        }
        return result.divide(new BigDecimal(duration));

    }

    public boolean isThisStrategyTradeType(TradeType tradeType){
        for(Trade trade: ExistingTrades.getInstance()){
            if (trade.getStrategy().equals(this)&&trade.getType().equals(tradeType))
                return true;
        }
        return false;
    }

    public void OnStart() {

        if(getAvg(12).subtract(getAvg(26)).compareTo(getAvg(9))>0 && !isThisStrategyTradeType(TradeType.BUY)) {
            for (int i = ExistingTrades.getInstance().size()-1; i>=0; i--) {
                Trade v = ExistingTrades.getInstance().get(i);
                if (v.getType().equals(TradeType.SELL) && v.getStrategy().equals(this))
                    try {
                        TradeMgr.getInstance().close(v);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
            TradeMgr.getInstance().open(this, new StopLoss(MarketMgr.getInstance(this.getSymbol()).getAsk().subtract(new BigDecimal(0.1))), TradeType.BUY, this.getSymbol(), this.getSize(), this.getAccount());
        }

        if(getAvg(12).subtract(getAvg(26)).compareTo(getAvg(9))<0 && !isThisStrategyTradeType(TradeType.SELL)) {

            for (int i = ExistingTrades.getInstance().size()-1; i>=0; i--) {
                Trade v = ExistingTrades.getInstance().get(i);
                if (v.getType().equals(TradeType.BUY) && v.getStrategy().equals(this))
                    try {
                        TradeMgr.getInstance().close(v);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
            TradeMgr.getInstance().open(this, new StopLoss(MarketMgr.getInstance(this.getSymbol()).getAsk().subtract(new BigDecimal(-0.1))), TradeType.SELL, this.getSymbol(), this.getSize(), this.getAccount());
        }


    }

}

