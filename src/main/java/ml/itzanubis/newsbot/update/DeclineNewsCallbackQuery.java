package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
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

    @PostConstruct
    private void createQuery() {
        callbackManager.create("decline-news", this);
    }

    @Autowired
    public DeclineNewsCallbackQuery(TelegramBot bot, UpdateCallbackManager callbackManager) {
        this.bot = bot;
        this.callbackManager = callbackManager;
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull User user, @NotNull CallbackQuery callback) {
        val message = callback.getMessage();
        val userId = String.valueOf(user.getId());

        bot.execute(new DeleteMessage(userId, callback.getMessage().getMessageId()));
        bot.execute(new SendMessage(userId, "Предложение отклонено!"));
    }
}
