package net.frozenorb.potpvp.party.command;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyAccessRestriction;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyAnnounceCommand {

    @Command(names = {"party announce", "p announce", "t announce", "team announce", "f announce"}, permission = "potpvp.party.announce")
    public static void announce(Player sender) {
        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            return;
        }
        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }

        if (PotPvPND.getInstance().getAnnounceCooldown().isOnCooldown(sender)) {
            sender.sendMessage(ChatColor.RED + "You must wait to use that command again.");
            return;
        }

        if (party.getAccessRestriction() != PartyAccessRestriction.Public) {
            party.setAccessRestriction(PartyAccessRestriction.Public);
            sender.sendMessage(CC.translate(
                "&eYour party is now &aopen&e."));
        }

        FancyMessage clickable = new FancyMessage(sender.getName() +
            " &ais hosting a public party type &a'&c/p join " + sender.getName() + "&a' &aor &cclick here&a to join");
        clickable.tooltip("Click here to join the party!");
        clickable.command("/p join" + sender.getName());

        if (!sender.hasPermission("potpvp.bypass")) {
            PotPvPND.getInstance().getAnnounceCooldown().setCooldown(sender);
        }

        Bukkit.getOnlinePlayers().forEach(clickable::send);
    }

}
