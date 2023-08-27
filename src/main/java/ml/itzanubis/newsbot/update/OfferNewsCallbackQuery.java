package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.service.UserService;
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

    private final LangConfiguration langConfiguration;

    private final UserService userService;

    @PostConstruct
    private void init() {
        callbackManager.create("inform-news", this);
    }

    @Autowired
    private OfferNewsCallbackQuery(final @NotNull TelegramBotConfiguration configuration,
                                   final @NotNull TelegramBot bot,
                                   final @NotNull UpdateCallbackManager callbackManager,
                                   final @NotNull LangConfiguration langConfiguration,
                                   final @NotNull UserService userService) {

        this.configuration = configuration;
        this.bot = bot;
        this.callbackManager = callbackManager;
        this.langConfiguration = langConfiguration;
        this.userService = userService;
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull User user, @NotNull CallbackQuery callback) {
        val callbackData = callback.getData();
        val answerCallbackquery = new AnswerCallbackQuery(callback.getId());
        val language = langConfiguration.getLanguage(userService.getUser(user.getId()).getLang());

        answerCallbackquery.setShowAlert(true);
        answerCallbackquery.setText(language.getString("offer_news").replace("{channel}", configuration.getUsername()));

        bot.execute(answerCallbackquery);
    }

}
