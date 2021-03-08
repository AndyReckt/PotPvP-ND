/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 */
package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

public class SendDuelButton
        extends Button {
    private final Set<String> maps;
    private final Callback<Set<String>> mapsCallback;

    @Override
    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player arg0) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player arg0) {
        return DyeColor.LIME.getWoolData();
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Send duel";
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (this.maps.size() < 2) {
            player.sendMessage(ChatColor.RED + "You must select at least two maps.");
            return;
        }
        this.mapsCallback.callback(this.maps);
    }

    @ConstructorProperties(value={"maps", "mapsCallback"})
    public SendDuelButton(Set<String> maps, Callback<Set<String>> mapsCallback) {
        this.maps=maps;
        this.mapsCallback=mapsCallback;
    }
}

