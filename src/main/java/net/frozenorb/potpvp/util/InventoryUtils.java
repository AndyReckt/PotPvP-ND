package net.frozenorb.potpvp.util;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.lobby.LobbyUtils;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public final class InventoryUtils {

    public static final long RESET_DELAY_TICKS = 2L;

    public static void resetInventoryDelayed(Player player) {
        Runnable task = () -> resetInventoryNow(player);
        Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), task, RESET_DELAY_TICKS);
    }

    public static void resetInventoryNow(Player player) {
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            MatchUtils.resetInventory(player);
        } else {
            LobbyUtils.resetInventory(player);
        }
    }

}