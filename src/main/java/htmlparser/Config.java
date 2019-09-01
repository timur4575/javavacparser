package htmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для работы с app.properties.
 * Считывает конфигурационные данные из файла в программу.
 */
public class Config {
    private Properties properties;
    private String propertyFile;

    public Config(String propFile) {
        this.propertyFile = propFile;
    }

    /**
     * Метод считывает данные для подключения к БД из app.properties;
     */
    public void init() {
        try (InputStream is = StoreSQL.class.getClassLoader().getResourceAsStream(propertyFile)) {
            properties = new Properties();
            properties.load(is);

        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public String get(String key) {
        return this.properties.getProperty(key);
    }
}
