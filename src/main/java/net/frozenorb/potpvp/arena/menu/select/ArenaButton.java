package net.frozenorb.potpvp.arena.menu.select;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

@AllArgsConstructor
public class ArenaButton extends Button {

    private final String mapName;
    private final Callback<ArenaSchematic> mapCallback;

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + mapName;
    }

    @Override
    public Material getMaterial(Player player) {
        return PotPvPND.getInstance().getArenaHandler()
            .getSchematic(mapName).getArenaItem();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        player.closeInventory();
        mapCallback.callback(PotPvPND.getInstance().getArenaHandler().getSchematic(mapName));
    }
}
