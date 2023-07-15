package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
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
public class ChannelUnbindCommand implements CommandExecutor {
    private final CommandManager commandManager;

    private final ChannelService channelService;

    private final TelegramBot bot;

    @Autowired
    public ChannelUnbindCommand(CommandManager commandManager, ChannelService channelService, TelegramBot bot) {
        this.commandManager = commandManager;
        this.channelService = channelService;
        this.bot = bot;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/unbind", this);
    }

    @Override
    @SneakyThrows
    public void execute(@NotNull Message message, @NotNull User user, @NotNull Chat chat, @NotNull String[] args) {
        val userId = String.valueOf(user.getId());
        val channel = channelService.getChannel(userId);

        if (channel == null) {
            bot.execute(new SendMessage(userId, "У вас нет привязанного канала!"));
            return;
        }

        channelService.deleteChannel(channel);
        bot.execute(new SendMessage(userId, "Вы успешно отвязали канал!"));
    }
}
