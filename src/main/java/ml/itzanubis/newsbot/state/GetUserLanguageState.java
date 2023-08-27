package ml.itzanubis.newsbot.state;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.repository.UserRepository;
import ml.itzanubis.newsbot.telegram.machine.FieldStateMachine;
import ml.itzanubis.newsbot.telegram.machine.UserState;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class GetUserLanguageState implements UserState {

    private final LangConfiguration langConfiguration;

    private final UserRepository userRepository;

    private final TelegramBot telegramBot;

    @Autowired
    public GetUserLanguageState(final @NotNull LangConfiguration langConfiguration,
                                final @NotNull UserRepository userRepository,
                                final @NotNull TelegramBot telegramBot) {

        this.langConfiguration = langConfiguration;
        this.userRepository = userRepository;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    private void init() {
        FieldStateMachine.addState("getUserLanguage", this);
    }

    @SneakyThrows
    @Override
    public void state(@NotNull User user, @NonNull Message message, final Object[] callbackData) {
        val userEntity = new UserEntity();
        val userLanguage = message.getText();
        val userId = Math.toIntExact(user.getId());

        userEntity.setId(userId);
        if (langConfiguration.getLanguage(userLanguage) == null) {
            return;
        }

        userEntity.setLang(userLanguage);
        userRepository.save(userEntity);

        val removeKeyboard = new SendMessage();

        removeKeyboard.setReplyMarkup(new ReplyKeyboardRemove(true));
        removeKeyboard.setChatId((long) userId);
        removeKeyboard.setText(langConfiguration.getLanguage(userLanguage).getString("hello_message"));

        telegramBot.execute(removeKeyboard);

        FieldStateMachine.cancelState(user);
    }

}
