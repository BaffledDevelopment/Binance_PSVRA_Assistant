package utils;

import java.util.HashMap;
import java.util.Map;

// usage:         Create_Table ct = new Create_Table();
//
//                ct.createTable();

public class TableSchema {

    public Map<String, String> TABLE_SCHEMA = new HashMap<>();

    public TableSchema() {
//        TABLE_SCHEMA.put("time", "TIMESTAMP NOT NULL PRIMARY KEY");
//        TABLE_SCHEMA.put("order_id", "BIGINT");
//        TABLE_SCHEMA.put("isFilled", "BOOL NOT NULL");
//        TABLE_SCHEMA.put("amount", "DOUBLE PRECISION");
//        TABLE_SCHEMA.put("")
//        TABLE_SCHEMA.put("open", "NUMERIC");
//        TABLE_SCHEMA.put("high", "NUMERIC");
//        TABLE_SCHEMA.put("low", "NUMERIC");
//        TABLE_SCHEMA.put("close", "NUMERIC");
//        TABLE_SCHEMA.put("base_volume", "NUMERIC");
//        TABLE_SCHEMA.put("quote_volume", "NUMERIC");
//        TABLE_SCHEMA.put("trades", "INTEGER");
//        TABLE_SCHEMA.put("kline_closed", "BOOL");
//        TABLE_SCHEMA.put("is_vector", "BOOL NOT NULL SET DEFAULT FALSE");
    }

    public String composeTableSchema() {

//        String composeTableQuery = String.join(", ", TABLE_SCHEMA. ne rabotaet);

        String delim = "";

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> element : TABLE_SCHEMA.entrySet()) {
            stringBuilder.append(delim);
            delim = ", ";
            stringBuilder.append(element.getKey()).append(" ").append(element.getValue());

        }

        return stringBuilder.toString();
    }
}
