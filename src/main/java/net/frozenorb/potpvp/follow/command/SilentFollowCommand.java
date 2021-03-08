package net.frozenorb.potpvp.follow.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.command.LeaveCommand;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SilentFollowCommand {

    @Command(names = {"silentfollow", "tp"}, permission = "potpvp.silent")
    public static void silentfollow(Player sender, @Param(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You can't use this command while on a match.");
            return;
        }
        VisibilityUtils.updateVisibility(sender);
        if (PotPvPSI.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
