package ml.itzanubis.newsbot.update;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.service.ChannelService;
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

    @PostConstruct
    private void createQuery() {
        callbackManager.create("accept-news", this);
    }

    @Autowired
    public AcceptNewsCallbackQuery(@NotNull TelegramBot bot, UpdateCallbackManager callbackManager, ChannelService channelService) {
        this.bot = bot;
        this.callbackManager = callbackManager;
        this.channelService = channelService;
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

        answerCallbackquery.setShowAlert(true);

        checkButton.setCallbackData("inform-news");

        buttonsRow.add(List.of(checkButton));

        checkButton.setText("Предложить новость.");
        replyKeyboardMarkup.setKeyboard(buttonsRow);

        if (channel == null) {
            answerCallbackquery.setText("Чат не существует!");
            bot.execute(answerCallbackquery);
            return;
        }

        if (!channelService.isAdmin(String.valueOf(channel.getId()))) {
            answerCallbackquery.setText("Бот в чате не администратор!");
            bot.execute(answerCallbackquery);
            return;
        }

        if (message.hasPhoto()) {
            val news = message.getCaption();
            val informedMessage = new SendPhoto();
            val photo = bot.downloadFile(bot.execute(new GetFile(message.getPhoto().get(2).getFileId())));

            informedMessage.setCaption(news.replaceAll("Вам пришла новость: ", ""));
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

        informedMessage.setText(news.replaceAll("Вам пришла новость: ", ""));
        informedMessage.setChatId(channel.getId());
        informedMessage.setReplyMarkup(replyKeyboardMarkup);
        answerCallbackquery.setText("Пост отправлен!");

        bot.execute(answerCallbackquery);
        bot.execute(informedMessage);

        deleteMessage(userId, message.getMessageId());
    }

    @SneakyThrows
    private void deleteMessage(String userId, int messageId) {
        bot.execute(new DeleteMessage(userId, messageId));
    }
}
