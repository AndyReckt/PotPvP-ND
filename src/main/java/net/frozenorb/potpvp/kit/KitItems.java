package net.frozenorb.potpvp.kit;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class KitItems {
    public static final ItemStack OPEN_EDITOR_ITEM;

    private KitItems() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        OPEN_EDITOR_ITEM=new ItemStack(Material.BOOK);
        ItemUtils.setDisplayName(OPEN_EDITOR_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.YELLOW + ChatColor.BOLD + "Kit Editor" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
    }
}


