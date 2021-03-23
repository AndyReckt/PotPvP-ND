package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MatchStatusCommand {

    @Command(names = {"match status"}, permission = "")
    public static void matchStatus(CommandSender sender, @Param(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(target);

        if (match == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not playing or is spectating a match.");
            return;
        }

        for (String line : PotPvPND.getGson().toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }

}