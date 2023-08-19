package ml.itzanubis.newsbot;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.state.GetUserLanguageState;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.repository.UserRepository;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;
import java.util.Collections;

@Controller
@SuppressWarnings("ALL")
public class TelegramBot extends TelegramLongPollingBot {

    @Getter
    private final static Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final TelegramBotConfiguration configuration;

    private final CommandManager commandManager;

    private final UpdateCallbackManager callbackManager;

    private final UserRepository userRepository;

    private final LangConfiguration langConfiguration;

    @Autowired
    private TelegramBot(final @NotNull TelegramBotConfiguration configuration,
                        final @NotNull CommandManager commandManager,
                        final @NotNull UpdateCallbackManager callbackManager,
                        final @NotNull UserRepository userRepository,
                        final @NotNull LangConfiguration langConfiguration) {

        this.configuration = configuration;
        this.commandManager = commandManager;
        this.callbackManager = callbackManager;
        this.userRepository = userRepository;
        this.langConfiguration = langConfiguration;
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
        val callbackQuery = update.getCallbackQuery();

        if (update.hasCallbackQuery()) {
            callbackManager.call(callbackManager.get(callbackQuery.getData()), callbackQuery.getFrom(), callbackQuery);
            return;
        }

        val message = update.getMessage();
        val user = message.getFrom();
        val userState = FieldStateMachine.getState(update.getMessage().getFrom());
        val callbackData = FieldStateMachine.getCallback(userState);
        val userId = Math.toIntExact(user.getId());
        val userExist = !(userRepository.getUserById(userId).orElse(null) == null);

        if (userState != null) {
            userState.state(user, message, callbackData);
            return;
        }

        if (!userExist) {
            val sendMessage = new SendMessage();

            sendMessage.setChatId((long) userId);
            sendMessage.setText("Choose your language: ");

            val replyKeyboardMarkup = new ReplyKeyboardMarkup();
            val keyboardRow = new KeyboardRow();

            langConfiguration.getLanguagesNames().forEach(language -> keyboardRow.add(language));
            replyKeyboardMarkup.setResizeKeyboard(true);

            replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            FieldStateMachine.createState(user, new GetUserLanguageState(langConfiguration, userRepository, this));
            execute(sendMessage);

            return;
        }

        val args = message.getText().split(" ");
        val key = args[0];
        val userEntity = userRepository.getUserById(userId).orElse(null);

        if (!commandManager.isExist(key)) {
            return;
        }

        commandManager.execute(key, message, message.getFrom(), message.getChat(), Arrays.copyOfRange(args, 1, args.length), userEntity);
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
