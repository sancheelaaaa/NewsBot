package ml.itzanubis.newsbot.repository;

import ml.itzanubis.newsbot.entity.ChannelEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SuppressWarnings("ALL")
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    @Override
    Optional<ChannelEntity> findById(final @NotNull Long id);

    Optional<ChannelEntity> findByName(final @NotNull String name);
}
