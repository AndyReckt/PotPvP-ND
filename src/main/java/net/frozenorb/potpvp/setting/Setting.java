package net.frozenorb.potpvp.setting;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public enum Setting {

    ALLOW_SPECTATORS(
        ChatColor.AQUA + "Allow Spectators",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, players can spectate your",
            ChatColor.BLUE + "matches with /spectate.",
            "",
            ChatColor.BLUE + "Disable to disallow match spectators."
        ),
        3,
        Material.SKULL_ITEM,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    VIEW_OTHER_SPECTATORS(
        ChatColor.GRAY + "See Spectator",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you can see spectators",
            ChatColor.BLUE + "in the same match as you.",
            "",
            ChatColor.BLUE + "Disable to only see alive players in match."
        ),
        0,
        Material.SKULL_ITEM,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    RECEIVE_DUELS(
        ChatColor.DARK_PURPLE + "Duel Request",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you will be able to receive",
            ChatColor.BLUE + "duels from other players or parties.",
            "",
            ChatColor.BLUE + "Disable to not receive, but still send duels."
        ),
        0,
        Material.FIRE,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        "potpvp.toggleduels"
    ),
    PARTY_INVITE(
        ChatColor.YELLOW + "Toggle Party Invite",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, can receive party invite",
            "",
            ChatColor.BLUE + "Disable to can't receive party invite."
        ),
        0,
        Material.SLIME_BALL,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    VIEW_OTHERS_LIGHTNING(
        ChatColor.YELLOW + "Death Lightning",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, lightning will be visible",
            ChatColor.BLUE + "when other players die.",
            "",
            ChatColor.BLUE + "Disable to hide others lightning."
        ),
        0,
        Material.GLOWSTONE_DUST,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    SEE_TOURNAMENT_ELIMINATION_MESSAGES(
        ChatColor.RED + "Tournament Elimination Messages",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you will see messages when",
            ChatColor.BLUE + "people are eliminated the tournament",
            "",
            ChatColor.BLUE + "Disable to only see your own party elimination messages."
        ),
        0,
        Material.PUMPKIN,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    SEE_TOURNAMENT_JOIN_MESSAGE(
        ChatColor.DARK_AQUA + "Tournament Join Messages",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you will see messages",
            ChatColor.BLUE + "when people join the tournament",
            "",
            ChatColor.BLUE + "Disable to only see your own party join messages."
        ),
        0,
        Material.IRON_DOOR,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        null // no permission required
    ),
    SELECT_MAP(
        ChatColor.GOLD + "Select Arena",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you will be able to select",
            ChatColor.BLUE + "arenas when dueling players."
        ),
        0,
        Material.PAPER,
        ChatColor.GREEN + "Enable",
        ChatColor.RED + "Disable",
        true,
        "practice.map.select"
    );


    /**
     * Friendly (colored) display name for this setting
     */
    @Getter private final String name;

    /**
     * Friendly (colored) description for this setting
     */
    @Getter private final List<String> description;

    @Getter private int damage;

    /**
     * Material to be used when rendering an icon for this setting
     *
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final Material icon;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     *
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final String enabledText;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     *
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final String disabledText;

    /**
     * Default value for this setting, will be used for players who haven't
     * updated the setting and if a player's settings fail to load.
     */
    private final boolean defaultValue;

    /**
     * The permission required to be able to see and update this setting,
     * null means no permission is required to update/see.
     */
    private final String permission;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean canUpdate(Player player) {
        return permission == null || player.hasPermission(permission);
    }

}