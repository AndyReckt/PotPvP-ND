package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.UUID;

public final class SoloMatchQueueEntry
        extends MatchQueueEntry {
    private final UUID player;

    SoloMatchQueueEntry(MatchQueue queue, UUID player) {
        super(queue);
        this.player=Preconditions.checkNotNull(player, "player");
    }

    @Override
    public Set<UUID> getMembers() {
        return ImmutableSet.of(this.player);
    }

    public UUID getPlayer() {
        return this.player;
    }
}

