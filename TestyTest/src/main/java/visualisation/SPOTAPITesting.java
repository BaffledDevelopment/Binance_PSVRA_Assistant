package visualisation;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import configconstants.APIConstants;

public class SPOTAPITesting {

    public static void main(String[] args) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(APIConstants.API_KEY, APIConstants.API_SECRET_KEY);
        BinanceApiRestClient client = factory.newRestClient();

        // First, we obtain a listenKey which is required to interact with the user data stream
        String listenKey = client.startUserDataStream();

        // Then, we open a new web socket client, and provide a callback that is called on every update
        BinanceApiWebSocketClient webSocketClient = factory.newWebSocketClient();

        // Listen for changes in the account
        webSocketClient.onUserDataUpdateEvent(listenKey, response -> {
            if (response.getEventType() == UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_POSITION_UPDATE) {
//                AccountUpdateEvent accountUpdateEvent = response.getOutboundAccountPositionUpdateEvent();
                // Print new balances of every available asset
                System.out.println("pee");
            } else {
                OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                // Print details about an order/trade
                System.out.println(orderTradeUpdateEvent);

                // Print original quantity
                System.out.println(orderTradeUpdateEvent.getOriginalQuantity());

                // Or price
                System.out.println(orderTradeUpdateEvent.getPrice());
            }
        });
        System.out.println("Waiting for events...");

        // We can keep alive the user data stream
        // client.keepAliveUserDataStream(listenKey);

        // Or we can invalidate it, whenever it is no longer needed
        // client.closeUserDataStream(listenKey);
    }

}
