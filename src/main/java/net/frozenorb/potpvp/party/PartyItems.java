package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class PartyItems {
    public static final Material ICON_TYPE=Material.PAPER;
    public static final ItemStack LEAVE_PARTY_ITEM=new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ASSIGN_CLASSES=new ItemStack(Material.ITEM_FRAME);
    public static final ItemStack START_TEAM_EVENTS=new ItemStack(Material.GOLD_AXE);
    public static final ItemStack OTHER_PARTIES_ITEM=new ItemStack(Material.SKULL_ITEM);
    public static final ItemStack PARTY_SETTINGS=new ItemStack(Material.ANVIL);

    public static ItemStack icon(Party party) {
        ItemStack item=new ItemStack(ICON_TYPE);
        String displayName=ChatColor.AQUA.toString() + "Party Info";
        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

    private PartyItems() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        ItemUtils.setDisplayName(PARTY_SETTINGS, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.YELLOW + ChatColor.BOLD + "Party Settings" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Leave Party" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.YELLOW + ChatColor.BOLD + "HCF Kits" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(START_TEAM_EVENTS, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.YELLOW + ChatColor.BOLD + "Start Party Events" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Other Parties" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
    }
}

