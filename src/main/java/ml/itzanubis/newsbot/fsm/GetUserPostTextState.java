package ml.itzanubis.newsbot.fsm;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.service.ChannelService;
import ml.itzanubis.newsbot.telegram.machine.FieldStateMachine;
import ml.itzanubis.newsbot.telegram.machine.UserState;
import ml.itzanubis.newsbot.telegram.message.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GetUserPostTextState implements UserState {
    private final ChannelService channelService;

    private final TelegramBot bot;

    @PostConstruct
    private void createState() {
        FieldStateMachine.addState("getUserPostTextState", this);
    }

    @Autowired
    public GetUserPostTextState(ChannelService channelService, TelegramBot bot) {
        this.channelService = channelService;
        this.bot = bot;
    }

    @Override
    @SneakyThrows
    public void state(@NonNull User user, @NonNull Message message) {
        val postText = new MessageBuilder();
        val userId = String.valueOf(user.getId());
        val channelEntity = channelService.getChannel(userId);
        val channelId = String.valueOf(channelEntity.getId());
        val replyKeyboardMarkup = new InlineKeyboardMarkup();
        val checkButton = new InlineKeyboardButton();
        val buttonsRow = new ArrayList<List<InlineKeyboardButton>>();

        checkButton.setCallbackData("inform-news");

        buttonsRow.add(List.of(checkButton));

        checkButton.setText("Предложить новость.");
        replyKeyboardMarkup.setKeyboard(buttonsRow);

        if (message.hasPhoto()) {
            val messageArgs = message.getCaption().split(" ");
            val post = Arrays.copyOfRange(messageArgs, 1, messageArgs.length);
            val photo = bot.downloadFile(bot.execute(new GetFile(message.getPhoto().get(2).getFileId())));
            val sendPost = new SendPhoto();

            for (String arg : post) {
                postText.add(arg).append(" ");
            }

            sendPost.setParseMode("html");
            sendPost.setChatId(channelId);
            sendPost.setReplyMarkup(replyKeyboardMarkup);
            sendPost.setPhoto(new InputFile(photo));
            sendPost.setCaption(postText.build());

            bot.execute(sendPost);
            bot.execute(new SendMessage(userId, "Пост отправлен!"));

            FieldStateMachine.cancelState(user);
            return;
        }

        val messageArgs = message.getText().split(" ");
        val post = Arrays.copyOfRange(messageArgs, 0, messageArgs.length);

        val sendPost = new SendMessage();

        for (String arg : post) {
            postText.add(arg).append(" ");
        }

        sendPost.enableHtml(true);
        sendPost.setChatId(channelId);
        sendPost.setText(postText.build());
        sendPost.setReplyMarkup(replyKeyboardMarkup);

        bot.execute(sendPost);
        bot.execute(new SendMessage(userId, "Пост отправлен!"));

        FieldStateMachine.cancelState(user);
    }
}
