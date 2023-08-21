package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.service.UserService;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackManager;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackQueryExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class DeclineNewsCallbackQuery implements UpdateCallbackQueryExecutor {
    private final TelegramBot bot;

    private final UpdateCallbackManager callbackManager;

    private final LangConfiguration langConfiguration;

    private final UserService userService;

    @PostConstruct
    private void createQuery() {
        callbackManager.create("decline-news", this);
    }

    @Autowired
    public DeclineNewsCallbackQuery(final @NotNull TelegramBot bot,
                                    final @NotNull UpdateCallbackManager callbackManager,
                                    final @NotNull LangConfiguration langConfiguration,
                                    final @NotNull UserService userService) {
        this.bot = bot;
        this.callbackManager = callbackManager;
        this.langConfiguration = langConfiguration;
        this.userService = userService;
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull User user, @NotNull CallbackQuery callback) {
        val message = callback.getMessage();
        val userId = String.valueOf(user.getId());
        val language = langConfiguration.getLanguage(userService.getUser(Long.valueOf(userId)).getLang());

        bot.execute(new DeleteMessage(userId, callback.getMessage().getMessageId()));
        bot.execute(new SendMessage(userId, language.getString("news_rejected")));
    }
}
