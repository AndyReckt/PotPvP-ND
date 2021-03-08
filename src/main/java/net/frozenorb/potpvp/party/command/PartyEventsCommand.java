package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.menu.PartyEventsMenu;
import org.bukkit.entity.Player;

public class PartyEventsCommand {
    @Command(names={"party events", "p events"}, permission="")
    public static void execute(Player sender) {
        new PartyEventsMenu().openMenu(sender);
    }
}
