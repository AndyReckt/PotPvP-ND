package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KitWipeKitsCommands {
    @Command(names={"kit wipeKits Type"}, permission="op")
    public static void kitWipeKitsType(Player sender, @Param(name="kit type") KitType kitType) {
        int modified=PotPvPND.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getName() + " kits.");
        sender.sendMessage(ChatColor.GRAY + "^ We would have a proper count here if we ran recent versions of MongoDB");
    }

    @Command(names={"kit wipeKits Player"}, permission="op")
    public static void kitWipeKitsPlayer(Player sender, @Param(name="target") UUID target) {
        PotPvPND.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + PotPvPND.getInstance().getUuidCache().name(target) + "'s kits.");
    }
}

