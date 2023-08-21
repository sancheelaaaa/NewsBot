package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.service.ChannelService;
import ml.itzanubis.newsbot.telegram.command.CommandExecutor;
import ml.itzanubis.newsbot.telegram.command.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@SuppressWarnings("ALL")
public class ChannelUnbindCommand implements CommandExecutor {
    private final CommandManager commandManager;

    private final ChannelService channelService;

    private final TelegramBot bot;

    private final LangConfiguration langConfiguration;

    @Autowired
    public ChannelUnbindCommand(CommandManager commandManager, ChannelService channelService, TelegramBot bot, LangConfiguration langConfiguration) {
        this.commandManager = commandManager;
        this.channelService = channelService;
        this.bot = bot;
        this.langConfiguration = langConfiguration;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/unbind", this);
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args,
                        final @NotNull UserEntity userEntity) {

        val userId = String.valueOf(user.getId());
        val channel = channelService.getChannel(userId);
        val language = langConfiguration.getLanguage(userEntity.getLang());

        if (channel == null) {
            bot.execute(new SendMessage(userId, language.getString("dont_having_channels")));
            return;
        }

        channelService.deleteChannel(channel);
        bot.execute(new SendMessage(userId, language.getString("success_unbind")));
    }
}
