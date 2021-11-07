package database_impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    public static void main(String[] args) {
        ConnectDB obj_Connection = new ConnectDB();

        System.out.println(obj_Connection.getDBConnection());
    }

    public Connection getDBConnection () {
        Connection connection = null;
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "docker";

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                System.out.println("Succesfully connected");
            } else {
                System.out.println("Failed to connect to PostgreSQL connection");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

}
