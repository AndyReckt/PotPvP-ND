package net.frozenorb.potpvp.elo.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class EloSetCommands {

    @Command(names = {"elo setSolo"}, permission = "op")
    public static void eloSetSolo(Player sender, @Param(name = "target") String target, @Param(name = "kit type") KitType kitType, @Param(name = "new elo") int newElo) {
        EloHandler eloHandler = PotPvPND.getInstance().getEloHandler();
        UUID uuid = PotPvPND.getInstance().getUuidCache().uuid(target);
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name " + target + " found.");
            return;
        }

        eloHandler.setElo(uuid, kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + target + "'s " + kitType.getName() + " elo to " + newElo + ".");
    }

    @Command(names = {"elo setTeam"}, permission = "op")
    public static void eloSetTeam(Player sender, @Param(name = "target") Player target, @Param(name = "kit type") KitType kitType, @Param(name = "new elo") int newElo) {
        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        EloHandler eloHandler = PotPvPND.getInstance().getEloHandler();

        Party targetParty = partyHandler.getParty(target);

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        eloHandler.setElo(targetParty.getMembers(), kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + kitType.getName() + " elo of " + PotPvPND.getInstance().getUuidCache().name(targetParty.getLeader()) + "'s party to " + newElo + ".");
    }

}