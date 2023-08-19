package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
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

    private final LangConfiguration langConfiguration;

    @Autowired
    public StartCommand(CommandManager commandManager, TelegramBot bot, LangConfiguration langConfiguration) {
        this.commandManager = commandManager;
        this.bot = bot;
        this.langConfiguration = langConfiguration;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/start", this);
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args,
                        final @NotNull UserEntity userEntity) {

        bot.execute(new SendMessage(String.valueOf(user.getId()), langConfiguration.getLanguage(userEntity.getLang()).getString("hello_message")));
    }
}
