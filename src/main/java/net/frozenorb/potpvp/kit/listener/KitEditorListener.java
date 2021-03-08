package net.frozenorb.potpvp.kit.listener;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

/**
 * "Modifications" needed to make the EditKitMenu work as expected
 */
public final class KitEditorListener implements Listener {

    @Getter private static final Map<UUID, KitType> playerEditKit = Maps.newHashMap();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Prevent continuing when player is not editing kit, or clicked block was null.
        if (!isEditingKit(player)) return;
        if (event.getClickedBlock() == null) return;

        event.setCancelled(true);
        if (event.getClickedBlock().getType() == Material.ANVIL) {
            new KitsMenu(playerEditKit.get(player.getUniqueId())).openMenu(player);
        }
        if (event.getClickedBlock().getType() == Material.CHEST) {
            KitType kitType = playerEditKit.get(player.getUniqueId());
            if (kitType.isEditorSpawnAllowed() && kitType.getEditorItems().length > 0) {
                new EditKitMenu(playerEditKit.get(player.getUniqueId())).openMenu(player);
            } else {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cThis kit has nothing to edit."));
            }
        }
        if (event.getClickedBlock().getState() instanceof Sign) {
            KitType kitType = playerEditKit.get(player.getUniqueId());
            Sign sign = (Sign) event.getClickedBlock().getState();

            for (String line : sign.getLines()) {
                if (line == null) {
                    continue;
                }
                if (ChatColor.stripColor(line).toLowerCase().contains("spawn")) {
                    playerEditKit.remove(player.getUniqueId());
                    PotPvPSI.getInstance().getLobbyHandler().returnToLobby(player);
                    break;
                }
                if (ChatColor.stripColor(line).toLowerCase().contains("load")) {
                    player.getInventory().setArmorContents(kitType.getDefaultArmor());
                    player.getInventory().setContents(kitType.getDefaultInventory());
                    player.updateInventory();

                    player.sendMessage(ChatColor.YELLOW + "Loaded default armor/inventory for " + kitType + ".");
                    break;
                }
            }


        }
    }

    public static boolean isEditingKit(Player player) {
        return playerEditKit.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Continue only if player is editing kit.
        if(!isEditingKit(event.getPlayer())) return;

        playerEditKit.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Continue only if player is editing kit.
        if(!isEditingKit(event.getPlayer())) return;

        event.getItemDrop().remove();
    }

    /**
     * Prevents placing items into the top inventory
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents all forms of dragging (the goal of this is
     * to prevent items being put into the top inventory,
     * but item dragging overall is too complicated to deal
     * with properly so we just disallow dragging.)
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

}