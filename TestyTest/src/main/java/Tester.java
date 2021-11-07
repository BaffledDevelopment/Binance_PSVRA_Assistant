
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.client.*;
import com.binance.client.impl.WebSocketConnection;
import com.binance.client.impl.WebSocketStreamClientImpl;
import com.binance.client.model.enums.*;
import com.binance.client.model.user.UserDataUpdateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import configconstants.APIConstants;
import database_impl.ConnectDB;
import database_impl.Create_Table;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@ClientEndpoint
public class Tester {

    private static final Logger logger = LoggerFactory.getLogger(Tester.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    protected WebSocketContainer container;
    protected Session userSession = null;

    public Tester() {
        container = ContainerProvider.getWebSocketContainer();
    }

    public void Connect(String server) {
        try {
            userSession = container.connectToServer(this, new URI(server));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }


    }

//    BinanceApiWebSocketClient

    public void SendMessage(String sMsg) throws IOException {
        userSession.getBasicRemote().sendText(sMsg);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {

    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.println(msg);
    }

    public void disconnect() throws IOException {
        userSession.close();
    }


    private static final ObjectMapper mapper = new ObjectMapper();

//    private final ObjectReader objectReader;



    public static void main(String[] args) throws InterruptedException, IOException {



        BigDecimal origQuantity = new BigDecimal(15);
        BigDecimal remainingQuantity = new BigDecimal(15);
        float marginAmount = 15;
        BigDecimal startingPrice = new BigDecimal("1000.1234");
//        BigDecimal purchasePrice = responseWS.getOrderUpdate().getPrice();

        List<BigDecimal> takeProfitLevels = new ArrayList<>();
        takeProfitLevels.add(new BigDecimal("1.01"));
        takeProfitLevels.add(new BigDecimal("1.05"));
        takeProfitLevels.add(new BigDecimal("1.1"));
        takeProfitLevels.add(new BigDecimal("1.20"));

        double[] amountToSellInPercentagesTP = {0.1, 0.25, 0.5, 0.2};

        int counter = 0;

//        String ticker = responseWS.getOrderUpdate().getSymbol();
        String reduceOnlyTrue = "true";

        for (BigDecimal takeprofit : takeProfitLevels) {

            BigDecimal coinsToSell = origQuantity.multiply(BigDecimal.valueOf(amountToSellInPercentagesTP[counter]));
            coinsToSell = coinsToSell.setScale(0, RoundingMode.FLOOR);
            BigDecimal takeProfitPrice = startingPrice.multiply(takeprofit).setScale(2, RoundingMode.HALF_UP);

            System.out.println(takeProfitPrice);

            if (!(counter == 3)) {

                remainingQuantity = remainingQuantity.subtract(coinsToSell);
                System.out.println("OSTALOS" + remainingQuantity);



                counter+=1;


            } else {
                System.out.println("SOVSEM OSTALOS" + remainingQuantity);
            }



//            System.out.println("remaining quant" + remainingQuantity);
//
//
//
//            System.out.println(takeProfitPrice);



            //Minimum possible order is derived from two flters:
            // LOT_SIZE.min which is the minimum quantity
            // and MIN_NOTIONAL which is the minimum quantity*price

//                syncRC.postOrder(ticker, OrderSide.BUY, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
//                        "3", "2.0659", reduceOnlyTrue, null, null, null, NewOrderRespType.RESULT);

        }
//

//        String apiKeyTest = "Gh1McAeGjtgLPtcg2ybIR8CiaoFajcFhA7QA9BryVblRdxL3sw4ZSLu6ye9UdYys";
//        String apiSecretKeyTest = "HrsyQE8dM1G97KMalpC806tFrODZ71DGEUiySfni2mhdK0Epu2c1gGXy3utHJmJL";
//
//
////        String apiKeyTest = System.getenv("API_KEY_TEST");
////        String apiSecretKeyTest = System.getenv("API_SECRET_KEY_TEST");
////        String testURL = System.getenv("TESTNET_URL");
//
//        RequestOptions options = new RequestOptions();
////        options.setUrl("https://testnet.binancefuture.com");
//
//        options.setUrl("https://fapi.binance.com/");
//        SyncRequestClient syncRequestClient = SyncRequestClient.create(apiKeyTest, apiSecretKeyTest,
//                options);
//
//        System.out.println(syncRequestClient.getExchangeInformation());
//
////        String listenKey = syncRequestClient.startUserDataStream();
////
//        // Keep user data stream
//        syncRequestClient.keepUserDataStream(listenKey);
////
////        // Close user data stream
////        syncRequestClient.closeUserDataStream(listenKey);
//        System.out.println(listenKey);
//
//        SubscriptionOptions subscriptionOptions = new SubscriptionOptions();
//        subscriptionOptions.setUri("wss://stream.binancefuture.com");
//        SubscriptionClient client = SubscriptionClient.create();
//
//        List<UserDataUpdateEvent> userList = new LinkedList<>();
//
////        WebSocketListener
//        //            for (UserDataUpdateEvent event : userList) {
//        //                System.out.println(event.getEventType());
//        //            }
//
//
//        client.subscribeUserDataEvent(listenKey, System.out::println, null);
//
//



//
//        SubscriptionOptions subscriptionOptions = new SubscriptionOptions();
//        subscriptionOptions.setUri("wss://stream.binance.com:9443/ws/btcusdt@aggTrade");
//
//
//
//        client.subscribeMarkPriceEvent("BTCUSDT", listener, );
//
//        WebSocketStreamClientImpl


    }

    }




//
//
//        RequestOptions options = new RequestOptions();
//        options.setUrl(testURL);
//        SyncRequestClient syncRC = SyncRequestClient.create(apiKeyTest, apiSecretKeyTest, options);
////
//        System.out.println(syncRC.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.SHORT, OrderType.LIMIT, TimeInForce.GTC,
//                "1", "64000", null, null, null, null, NewOrderRespType.RESULT));

//        syncRC.getMarkPrice("BTCUSDT");

//        System.out.println(syncRC.getMarkPrice("BTCUSDT"));

//        syncRC.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.SHORT, OrderType.LIMIT, TimeInForce.GTC,
//                "1", "64000", null, null, null, null, NewOrderRespType.RESULT);

//
//
//
//
//    }
//
//}
