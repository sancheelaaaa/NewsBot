package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
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

    @Autowired
    private ChannelListCommand(ChannelService channelService, TelegramBot bot, CommandManager commandManager) {
        this.channelService = channelService;
        this.bot = bot;
        this.commandManager = commandManager;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/каналы", this);
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull Message message, @NotNull User user, @NotNull Chat chat, @NotNull String[] args) {
        val channels = channelService.collectAll();
        val userId = String.valueOf(user.getId());

        if (channels.isEmpty()) {
            bot.execute(new SendMessage(userId, "У вас нет привязанных каналов!"));
            return;
        }

        val messageBuilder = new MessageBuilder();
        messageBuilder.add("Список Ваших каналов: ");

        for (int i = 0; i < channels.size(); i++) {
            messageBuilder.add(i + ": " + channels.stream().toList().get(i).getName());
        }

        bot.execute(new SendMessage(userId, messageBuilder.build()));
    }
}
