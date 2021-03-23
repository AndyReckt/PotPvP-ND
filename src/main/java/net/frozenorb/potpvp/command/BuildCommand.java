package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class BuildCommand {

    @Command(names = {"build"}, permission = "op")
    public static void silent(Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", PotPvPND.getInstance());
            sender.sendMessage(ChatColor.RED + "Builder mode disabled.");
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(PotPvPND.getInstance(), true));
            sender.sendMessage(ChatColor.GREEN + "Builder mode enabled.");
        }
    }

}