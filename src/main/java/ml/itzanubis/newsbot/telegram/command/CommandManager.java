package ml.itzanubis.newsbot.telegram.command;

import ml.itzanubis.newsbot.TelegramBot;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@Component
public final class CommandManager {
    private static final Map<String, CommandExecutor> commands = new HashMap<>();

    public void createCommand(final @NotNull String key, final @NotNull CommandExecutor commandExecutor) {
        commands.put(key, commandExecutor);

        TelegramBot.getLogger().info("Created command with name: " + key);
    }

    public void execute(final @NotNull String key,
                        final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args) {

        commands.get(key).execute(message, user, chat, args);
    }

    public boolean isExist(final @NotNull String key) {
        return commands.get(key) != null;
    }
}
