import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.Candlestick;
import configconstants.APIConstants;
import database_impl.ConnectDB;
import database_impl.Create_Table;

import java.sql.*;

import java.util.*;

public class BinanceServiceInterface {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {


        String ticker = "ALPHAUSDT";
        CandlestickInterval klineInterval = CandlestickInterval.FIFTEEN_MINUTES;
        int candlesLimit = 10;
        String table_name = ticker + klineInterval;

        ConnectDB konnectDB = new ConnectDB();
        Connection connection = konnectDB.getDBConnection();
        Create_Table ct = new Create_Table();
        ct.createTable(table_name);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.setFetchSize(0);



        RequestOptions options = new RequestOptions();
        options.setUrl("https://testnet.binancefuture.com/");
        SyncRequestClient syncRC = SyncRequestClient.create(APIConstants.API_KEY, APIConstants.API_SECRET_KEY, options);

//        parseToDatabase(syncRC, ticker, klineInterval, candlesLimit, connection);
    }

    public static void parseToDatabase (SyncRequestClient syncRC,
                                 String ticker,
                                 CandlestickInterval klineInterval,
                                 int candlesLimit, Connection connection) throws SQLException {

        List<Candlestick> klines = syncRC.getCandlestick(ticker, klineInterval,null, null, candlesLimit);
        boolean hasIncreasedCounter = false; // along start time, along1 end time of range where grab candles
        String[] assignersArray = {"high", "low", "open", "close", "quote_volume", "base_volume"};

        //todo: tablename, implement DYNAMIC ON CONFLICT (time) instead of assignersArray pre-set

        List<String> list = new ArrayList<>();

        // todo: rewrite as stream. Learn Stream API!
        for (String column : assignersArray) {
            list.add(String.format("%s = excluded.%s", column, column));
        }
//        String valuesPartOfQuery = String.join(", ", tut budet kol-vo znakov "?" po dline assigners array + time
        String assignersPartOfQuery = String.join(", ", list);
        String prepStatement = String.format("INSERT INTO binance_klines VALUES(?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (time) DO UPDATE SET %s", assignersPartOfQuery);

        PreparedStatement pstm = connection.prepareStatement(prepStatement);

        for (Candlestick kline : klines) {

            // todo: rewrite using Spring JPA to avoid the garbled ? ? ? mess
            //  also check SQLBuilder
            //   also set the order, TIMESTAMP MUST BE THE FIRST COLUMN IN TABLE

            pstm.setBigDecimal(8, kline.getOpen());
            pstm.setBigDecimal(1, kline.getHigh());
            pstm.setBigDecimal(7, kline.getClose());
            pstm.setBigDecimal(2, kline.getLow());
            pstm.setBigDecimal(3, kline.getQuoteAssetVolume());
            pstm.setBigDecimal(4, kline.getTakerBuyBaseAssetVolume());
            pstm.setInt(5, kline.getNumTrades());
            pstm.setTimestamp(6, new Timestamp(kline.getOpenTime()));

            pstm.addBatch();
            hasIncreasedCounter = true;
        }

        if (hasIncreasedCounter) {
            pstm.executeBatch();
            connection.commit();
            System.out.println("Mashallah");
        }
    }
}


