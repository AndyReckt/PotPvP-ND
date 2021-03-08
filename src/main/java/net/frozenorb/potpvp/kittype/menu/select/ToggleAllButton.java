/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 */
package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

public class ToggleAllButton
        extends Button {
    private final Set<String> allMaps;
    private final Set<String> maps;

    @Override
    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player arg0) {
        return this.maps.isEmpty() ? Material.REDSTONE_TORCH_ON : Material.REDSTONE_TORCH_OFF;
    }

    @Override
    public String getName(Player arg0) {
        return this.maps.isEmpty() ? ChatColor.GREEN + "Enable all maps" : ChatColor.RED + "Disable all maps";
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (this.maps.isEmpty()) {
            this.maps.addAll(this.allMaps);
        } else {
            this.maps.clear();
        }
    }

    @ConstructorProperties(value={"allMaps", "maps"})
    public ToggleAllButton(Set<String> allMaps, Set<String> maps) {
        this.allMaps=allMaps;
        this.maps=maps;
    }
}

