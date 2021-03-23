package net.frozenorb.potpvp.duel.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.arena.menu.select.SelectArenaMenu;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.duel.DuelInvite;
import net.frozenorb.potpvp.duel.PartyDuelInvite;
import net.frozenorb.potpvp.duel.PlayerDuelInvite;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class DuelCommand {

    @Command(names = {"duel", "1v1"})
    public static void duel(Player sender, @Param(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't duel yourself!");
            return;
        }

        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = PotPvPND.getInstance().getLobbyHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            // party dueling party (legal)
            if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                // reassign these fields so that any party changes
                // (kicks, etc) are reflectednow
                Party newSenderParty = partyHandler.getParty(sender);
                Party newTargetParty = partyHandler.getParty(target);

                if (newSenderParty != null && newTargetParty != null) {
                    if (newSenderParty.isLeader(sender.getUniqueId())) {
                        duel(sender, newSenderParty, newTargetParty, kitType);
                    } else {
                        sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
                    }
                }
            }, "Select a kit type...").openMenu(sender);
        } else if (senderParty == null && targetParty == null) {
            // player dueling player (legal)
            if (!PotPvPValidation.canSendDuel(sender, target)) {
                return;
            }

            if (target.hasPermission("potpvp.famous") && System.currentTimeMillis() - lobbyHandler.getLastLobbyTime(target) < 3_000) {
                sender.sendMessage(ChatColor.RED + target.getName() + " just returned to the lobby, please wait a moment.");
                return;
            }

            SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();
            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                if (sender.hasPermission("practice.map.select")
                    && settingHandler.getSetting(sender, Setting.SELECT_MAP)) {
                    new SelectArenaMenu(kitType, arena -> duel(sender, target, kitType), "Select an arena...").openMenu(sender);
                } else {
                    duel(sender, target, kitType);
                }
            }, "Select a kit type...").openMenu(sender);
        } else if (senderParty == null) {
            // player dueling party (illegal)
            sender.sendMessage(ChatColor.RED + "You must create a party to duel " + target.getName() + "'s party.");
        } else {
            // party dueling player (illegal)
            sender.sendMessage(ChatColor.RED + "You must leave your party to duel " + target.getName() + ".");
        }
    }

    public static void duel(Player sender, Player target, KitType kitType) {
        if (!PotPvPValidation.canSendDuel(sender, target)) {
            return;
        }

        DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
        target.spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target, kitType));
    }

    public static void duel(Player sender, Player target, KitType kitType, ArenaSchematic arena) {
        if (!PotPvPValidation.canSendDuel(sender, target)) {
            return;
        }

        DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel on arena " + ChatColor.GREEN + arena.getName() + ".");
        target.spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target, kitType, arena));
    }


    public static void duel(Player sender, Party senderParty, Party targetParty, KitType kitType) {
        if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        DuelHandler duelHandler = PotPvPND.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(targetParty, senderParty);
        String targetPartyLeader = PotPvPND.getInstance().getUuidCache().name(targetParty.getLeader());

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, Bukkit.getPlayer(targetParty.getLeader()));
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(senderParty, targetParty);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        targetParty.message(ChatColor.AQUA + sender.getName() + "'s Party (" + senderParty.getMembers().size() + ")" + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
        Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, kitType));
    }

    private static TextComponent[] createInviteNotification(String sender) {
        TextComponent firstPart = new TextComponent("Click here ");
        TextComponent secondText = new TextComponent("or type ");
        TextComponent commandPart = new TextComponent("/accept " + sender);
        TextComponent secondPart = new TextComponent(" to accept the duel request");

        firstPart.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        secondText.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        commandPart.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        secondPart.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click here to accept")}));

        secondText.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        secondText.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click here to accept")}));

        commandPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        commandPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click here to accept")}));

        secondPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        secondPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click here to accept")}));

        return new TextComponent[]{firstPart, commandPart, secondPart};
    }


}