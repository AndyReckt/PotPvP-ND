package net.frozenorb.potpvp.lobby.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.duel.DuelInvite;
import net.frozenorb.potpvp.duel.PlayerDuelInvite;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.pagination.PaginatedMenu;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DuelSelectorMenu extends PaginatedMenu {

    List<PlayerDuelInvite> duelInvites;

    @NotNull
    @Override
    public String getPrePaginatedTitle(@NotNull Player player) {
        return "Duel Selector";
    }

    @NotNull
    @Override
    public Map<Integer, Button> getAllPagesButtons(@NotNull Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        for (DuelInvite duelInvite : duelInvites) {
            if (duelInvite instanceof PlayerDuelInvite) {
                PlayerDuelInvite playerDuelInvite = (PlayerDuelInvite) duelInvite;
                buttons.put(buttons.size(), new DuelButton(playerDuelInvite));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    public class DuelButton extends Button {
        PlayerDuelInvite duelInvite;

        @NotNull
        @Override
        public ItemStack getButtonItem(@NotNull Player player) {
            Player sender = Bukkit.getPlayer(duelInvite.getSender());
            return new ItemBuilder(Material.SKULL_ITEM)
                .name("&aDuel From " + sender.getDisplayName())
                .addToLore("&aLadder: &c" + duelInvite.getKitType().getDisplayName())
                .addToLore("&aMap:&c " + (duelInvite.getArena() == null ? "" : duelInvite.getArena().getName()))
                .data((short) 3)
                .build();
        }

        @Override
        public void clicked(@NotNull Player player, int slot, @NotNull ClickType clickType, @NotNull InventoryView view) {
            player.closeInventory();
            Player sender = Bukkit.getPlayer(duelInvite.getSender());
            player.performCommand("accept " + sender.getName());
        }
    }
}
