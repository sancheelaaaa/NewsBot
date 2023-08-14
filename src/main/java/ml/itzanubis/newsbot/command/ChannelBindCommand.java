package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.entity.Channel;
import ml.itzanubis.newsbot.service.ChannelService;
import ml.itzanubis.newsbot.telegram.command.CommandExecutor;
import ml.itzanubis.newsbot.telegram.command.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class ChannelBindCommand implements CommandExecutor {
    private final ChannelService channelService;

    private final TelegramBot bot;

    private final CommandManager commandManager;

    private final TelegramBotConfiguration configuration;

    @Autowired
    private ChannelBindCommand(final @NotNull ChannelService channelService,
                               final @NotNull TelegramBot bot,
                               final @NotNull CommandManager commandManager,
                               final @NotNull TelegramBotConfiguration configuration) {

        this.channelService = channelService;
        this.bot = bot;
        this.commandManager = commandManager;
        this.configuration = configuration;
    }

    @PostConstruct
    private void init() {
        commandManager.createCommand("/bind", this);
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args) {

        val userId = String.valueOf(user.getId());

        if (args.length != 1) {
            bot.execute(new SendMessage(userId, "Отправьте айди чата!"));
            return;
        }

        val chatId = args[0];

        if (!isNumeric(chatId)) {
            bot.execute(new SendMessage(userId, "Некорректное айди чата!"));
            return;
        }

        if (!isChatExist(chatId)) {
            bot.execute(new SendMessage(userId, "Бот не находится в чате!"));
            return;
        }

        val channel = bot.execute(new GetChat(chatId));

        if (!channelService.isAdmin(chatId)) {
            bot.execute(new SendMessage(userId, "Бот в чате не администратор!"));
            return;
        }

        if (channelService.getChannel(userId) != null) {
            bot.execute(new SendMessage(userId, "Вы уже добавляли свой канал!"));
            return;
        }

        val channelEntity = new Channel(channel.getId(), channel.getTitle(), userId);

        channelService.createChannel(userId, channelEntity);

        val channelName = channelEntity.getName();
        bot.execute(new SendMessage(userId, "Вы добавили чат: " + channelName));
        TelegramBot.getLogger().info("Added a new chat: " + channelName);
    }

    private boolean isNumeric(final @NotNull String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isChatExist(final @NotNull String chatId) {
        try {
            bot.execute(new GetChat(chatId));
            return true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }
}
