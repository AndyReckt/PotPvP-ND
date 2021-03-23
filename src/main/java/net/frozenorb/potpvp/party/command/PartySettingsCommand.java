package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.menu.PartySettingsMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartySettingsCommand {
    @Command(names={"party settings", "p settings"})
    public static void menu(Player sender) {
        if (PotPvPND.getInstance().getPartyHandler().getParty(sender) == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a party!");
            if (!PotPvPND.getInstance().getPartyHandler().getParty(sender).isLeader(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not the leader of any party!");
            } else {
                new PartySettingsMenu().openMenu(sender);
            }
        }
    }
}

