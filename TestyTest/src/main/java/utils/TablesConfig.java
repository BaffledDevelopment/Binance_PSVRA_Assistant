package utils;

import com.typesafe.config.*;

public class TablesConfig {

    private Config config;

    public TablesConfig(Config config) {
        this.config = config;

        config.checkValid(ConfigFactory.defaultReference(), "config_file");
    }

    public TablesConfig() {
        // This uses the standard default Config, if none is provided,
        // which simplifies apps willing to use the defaults
        this(ConfigFactory.load());
    }

    public void printSetting(String path) {
        System.out.println("The setting '" + path + "' is: " + config.getString(path));
    }


    public static void main(String[] args) {
        TablesConfig cfg = new TablesConfig();
        Config conf = ConfigFactory.load();
        String test1 = conf.getString("symbols.foo");

        System.out.println(test1);

//        cfg.printSetting("..\\TestyTest\\src\\main\\java\\utils\\config_file.conf");
    }


}
