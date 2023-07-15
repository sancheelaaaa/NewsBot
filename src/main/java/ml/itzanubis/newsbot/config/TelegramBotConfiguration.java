package ml.itzanubis.newsbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "telegram")
public class TelegramBotConfiguration {
    private String token;

    private String username;

}
