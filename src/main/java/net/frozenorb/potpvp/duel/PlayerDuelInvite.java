package net.frozenorb.potpvp.duel;

import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kittype.KitType;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, KitType kitType) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
    }

    public PlayerDuelInvite(Player sender, Player target, KitType kitType, ArenaSchematic arena) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType, arena);
    }

}