package ml.itzanubis.newsbot.fsm;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.val;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.repository.UserRepository;
import ml.itzanubis.newsbot.telegram.machine.FieldStateMachine;
import ml.itzanubis.newsbot.telegram.machine.UserState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class GetUserLanguageState implements UserState {

    private final LangConfiguration langConfiguration;

    private final UserRepository userRepository;

    public GetUserLanguageState(final @NotNull LangConfiguration langConfiguration,
                                final @NotNull UserRepository userRepository) {
        this.langConfiguration = langConfiguration;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void init() {
        FieldStateMachine.addState("getUserLanguage", this);
    }

    @Override
    public void state(@NotNull User user, @NonNull Message message, final Object[] callbackData) {
        val userEntity = new UserEntity();
        val userLanguage = message.getText();

        userEntity.setId(Math.toIntExact(user.getId()));
        if (langConfiguration.getLanguage(userLanguage) == null) {
            return;
        }

        userEntity.setLang(userLanguage);
        userRepository.save(userEntity);

        FieldStateMachine.cancelState(user);
    }
}
