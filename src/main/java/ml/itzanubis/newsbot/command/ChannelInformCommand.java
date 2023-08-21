package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.state.GetUserInformMessageState;
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

    private final LangConfiguration langConfiguration;

    @PostConstruct
    private void init() {
        commandManager.createCommand("/inform", this);
    }

    @Autowired
    public ChannelInformCommand(final @NotNull ChannelService channelService,
                                final @NotNull TelegramBot telegramBot,
                                final @NotNull GetUserInformMessageState getUserInformMessageState,
                                final @NotNull CommandManager commandManager,
                                final @NotNull LangConfiguration langConfiguration) {

        this.channelService = channelService;
        this.telegramBot = telegramBot;
        this.getUserInformMessageState = getUserInformMessageState;
        this.commandManager = commandManager;
        this.langConfiguration = langConfiguration;
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args,
                        final @NotNull UserEntity userEntity) {
        
        val executorId = String.valueOf(user.getId());
        val language = langConfiguration.getLanguage(userEntity.getLang());

        if (args.length < 1) {
            telegramBot.execute(new SendMessage(executorId, language.getString("need_chat_id")));
            return;
        }

        val channelId = args[0];

        if (!isNumeric(channelId)) {
            telegramBot.execute(new SendMessage(executorId, language.getString("incorrect_chat_id")));
            return;
        }

        val channel = channelService.getChannelById(channelId);

        if (channel == null) {
            telegramBot.execute(new SendMessage(executorId, language.getString("chat_not_found")));
            return;
        }

        telegramBot.execute(new SendMessage(executorId, language.getString("send_a_news")));

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
