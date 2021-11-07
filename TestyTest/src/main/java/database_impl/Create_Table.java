package database_impl;

import utils.TableSchema;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Create_Table {

    public void createTable(String table_name) {

        TableSchema schema = new TableSchema();

        Connection connection;
        Statement statement;

        ConnectDB obj_connectDB = new ConnectDB();
        connection = obj_connectDB.getDBConnection();

//        String table_name = "thisistimescale";

        String query = "CREATE TABLE IF NOT EXISTS " + table_name + "(" + schema.composeTableSchema() + ")";

        try {
            System.out.println(query);
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Statement sent and updated");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
