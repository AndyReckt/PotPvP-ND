package net.frozenorb.potpvp.setting.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.setting.menu.SettingsMenu;
import org.bukkit.entity.Player;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public final class SettingsCommand {

    @Command(names = {"prefs", "options"})
    public static void prefs(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}