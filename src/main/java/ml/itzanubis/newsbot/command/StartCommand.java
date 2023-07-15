package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.telegram.command.CommandExecutor;
import ml.itzanubis.newsbot.telegram.command.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Controller
public class StartCommand implements CommandExecutor {
    private final CommandManager commandManager;

    private final TelegramBot bot;

    @Autowired
    public StartCommand(CommandManager commandManager, TelegramBot bot) {
        this.commandManager = commandManager;
        this.bot = bot;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/start", this);
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull Message message, @NotNull User user, @NotNull Chat chat, @NotNull String[] args) {
        bot.execute(new SendMessage(String.valueOf(user.getId()), "Привет, я чат бот для Ваших новостей!"));
    }
}
