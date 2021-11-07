import database_impl.ConnectDB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
//import

public class TestSQLQueries {

    public static void main(String[] args) throws InterruptedException, IOException, SQLException {

        ConnectDB konnectDB = new ConnectDB();
        Connection connection = konnectDB.getDBConnection();
        Statement statement = connection.createStatement();

        long orderID = 14283964101L;

        String prepStatement = String.format("UPDATE futures_orders SET order_status = 'FILLED' WHERE order_id = %s", orderID);

        try {
            PreparedStatement pstm = connection.prepareStatement(prepStatement);
            pstm.execute();
            System.out.println("Mashallah");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}    