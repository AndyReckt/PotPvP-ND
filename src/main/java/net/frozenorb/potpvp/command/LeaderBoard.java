package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.lobby.menu.StatisticsMenu;
import org.bukkit.entity.Player;

public final class LeaderBoard {
    @Command(names={"leaderboard", "stats"})
    public static void menu(Player sender) {
        new StatisticsMenu().openMenu(sender);
    }
}