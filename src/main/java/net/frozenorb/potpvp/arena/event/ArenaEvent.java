package net.frozenorb.potpvp.arena.event;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.frozenorb.potpvp.arena.Arena;
import org.bukkit.event.Event;

/**
 * Represents an event involving an {@link Arena}
 */
abstract class ArenaEvent extends Event {

    /**
     * The match involved in this event
     */
    @Getter private final Arena arena;

    ArenaEvent(Arena arena) {
        this.arena = Preconditions.checkNotNull(arena, "arena");
    }

}