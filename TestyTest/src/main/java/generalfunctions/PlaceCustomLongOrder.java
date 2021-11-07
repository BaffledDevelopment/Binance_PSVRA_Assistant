package generalfunctions;

import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SubscriptionOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.Order;
import com.binance.client.model.user.UserDataUpdateEvent;
import configconstants.APIConstants;
import database_impl.ConnectDB;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

public class PlaceCustomLongOrder {

    public static void main(String[] args) throws SQLException {

        ConnectDB konnectDB = new ConnectDB();
        Connection connection = konnectDB.getDBConnection();
        Statement statement = connection.createStatement();


        RequestOptions options = new RequestOptions();
        options.setUrl("https://fapi.binance.com/");

        SyncRequestClient syncRC = SyncRequestClient.create(APIConstants.API_KEY, APIConstants.API_SECRET_KEY, options);
        String listenKey = syncRC.startUserDataStream();

        SubscriptionOptions optionsWebSock = new SubscriptionOptions();
        optionsWebSock.setUri("wss://fstream.binance.com");

        SubscriptionClient clientWS = SubscriptionClient.create(optionsWebSock);

        // Keep user data stream
        syncRC.keepUserDataStream(listenKey);



//        Order RCresponse = syncRC.postOrder("MATICUSDT", OrderSide.BUY, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
//                "3", "2.0659", "false", null, null, null, NewOrderRespType.RESULT);

        clientWS.subscribeUserDataEvent(listenKey, responseWS -> {

            String eventType = responseWS.getEventType();

            System.out.println(responseWS);

            if (Objects.equals(eventType, "ORDER_TRADE_UPDATE")) {

                String orderStatus = responseWS.getOrderUpdate().getOrderStatus();
                Boolean isReduceOnly = responseWS.getOrderUpdate().getIsReduceOnly();


                if (Objects.equals(orderStatus, "NEW") && Objects.equals(isReduceOnly, false)) {

                    // совершенно новый ордер not a takeprofit\sl, вносим в базу данных

                    String prepStatement = "INSERT INTO futures_orders (time, ticker, order_status, order_id, amount) VALUES(?, ?, ?, ?, ?)";

                    processToDataBase(responseWS, connection, prepStatement);


            }  else if (Objects.equals(orderStatus, "FILLED") && Objects.equals(isReduceOnly, false)) {

                    Long orderID = responseWS.getOrderUpdate().getOrderId();
                // это означает выполнение выставленного МНОЮ ордера (редус фалс), можно даблчек по ордерид

                    String prepStatement = String.format("UPDATE futures_orders SET order_status = 'FILLED' WHERE order_id = %s", orderID);

                    try {
                        PreparedStatement pstm = connection.prepareStatement(prepStatement);
                        pstm.execute();

                        System.out.println("Mashallah");


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    createTakeProfitOrders(responseWS, connection, syncRC);

                    // записать об изменении ордера по ордер ид

                    // начать создавать тейкпрофиты

                }

            } else if (Objects.equals(responseWS.getEventType(), "ORDER_TRADE_UPDATE")
                    && Objects.equals(responseWS.getOrderUpdate().getOrderStatus(), "FILLED")) {

                    createTakeProfitOrders(responseWS, connection, syncRC);

            } else {
                System.out.println("NE ORDER UPDATE");
                System.out.println(responseWS);
                System.out.println("END OF NE ORDER RESPONSE");
            }

        }, System.out::println);

        System.out.println();


    }

    public static void createTakeProfitOrders(UserDataUpdateEvent responseWS, Connection connection, SyncRequestClient syncRC) {


        BigDecimal origQuantity = responseWS.getOrderUpdate().getOrigQty();
        BigDecimal remainingQuantity = responseWS.getOrderUpdate().getCumulativeFilledQty();
        String ticker = responseWS.getOrderUpdate().getSymbol();
//        float marginAmount = 15;
//        BigDecimal startingPrice = new BigDecimal(1000);
        BigDecimal purchasePrice = responseWS.getOrderUpdate().getPrice();

        List<BigDecimal> takeProfitLevels = new ArrayList<>();
        takeProfitLevels.add(new BigDecimal("1.01"));
        takeProfitLevels.add(new BigDecimal("1.05"));
        takeProfitLevels.add(new BigDecimal("1.1"));
        takeProfitLevels.add(new BigDecimal("1.20"));

        double[] amountToSellInPercentagesTP = {0.1, 0.25, 0.5, 0.2};


        int counter = 0;

        String reduceOnlyTrue = "true";

        for (BigDecimal takeprofit : takeProfitLevels) {

            BigDecimal coinsToSell = origQuantity.multiply(BigDecimal.valueOf(amountToSellInPercentagesTP[counter]));
            coinsToSell = coinsToSell.setScale(0, RoundingMode.FLOOR);
            BigDecimal takeProfitPrice = purchasePrice.multiply(takeprofit).setScale(2, RoundingMode.HALF_UP);


            if (!(counter == 3)) {

                remainingQuantity = remainingQuantity.subtract(coinsToSell);

                syncRC.postOrder(ticker, OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
                        coinsToSell.toString(), takeProfitPrice.toString(), reduceOnlyTrue, null, null, null, NewOrderRespType.RESULT);

                counter+=1;

            } else {

                syncRC.postOrder(ticker, OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
                        remainingQuantity.toString(), takeProfitPrice.toString(), reduceOnlyTrue, null, null, null, NewOrderRespType.RESULT);

            }
                System.out.println("SOVSEM OSTALOS" + remainingQuantity);
            }
        }

    public static void processToDataBase(UserDataUpdateEvent responseWS, Connection connection, String prepStatement) {

        Long orderID = responseWS.getOrderUpdate().getOrderId();
        String orderStatus = responseWS.getOrderUpdate().getOrderStatus();
        Boolean isReduceOnly = responseWS.getOrderUpdate().getIsReduceOnly();
        String symbol = responseWS.getOrderUpdate().getSymbol();
        BigDecimal quantity = responseWS.getOrderUpdate().getOrigQty();
        Long timestamp = responseWS.getOrderUpdate().getOrderTradeTime();
        String isFilled = "f";
        String isCancelled = "f";
        if (Objects.equals(orderStatus, "FILLED")) {
            isFilled = "t";
        }
        if (Objects.equals(orderStatus, "CANCELLED")) {
            isCancelled = "t";
        }

//        String prepStatement = "INSERT INTO futures_orders (time, ticker, order_status, order_id, amount) VALUES(?, ?, ?, ?, ?)";


        try {
            PreparedStatement pstm = connection.prepareStatement(prepStatement);

            pstm.setTimestamp(1, new Timestamp(timestamp));
            pstm.setString(2, symbol);
            pstm.setString(3, orderStatus);
            pstm.setLong(4, orderID);
//                    pstm.setString(5, isFilled);  // че за хуйня не вставляются булины блин(((
//                    pstm.setString(6, isCancelled);
            pstm.setBigDecimal(5, quantity);

            pstm.execute();

//            pstm.addBatch();
//            pstm.executeBatch();
            System.out.println("Mashallah");
//                    connection.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }




    }



}


//        options.setUrl("https://testnet.binancefuture.com/");
//        optionsWebSock.setUri("wss://stream.binancefuture.com/ws");