package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackManager;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackQueryExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class OfferNewsCallbackQuery implements UpdateCallbackQueryExecutor {
    private final TelegramBotConfiguration configuration;

    private final TelegramBot bot;

    private final UpdateCallbackManager callbackManager;

    @PostConstruct
    private void init() {
        callbackManager.create("inform-news", this);
    }

    @Autowired
    private OfferNewsCallbackQuery(TelegramBotConfiguration configuration, TelegramBot bot, UpdateCallbackManager callbackManager) {
        this.configuration = configuration;
        this.bot = bot;
        this.callbackManager = callbackManager;
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull User user, @NotNull CallbackQuery callback) {
        val callbackData = callback.getData();

        val answerCallbackquery = new AnswerCallbackQuery(callback.getId());

        answerCallbackquery.setShowAlert(true);
        answerCallbackquery.setText("Напишите боту @" + configuration.getUsername() + " команду: /предложить [чат]");

        bot.execute(answerCallbackquery);
    }
}
