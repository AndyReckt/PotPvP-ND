package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ArenaSetItemCommand {
    @Command(names = {"arena setitem"}, permission = "op")
    public static void setArenaItem(Player sender, @Param(name = "schematic") String schematicName, @Param(name = "item") int itemId) {
        ArenaHandler arenaHandler = PotPvPND.getInstance().getArenaHandler();

        if (arenaHandler.getSchematic(schematicName) == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " doesn't exist!");
            return;
        }

        if (Material.getMaterial(itemId) == null) {
            sender.sendMessage(ChatColor.RED + "Item " + itemId + " doesn't exist!");
            return;
        }

        arenaHandler.getSchematic(schematicName)
            .setArenaItem(Material.getMaterial(itemId));

        try {
            arenaHandler.saveSchematics();
            sender.sendMessage(ChatColor.GREEN + "Item updated");
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Item could not be updated");
        }
    }
}
