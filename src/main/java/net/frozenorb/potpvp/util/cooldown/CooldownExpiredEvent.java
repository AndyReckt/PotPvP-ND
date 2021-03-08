package net.frozenorb.potpvp.util.cooldown;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class CooldownExpiredEvent extends PlayerBase {

    private final Cooldown cooldown;
    private boolean forced;

    CooldownExpiredEvent(Player player, Cooldown cooldown) {
        super(player);
        this.cooldown=cooldown;
    }

    public CustomEvent setForced(boolean forced) {
        this.forced=forced;
        return this;
    }
}
