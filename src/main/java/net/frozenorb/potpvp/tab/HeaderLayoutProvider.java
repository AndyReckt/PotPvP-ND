package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {
    @Override
    public void accept(Player player, TabLayout tabLayout) {
        tabLayout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());
        tabLayout.set(1, 0, "&9&lPractice");
        tabLayout.set(2, 1, ChatColor.GRAY + "In Fights: " + PotPvPND.getInstance().getCache().getFightsCount());
        tabLayout.set(1, 1, ChatColor.GRAY + "Your Connection", Math.max((PlayerUtils.getPing(player) + 5) / 10 * 10, 1));
    }
}
