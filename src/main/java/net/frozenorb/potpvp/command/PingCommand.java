package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PingCommand {

    @Command(names = "ping", permission = "")
    public static void ping(Player sender, @Param(name = "target", defaultValue = "self") Player target) {
        int ping = PlayerUtils.getPing(target);

        sender.sendMessage(target.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.GREEN + ping + "ms");

        if (sender.getName().equalsIgnoreCase(target.getName())) {
            Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(sender);
            if (match != null) {
                for (MatchTeam team : match.getTeams()) {
                    for (UUID other : team.getAllMembers()) {
                        Player otherPlayer = Bukkit.getPlayer(other);

                        if (otherPlayer != null && !otherPlayer.equals(sender)) {
                            int otherPing = PlayerUtils.getPing(otherPlayer);
                            sender.sendMessage(otherPlayer.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.GREEN + otherPing + "ms");
                        }
                    }
                }
            }
        }
    }

}
