package database_impl;

import utils.TableSchema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

// todo: recreate Create_Table and ConnectDB files for timescale part

public final class TimescaleConnect {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("jdbc.url", "jdbc:postgresql://localhost:5432/postgres");
        props.put("user", "postgres");
        props.put("password", "docker");

        try {
            Connection c = DriverManager.getConnection(props.getProperty("jdbc.url"), props);

            Statement statement; // переделать

            String table_name = "futures_orders";

            TableSchema schema = new TableSchema();

            String query = "CREATE TABLE IF NOT EXISTS " + table_name + "(" + schema.composeTableSchema() + ")";

            try {
                System.out.println(query);
                statement = c.createStatement();
                statement.executeUpdate(query);
                System.out.println("Statement sent and updated");

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Success");
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}