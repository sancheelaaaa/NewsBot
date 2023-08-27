package ml.itzanubis.newsbot.lang;

import com.github.jsixface.YamlConfig;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ml.itzanubis.newsbot.NewsBotApplication;
import org.apache.commons.codec.language.bm.Lang;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class LangConfiguration {

    private static final Map<String, Language> languages = new HashMap<>();

    @PostConstruct
    @SneakyThrows
    private void create() {
        load("ru", "Russian");
        load("en", "English");
    }

    private YamlConfig load(final @NotNull String lang, final @NotNull String name) {
        val langStream
                = NewsBotApplication.class.getClassLoader().getResourceAsStream("cfg/" + lang + "_lang.yml");

        createLanguage(lang, name);

        return YamlConfig.load(langStream);
    }

    private Language createLanguage(final @NotNull String lang, final @NotNull String name) {
        val language = new Language(lang);

        languages.put(name, Language.create(lang));

        return language;
    }

    public YamlConfig getLanguage(final @NotNull String name) {
        return languages.get(name).getYamlConfig();
    }

    public List<String> getLanguagesNames() {
        return languages.keySet().stream().toList();
    }

    @Data
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    @RequiredArgsConstructor
    private final static class Language {
        
        private String name;

        @NonFinal
        private YamlConfig yamlConfig;

        @Getter
        private static final Map<String, Language> langs = new HashMap<>();

        @SneakyThrows
        public static Language create(final @NotNull String name) {
            val language = new Language(name);
            val languagePath = NewsBotApplication.class.getClassLoader().getResourceAsStream("cfg/" + name + "_lang.yml");

            langs.put(name, language);

            language.setYamlConfig(YamlConfig.load(languagePath));

            return language;
        }

    }

}
