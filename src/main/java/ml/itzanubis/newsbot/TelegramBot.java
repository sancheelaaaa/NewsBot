package ml.itzanubis.newsbot;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.telegram.command.CommandManager;
import ml.itzanubis.newsbot.telegram.machine.FieldStateMachine;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;

@Controller
@SuppressWarnings("ALL")
public class TelegramBot extends TelegramLongPollingBot {
    @Getter
    private final static Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramBotConfiguration configuration;

    private final CommandManager commandManager;

    private final UpdateCallbackManager callbackManager;

    @Autowired
    private TelegramBot(final @NotNull TelegramBotConfiguration configuration,
                        final @NotNull CommandManager commandManager,
                        final @NotNull UpdateCallbackManager callbackManager) {

        this.configuration = configuration;
        this.commandManager = commandManager;
        this.callbackManager = callbackManager;
    }

    @SneakyThrows
    @PostConstruct
    private void start() {
        val telegramBotSession = new TelegramBotsApi(DefaultBotSession.class);

        telegramBotSession.registerBot(this);
        logger.info("Bot has been started with username: " + this.getBotUsername());
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(final @NotNull Update update) {
        if (update.hasCallbackQuery()) {
            val callbackQuery = update.getCallbackQuery();
            callbackManager.call(callbackManager.get(callbackQuery.getData()), callbackQuery.getFrom(), callbackQuery);
            return;
        }

        val message = update.getMessage();
        val user = message.getFrom();
        val userState = FieldStateMachine.getState(update.getMessage().getFrom());
        val callbackData = FieldStateMachine.getCallback(userState);

        if (userState != null) {
            userState.state(user, message, callbackData);
            return;
        }


        val args = message.getText().split(" ");
        val key = args[0];

        if (!commandManager.isExist(key)) {
            logger.error("Command not found: " + key);
            return;
        }

        commandManager.execute(key, message, message.getFrom(), message.getChat(), Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public String getBotToken() {
        return configuration.getToken();
    }

    @Override
    public String getBotUsername() {
        return configuration.getUsername();
    }
}
