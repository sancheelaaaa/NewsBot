package ml.itzanubis.newsbot.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.val;
import ml.itzanubis.newsbot.TelegramBot;
import ml.itzanubis.newsbot.config.TelegramBotConfiguration;
import ml.itzanubis.newsbot.entity.ChannelEntity;
import ml.itzanubis.newsbot.repository.ChannelRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ChannelService {

    private static final Map<String, ChannelEntity> channels = new HashMap<>();

    private final ChannelRepository repository;

    private final Logger logger = TelegramBot.getLogger();

    private final TelegramBot bot;

    private final TelegramBotConfiguration configuration;

    @Autowired
    private ChannelService(ChannelRepository repository, TelegramBot telegramBot, TelegramBotConfiguration configuration) {
        this.repository = repository;
        this.bot = telegramBot;
        this.configuration = configuration;
    }

    @PostConstruct
    private void init() {
        repository.findAll().forEach(channel -> channels.put(channel.getUserId(), channel));
    }

    public Collection<ChannelEntity> collectAll() {
        return channels.values();
    }

    public void createChannel(final @NotNull String userId, final @NotNull ChannelEntity channel) {
        logger.info("Creating a new channel for a user: " + userId);

        repository.save(channel);

        channels.put(userId, channel);
    }

    public void deleteChannel(final @NotNull ChannelEntity channel) {
        logger.info("Deleting a channel: " + channel.getName());

        repository.delete(channel);

        channels.values().remove(channel);
    }

    public ChannelEntity getChannelById(String channelId) {
        return repository.findById(Long.valueOf(channelId)).orElse(null);
    }

    public ChannelEntity getChannel(final @NotNull String userId) {
        val channel = channels.get(userId);

        if (channel == null) {
            logger.error("Don't have any channels for: " + userId);
            return null;
        }

        return channel;
    }

    @SneakyThrows
    public boolean isAdmin(final @NotNull String chatId) {
        val admins = bot.execute(new GetChatAdministrators(chatId));

        val isBotAdmin = admins.stream().filter(
                chatMember -> Objects.equals(chatMember.getUser().getUserName(), configuration.getUsername())
        );

        return !isBotAdmin.toList().isEmpty();
    }

}
