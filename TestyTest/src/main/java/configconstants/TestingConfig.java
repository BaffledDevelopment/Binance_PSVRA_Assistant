package configconstants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class TestingConfig {



    public static void main(String[] args) {
        Config conf = ConfigFactory.load("config_file.conf");
//        conf.get

//        for (Config element : conf) {
//
//        }

        String driver = conf.getString("jdbc.url");
        System.out.println(driver.toString());
    }


}
