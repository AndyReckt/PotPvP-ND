/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.kittype;

import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class KitTypeParameterType
        implements ParameterType<KitType> {
    @Override
    public KitType transform(CommandSender sender, String source) {
        for ( KitType kitType : KitType.getAllTypes() ) {
            if (!sender.isOp() && kitType.isHidden() || !kitType.getId().equalsIgnoreCase(source)) continue;
            return kitType;
        }
        sender.sendMessage(ChatColor.RED + "No kit type with the name " + source + " found.");
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        ArrayList<String> completions=new ArrayList<String>();
        for ( KitType kitType : KitType.getAllTypes() ) {
            if (!player.isOp() && kitType.isHidden() || !StringUtils.startsWithIgnoreCase(kitType.getId(), source))
                continue;
            completions.add(kitType.getId());
        }
        return completions;
    }
}

