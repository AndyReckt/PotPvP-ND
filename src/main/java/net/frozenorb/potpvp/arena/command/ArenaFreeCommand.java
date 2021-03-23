package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaFreeCommand {

    @Command(names = {"arena free"}, permission = "op")
    public static void arenaFree(Player sender) {
        PotPvPND.getInstance().getArenaHandler().getGrid().free();
        sender.sendMessage(ChatColor.GREEN + "Arena grid has been freed.");
    }

}