package ml.itzanubis.newsbot.telegram.machine;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserState {
    void state(final @NonNull User user, final @NonNull Message message, final @NonNull Object[] callbackData);
}
