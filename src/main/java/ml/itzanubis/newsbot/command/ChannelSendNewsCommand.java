package ml.itzanubis.newsbot.command;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.entity.UserEntity;
import ml.itzanubis.newsbot.lang.LangConfiguration;
import ml.itzanubis.newsbot.state.GetUserPostTextState;
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
public class ChannelSendNewsCommand implements CommandExecutor {
    private final ChannelService channelService;

    private final CommandManager commandManager;

    private final TelegramBot bot;

    private final GetUserPostTextState getUserPostTextState;

    private final LangConfiguration langConfiguration;

    @PostConstruct
    private void init() {
        commandManager.createCommand("/post", this);
    }

    @Autowired
    public ChannelSendNewsCommand(ChannelService channelService,
                                  CommandManager commandManager,
                                  TelegramBot bot,
                                  GetUserPostTextState getUserPostTextState,
                                  LangConfiguration langConfiguration) {

        this.channelService = channelService;
        this.commandManager = commandManager;
        this.bot = bot;
        this.getUserPostTextState = getUserPostTextState;
        this.langConfiguration = langConfiguration;
    }

    @Override
    @SneakyThrows
    public void execute(final @NotNull Message message,
                        final @NotNull User user,
                        final @NotNull Chat chat,
                        final @NotNull String[] args,
                        final @NotNull UserEntity userEntity) {

        val userId = String.valueOf(user.getId());
        val channelEntity = channelService.getChannel(userId);
        val language = langConfiguration.getLanguage(userEntity.getLang());

        if (channelEntity == null) {
            bot.execute(new SendMessage(userId, language.getString("dont_having_channels")));
            return;
        }

        val channelId = String.valueOf(channelEntity.getId());

        if (!channelService.isAdmin(channelId)) {
            bot.execute(new SendMessage(userId, language.getString("bot_is_not_admin")));
            return;
        }

        FieldStateMachine.createState(user, getUserPostTextState);
        bot.execute(new SendMessage(userId, language.getString("send_a_news")));
    }
}
