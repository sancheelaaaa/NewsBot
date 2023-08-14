package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.fsm.GetUserInformMessageState;
import ml.itzanubis.newsbot.service.ChannelService;
import ml.itzanubis.newsbot.telegram.command.CommandExecutor;
import ml.itzanubis.newsbot.telegram.command.CommandManager;
import ml.itzanubis.newsbot.telegram.machine.FieldStateMachine;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class ChannelInformCommand implements CommandExecutor {
    private final ChannelService channelService;

    private final TelegramBot telegramBot;

    private final GetUserInformMessageState getUserInformMessageState;

    private final CommandManager commandManager;

    @PostConstruct
    private void init() {
        commandManager.createCommand("/inform", this);
    }

    @Autowired
    public ChannelInformCommand(final @NotNull ChannelService channelService,
                                final @NotNull TelegramBot telegramBot,
                                final @NotNull GetUserInformMessageState getUserInformMessageState,
                                final @NotNull CommandManager commandManager) {

        this.channelService = channelService;
        this.telegramBot = telegramBot;
        this.getUserInformMessageState = getUserInformMessageState;
        this.commandManager = commandManager;
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args) {
        
        val executorId = String.valueOf(user.getId());

        if (args.length < 1) {
            telegramBot.execute(new SendMessage(executorId, "Введите айди канала!"));
            return;
        }

        val channelId = args[0];

        if (!isNumeric(channelId)) {
            telegramBot.execute(new SendMessage(executorId, "Некорректное айди канала!"));
            return;
        }

        val channel = channelService.getChannelById(channelId);

        if (channel == null) {
            telegramBot.execute(new SendMessage(executorId, "Чат не найден!"));
            return;
        }

        telegramBot.execute(new SendMessage(executorId, "Пришлите новость в след. сообщении!"));

        FieldStateMachine.addCallback(getUserInformMessageState, new Object[]{channel});
        FieldStateMachine.createState(user, getUserInformMessageState);
    }

    private boolean isNumeric(final @NotNull String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
