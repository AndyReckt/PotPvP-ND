/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDeleteCommand {
    @Command(names={"kittype delete"}, permission="op", description="Deletes an existing kit-type")
    public static void execute(Player player, @Param(name="kittype") KitType kitType) {
        kitType.deleteAsync();
        KitType.getAllTypes().remove(kitType);
        PotPvPND.getInstance().getQueueHandler().removeQueues(kitType);
        player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
    }
}

