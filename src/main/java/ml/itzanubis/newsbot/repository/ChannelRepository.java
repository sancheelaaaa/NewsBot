package ml.itzanubis.newsbot.repository;

import ml.itzanubis.newsbot.entity.Channel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SuppressWarnings("ALL")
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Override
    Optional<Channel> findById(final @NotNull Long id);

    Optional<Channel> findByName(final @NotNull String name);
}
