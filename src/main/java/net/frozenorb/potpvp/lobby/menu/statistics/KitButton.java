package net.frozenorb.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class KitButton extends Button {

    private KitType kitType;

    public KitButton(KitType kitType) {
        this.kitType = kitType;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + kitType.getDisplayName() + ChatColor.GRAY.toString() + ChatColor.BOLD + " | " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : PotPvPSI.getInstance().getEloHandler().topElo(kitType).entrySet()) {
            String color = (counter <= 3 ? ChatColor.GREEN : ChatColor.GRAY).toString();
            description.add(color + counter + ChatColor.GRAY.toString() + ChatColor.BOLD + " | " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }
}
