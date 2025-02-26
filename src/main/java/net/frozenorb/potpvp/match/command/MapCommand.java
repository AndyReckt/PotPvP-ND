package net.frozenorb.potpvp.match.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MapCommand {

    @Command(names = {"map"}, permission = "")
    public static void map(Player sender) {
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(sender);

        if (match == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a match.");
            return;
        }

        Arena arena = match.getArena();
        sender.sendMessage(ChatColor.YELLOW + "Playing on copy " + ChatColor.GOLD + arena.getCopy() + ChatColor.YELLOW + " of " + ChatColor.GOLD + arena.getSchematic() + ChatColor.YELLOW + ".");
    }

}