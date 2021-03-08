package net.frozenorb.potpvp.lobby;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.COMPASS);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack OPEN_SETTINGS_MENU = new ItemStack(Material.WATCH);
    public static final ItemStack MANAGE_ITEM = new ItemStack(Material.BEACON);
    public static final ItemStack ENABLE_SPEC_MODE_ITEM = new ItemStack(Material.REDSTONE_TORCH_ON);
    public static final ItemStack DISABLE_SPEC_MODE_ITEM = new ItemStack(Material.LEVER);
    public static final ItemStack SPECTATE_RANDOM_RANKED = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack UNFOLLOW_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack PLAYER_STATISTICS = new ItemStack(Material.PAPER, 1);

    static {
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.YELLOW + ChatColor.BOLD + "Spectate Random Match" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.GREEN + ChatColor.BOLD + "Spectate Menu" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(OPEN_SETTINGS_MENU, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.AQUA + ChatColor.BOLD + "Settings Menu" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(ENABLE_SPEC_MODE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.AQUA + ChatColor.BOLD + "Enable Spectator Mode" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(DISABLE_SPEC_MODE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.AQUA + ChatColor.BOLD + "Disable Spectator Mode" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(MANAGE_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Manage PotPvP" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(UNFOLLOW_ITEM, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.RED + ChatColor.BOLD + "Stop Following" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(SPECTATE_RANDOM_RANKED, ChatColor.BLUE.toString() + ChatColor.BOLD + "» " + ChatColor.AQUA + ChatColor.BOLD + "Spectate Random Ranked" + ChatColor.BLUE.toString() + ChatColor.BOLD + " «");
        ItemUtils.setDisplayName(PLAYER_STATISTICS, PotPvPLang.LEFT_ARROW + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Statistics" + PotPvPLang.RIGHT_ARROW);
    }

}