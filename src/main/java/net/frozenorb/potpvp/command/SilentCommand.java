package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class SilentCommand {
    @Command(names={"silent"}, permission="potpvp.staff")
    public static void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", PotPvPND.getInstance());
            sender.removeMetadata("invisible", PotPvPND.getInstance());
            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPND.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(PotPvPND.getInstance(), true));
            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }
        VisibilityUtils.updateVisibility(sender);
    }
}