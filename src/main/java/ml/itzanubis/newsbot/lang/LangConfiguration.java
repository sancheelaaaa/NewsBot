package ml.itzanubis.newsbot.lang;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public final class LangConfiguration {
    private final static File russianConfigurationFile = new File("cfg/ru_lang.yml");
    private final static File englishConfigurationFile = new File("cfg/en_lang.yml");

    @SneakyThrows
    public void create() {
        if (russianConfigurationFile.exists() || englishConfigurationFile.exists()) {
            return;
        }

        if (russianConfigurationFile.createNewFile() || englishConfigurationFile.createNewFile()) {
            System.out.println("Created configurations!");
        }
    }

    // TODO: read configuration files
}
