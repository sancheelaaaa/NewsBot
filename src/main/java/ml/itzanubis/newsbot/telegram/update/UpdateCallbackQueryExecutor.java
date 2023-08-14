package ml.itzanubis.newsbot.telegram.update;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UpdateCallbackQueryExecutor {
    void execute(final @NotNull User user,
                 final @NotNull CallbackQuery callback);
}
