package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.service.ChannelService;
import ml.itzanubis.newsbot.service.UserService;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackManager;
import ml.itzanubis.newsbot.telegram.update.UpdateCallbackQueryExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("ALL")
public class AcceptNewsCallbackQuery implements UpdateCallbackQueryExecutor {
    private final TelegramBot bot;

    private final UpdateCallbackManager callbackManager;

    private final ChannelService channelService;

    private final UserService userService;

    private final LangConfiguration langConfiguration;

    @PostConstruct
    private void createQuery() {
        callbackManager.create("accept-news", this);
    }

    @Autowired
    public AcceptNewsCallbackQuery(final @NotNull TelegramBot bot,
                                   final @NotNull UpdateCallbackManager callbackManager,
                                   final @NotNull ChannelService channelService,
                                   final @NotNull UserService userService,
                                   final @NotNull LangConfiguration langConfiguration) {
        this.bot = bot;
        this.callbackManager = callbackManager;
        this.channelService = channelService;
        this.userService = userService;
        this.langConfiguration = langConfiguration;
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull User user, @NotNull CallbackQuery callback) {
        val message = callback.getMessage();
        val userId = String.valueOf(user.getId());
        val channel = channelService.getChannel(userId);
        val replyKeyboardMarkup = new InlineKeyboardMarkup();
        val checkButton = new InlineKeyboardButton();
        val buttonsRow = new ArrayList<List<InlineKeyboardButton>>();
        val answerCallbackquery = new AnswerCallbackQuery(callback.getId());
        val language = langConfiguration.getLanguage(userService.getUser(Long.valueOf(userId)).getLang());

        answerCallbackquery.setShowAlert(true);

        checkButton.setCallbackData("inform-news");

        buttonsRow.add(List.of(checkButton));

        checkButton.setText(language.getString("suggest_news"));
        replyKeyboardMarkup.setKeyboard(buttonsRow);

        if (channel == null) {
            answerCallbackquery.setText(language.getString("chat_not_found"));
            bot.execute(answerCallbackquery);
            return;
        }

        if (!channelService.isAdmin(String.valueOf(channel.getId()))) {
            answerCallbackquery.setText(language.getString("bot_is_not_admin"));
            bot.execute(answerCallbackquery);
            return;
        }

        if (message.hasPhoto()) {
            val news = message.getCaption();
            val informedMessage = new SendPhoto();
            val photo = bot.downloadFile(bot.execute(new GetFile(message.getPhoto().get(2).getFileId())));

            informedMessage.setCaption(news.replaceAll(language.getString("you_have_unread_news"), ""));
            informedMessage.setPhoto(new InputFile(photo));
            informedMessage.setChatId(channel.getId());
            informedMessage.setReplyMarkup(replyKeyboardMarkup);

            answerCallbackquery.setText("Пост отправлен!");

            bot.execute(answerCallbackquery);
            bot.execute(informedMessage);
            deleteMessage(userId, message.getMessageId());
            return;
        }

        val informedMessage = new SendMessage();
        val news = message.getText();

        informedMessage.setText(news.replaceAll(language.getString("you_have_unread_news"), ""));
        informedMessage.setChatId(channel.getId());
        informedMessage.setReplyMarkup(replyKeyboardMarkup);
        answerCallbackquery.setText(language.getString("post_was_send"));

        bot.execute(answerCallbackquery);
        bot.execute(informedMessage);

        deleteMessage(userId, message.getMessageId());
    }

    @SneakyThrows
    private void deleteMessage(String userId, int messageId) {
        bot.execute(new DeleteMessage(userId, messageId));
    }
}
