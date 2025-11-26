package ma.nassnewsapp.backend.config;

import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.load();

    static {
        // Charger les variables depuis le fichier .env
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    public static String getIpstackApiKey() {
        return dotenv.get("IPSTACK_API_KEY");
    }
}
