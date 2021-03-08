package net.frozenorb.potpvp.rematch;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GREEN;

@UtilityClass
public final class RematchItems {

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.DIAMOND);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_DUEL_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack DUEL_SELECTOR = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

    static {
        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, DARK_PURPLE + "Request Rematch");
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, GREEN + "Sent Rematch");
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, GREEN + "Accept Rematch");
        ItemUtils.setDisplayName(DUEL_SELECTOR, GREEN + "Duel Selector");
        ItemUtils.setDisplayName(ACCEPT_DUEL_ITEM, GREEN + "Accept ");
    }

}