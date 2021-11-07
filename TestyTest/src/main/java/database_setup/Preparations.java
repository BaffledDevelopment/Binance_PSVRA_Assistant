package database_setup;

import database_impl.ConnectDB;
import utils.TableSchema;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Preparations {

    public void prepareUsers() {
        String query = "";

        try{
            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader("./src/main/java/database_setup/create_users.sql")
            );

            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str).append("\n ");
            }
            bufferedReader.close();
            query = sb.toString();

            Connection connection = new ConnectDB().getDBConnection();
            Statement statement;

            statement = connection.createStatement();
            statement.executeUpdate(query);
            connection.close();

        } catch (IOException | SQLException e){
            e.printStackTrace();
        }

    }

    public void prepareTable(String table_name) {
        //todo prepareTable should give access to other users and send notifications to a socket on event
    }

    public void prepareDatabase() {
        // idk just let it be
    }

    public void createTables(String table_name, TableSchema tableSchema, Connection connection, Statement statement) throws SQLException {
        String queryCreateTables = String.format("CREATE TABLE IF NOT EXISTS %s (%s) ", table_name, tableSchema.composeTableSchema());

        statement = connection.createStatement();
        statement.executeUpdate(queryCreateTables);

    }

    public void bootStrapEverything() throws SQLException {

        Connection connection = new ConnectDB().getDBConnection();
        Statement statement = null;
        TableSchema tableSchema = new TableSchema();

        //todo: implement tableNameList

//        for (String tableName : tableNameList) {
//            createTables(tableName, tableSchema, connection, statement);
//        }

        connection.close();

    } // todo: сделай итерацию для создания таблиц

    public static void main(String[] args) {
         Preparations test = new Preparations();
         test.prepareUsers();
    }
}
