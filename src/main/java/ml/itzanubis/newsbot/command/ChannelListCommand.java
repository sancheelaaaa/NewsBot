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
import ml.itzanubis.newsbot.telegram.message.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class ChannelListCommand implements CommandExecutor {

    private final ChannelService channelService;

    private final TelegramBot bot;

    private final CommandManager commandManager;

    private final LangConfiguration langConfiguration;

    @Autowired
    private ChannelListCommand(final @NotNull ChannelService channelService,
                               final @NotNull TelegramBot bot,
                               final @NotNull CommandManager commandManager,
                               final @NotNull LangConfiguration langConfiguration) {

        this.channelService = channelService;
        this.bot = bot;
        this.commandManager = commandManager;
        this.langConfiguration = langConfiguration;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/каналы", this);
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args,
                        final @NotNull UserEntity userEntity) {

        val channels = channelService.collectAll();
        val userId = String.valueOf(user.getId());
        val language = langConfiguration.getLanguage(userEntity.getLang());

        if (channels.isEmpty()) {
            bot.execute(new SendMessage(userId, language.getString("dont_having_channels")));
            return;
        }

        val messageBuilder = new MessageBuilder();
        messageBuilder.add("Список Ваших каналов: ");

        for (int i = 0; i < channels.size(); i++) {
            messageBuilder.add("\n");
            messageBuilder.add((i+1) + ". " + channels.stream().toList().get(i).getName());
        }

        bot.execute(new SendMessage(userId, messageBuilder.build()));
    }
}
