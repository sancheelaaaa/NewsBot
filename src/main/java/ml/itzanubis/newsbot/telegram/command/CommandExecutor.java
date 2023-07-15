package ml.itzanubis.newsbot.telegram.command;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface CommandExecutor {
    void execute(final @NotNull Message message,
                 final @NotNull User user,
                 final @NotNull Chat chat,
                 final @NotNull String[] args);
}
