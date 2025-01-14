package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.kit.listener.KitEditorListener;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class FillHealPotionsButton extends Button {

    private final short durability;

    FillHealPotionsButton(short durability) {
        this.durability = durability;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.BLUE + "Fill Empty Space";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.LIGHT_PURPLE + "Fill your empty inventory space",
            ChatColor.LIGHT_PURPLE + "with Splash Health Potions."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    // getDamageValue is for the data, not durability
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack superItem = super.getButtonItem(player);

        superItem.setDurability(durability);
        short amountMatching = 1;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                amountMatching++;
            }
        }
        superItem.setAmount(amountMatching);
        return superItem;
    }

    @Override
    public void clicked(final Player player, int slot, ClickType clickType, InventoryView view) {
        ItemStack potion = new ItemStack(Material.POTION);

        potion.setDurability(durability);

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            player.getInventory().addItem(potion);
        }
        new EditKitMenu(KitEditorListener.getPlayerEditKit().get(player.getUniqueId())).openMenu(player);
    }

}