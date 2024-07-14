package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties properties = new Properties();

    static {
        // Статический блок для загрузки свойств из файла при инициализации класса
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");

            }
            properties.load(input);
        } catch (IOException ex) {
            // Обработка исключений при чтении файла
            ex.printStackTrace();
        }
    }

    // Метод для получения значения свойства по ключу
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
