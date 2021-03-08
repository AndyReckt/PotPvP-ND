package net.frozenorb.potpvp.queue;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class QueueItems {
    public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM;
    public static final ItemStack LEAVE_SOLO_QUEUE_ITEM;
    public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM;
    public static final ItemStack JOIN_PARTY_UNRANKED_QUEUE_ITEM;
    public static final ItemStack LEAVE_PARTY_UNRANKED_QUEUE_ITEM;
    public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM;
    public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM;

    static {
        JOIN_SOLO_UNRANKED_QUEUE_ITEM=new ItemStack(Material.IRON_SWORD);
        LEAVE_SOLO_QUEUE_ITEM=new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
        JOIN_SOLO_RANKED_QUEUE_ITEM=new ItemStack(Material.DIAMOND_SWORD);
        JOIN_PARTY_UNRANKED_QUEUE_ITEM=new ItemStack(Material.IRON_SWORD);
        LEAVE_PARTY_UNRANKED_QUEUE_ITEM=new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
        JOIN_PARTY_RANKED_QUEUE_ITEM=new ItemStack(Material.DIAMOND_SWORD);
        LEAVE_PARTY_RANKED_QUEUE_ITEM=new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
        ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Join " + ChatColor.GRAY + ChatColor.BOLD + "Unranked" + ChatColor.GREEN + ChatColor.BOLD + " Queue" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_SOLO_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Leave Queue" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Join " + ChatColor.AQUA + ChatColor.BOLD + "Ranked" + ChatColor.GREEN + ChatColor.BOLD + " Queue" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Play 2v2 Unranked" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Leave 2v2 Unranked" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Play 2v2 Ranked" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Leave 2v2 Ranked" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
    }

    private QueueItems() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

