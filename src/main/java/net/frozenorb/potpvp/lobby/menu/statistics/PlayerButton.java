package net.frozenorb.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.util.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerButton extends Button {

    @Override
    public String getName(Player player) {
        return getColoredName(player) + ChatColor.WHITE + ChatColor.BOLD + " | " + ChatColor.WHITE + "Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                description.add(PotPvPND.getInstance().getDominantColor() + kitType.getName() + ChatColor.GRAY + ": " + PotPvPND.getInstance().getEloHandler().getElo(player, kitType));
            }
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        description.add(ChatColor.GREEN + "Global" + ChatColor.GRAY + ": " + PotPvPND.getInstance().getEloHandler().getGlobalElo(player.getUniqueId()));
        description.add(ChatColor.GREEN + "Division" + ChatColor.GRAY + ": " + PotPvPND.getInstance().getDivisionHandler().getDivision(player.getUniqueId()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return SkullCreator.itemFromUuid(player.getUniqueId()).getType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    private String getColoredName(Player player) {
        return player.getName();
    }
}
