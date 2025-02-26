package net.frozenorb.potpvp.tournament;


import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Tournament {

    @Getter private int currentRound = -1;
    @Getter private int requiredPartiesToStart;

    @Getter private List<Party> activeParties = Lists.newArrayList();
    private List<Party> lost = Lists.newArrayList();

    @Getter private int requiredPartySize;
    @Getter private KitType type;

    @Getter private List<Match> matches = Lists.newArrayList();

    @Getter private int beginNextRoundIn = 31;

    // We do this because players can leave a party or the server during the tournament
    // We will need to ensure that at the end of the tournament we clear this
    // (or make sure the Tournament object is unreachable)
    private Map<UUID, Party> partyMap = Maps.newHashMap();

    @Getter private TournamentStage stage = TournamentStage.WAITING_FOR_TEAMS;

    @Getter private long roundStartedAt;

    public Tournament(KitType type, int partySize, int requiredPartiesToStart) {
        this.type = type;
        this.requiredPartySize = partySize;
        this.requiredPartiesToStart = requiredPartiesToStart;
    }

    public void addParty(Party party) {
        activeParties.add(party);
        checkActiveParties();
        joinedTournament(party);
        checkStart();
    }

    public boolean isInTournament(Party party) {
        return activeParties.contains(party);
    }

    public void check() {
        checkActiveParties();
        populatePartyMap();
        checkMatches();

        if (matches.stream().anyMatch(s -> s != null && s.getState() != MatchState.TERMINATED)) return; // We don't want to advance to the next round if any matches are ongoing
        matches.clear();

        if (currentRound == -1) return;

        if (activeParties.isEmpty()) {
            if (lost.isEmpty()) {
                stage = TournamentStage.FINISHED;
                PotPvPND.getInstance().getTournamentHandler().setTournament(null);
                return;
            }

            // shouldn't happen, meant that the two last parties disconnected at the last second
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &7The tournament's last two teams forfeited, &aWinner &7by default: &b" + PatchedPlayerUtils.getFormattedName(this.lost.get(this.lost.size() - 1).getLeader()) + "'s &7team!"));
            PotPvPND.getInstance().getTournamentHandler().setTournament(null); // Removes references to this tournament, will get cleaned up by GC
            stage = TournamentStage.FINISHED;
            return;
        }

        if (activeParties.size() == 1) {
            Party party = activeParties.get(0);
            if (party.getMembers().size() == 1) {
                this.repeatMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &b&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + " &awon the tournament!"), 4, 2);
            } else if (party.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = party.getMembers().iterator();
                UUID[] members = new UUID[]{membersIterator.next(), membersIterator.next()};
                this.repeatMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &b&l" + PatchedPlayerUtils.getFormattedName(members[0]) + " &7and &b&l" + PatchedPlayerUtils.getFormattedName(members[1]) + " &awon the tournament!"), 4, 2);
            } else {
                this.repeatMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &b&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + "&a's team won the tournament!"), 4, 2);
            }

            activeParties.clear();
            PotPvPND.getInstance().getTournamentHandler().setTournament(null);
            stage = TournamentStage.FINISHED;
            return;
        }

        if (--beginNextRoundIn >= 1) {
            switch (beginNextRoundIn) {
                case 30:
                case 15:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    if (currentRound == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &7The &b&ltournament &7will begin in &b" + this.beginNextRoundIn + " &7second" + (this.beginNextRoundIn == 1 ? "" : "s") + "."));
                    } else {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &b&lRound " + (this.currentRound + 1) + " &7will begin in &b" + this.beginNextRoundIn + " &7second" + (this.beginNextRoundIn == 1 ? "" : "s") + "."));
                    }
            }

            if (beginNextRoundIn == 30 && currentRound == 0) {
                Bukkit.broadcastMessage("&b[Tournament] " + ChatColor.RED.toString() + ChatColor.BOLD + "Only donators can join the tournament beyond this point!");
            }

            stage = TournamentStage.COUNTDOWN;
            return;
        }

        startRound();
    }

    private void checkActiveParties() {
        Set<UUID> realParties = PotPvPND.getInstance().getPartyHandler().getParties().stream().map(p -> p.getPartyId()).collect(Collectors.toSet());
        Iterator<Party> activePartyIterator = activeParties.iterator();
        while (activePartyIterator.hasNext()) {
            Party activeParty = activePartyIterator.next();
            if (!realParties.contains(activeParty.getPartyId())) {
                activePartyIterator.remove();

                if (!lost.contains(activeParty)) {
                    lost.add(activeParty);
                }
            }
        }
    }

    private void repeatMessage(String message, int times, int interval) {
        new BukkitRunnable() {

            private int runs = times;

            @Override
            public void run() {
                if (0 <= --runs) {
                    Bukkit.broadcastMessage(message);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(PotPvPND.getInstance(), 0, interval * 20);
    }

    public void checkStart() {
        if (activeParties.size() == requiredPartiesToStart) {
            start();
        }
    }

    public void start() {
        if (currentRound == -1) {
            currentRound = 0;
        }
    }

    private void joinedTournament(Party party) {
        broadcastJoinMessage(party);
    }

    private void populatePartyMap() {
        activeParties.forEach(p -> p.getMembers().forEach(u -> {
            partyMap.put(u, p);
        }));
    }

    private void startRound() {
        beginNextRoundIn = 31;
        // Next round has begun...

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &aRound " + ++this.currentRound + " has begun. Good luck!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &7Use &b/status &7to see who is fighting."));

        List<Party> oldPartyList = Lists.newArrayList(activeParties);
        // Collections.shuffle(oldPartyList);
        // Doing it this way will ensure that the tournament runs BUT if one party
        // disconnects every round, the bottom party could get to the final round without
        // winning a single duel. Could shuffle? But would remove the predictability & pseudo-bracket system
        while (1 < oldPartyList.size()) {
            Party firstParty = oldPartyList.remove(0);
            Party secondParty = oldPartyList.remove(0);

            matches.add(PotPvPND.getInstance().getMatchHandler().startMatch(ImmutableList.of(new MatchTeam(firstParty.getMembers()), new MatchTeam(secondParty.getMembers())), type, false, false));
        }

        if (oldPartyList.size() == 1) {
            oldPartyList.get(0).message("&b[Tournament] " + ChatColor.RED + "There were an odd number of teams in this round - so your team has advanced to the next round.");
        }

        stage = TournamentStage.IN_PROGRESS;
        roundStartedAt = System.currentTimeMillis();
    }

    private void checkMatches() {
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            if (match == null) {
                matchIterator.remove();
                continue;
            }

            if (match.getState() != MatchState.TERMINATED) continue;
            MatchTeam winner = match.getWinner();
            List<MatchTeam> losers = Lists.newArrayList(match.getTeams());
            losers.remove(winner);
            MatchTeam loser = losers.get(0);
            Party loserParty = partyMap.get(loser.getFirstMember());
            if (loserParty != null) {
                activeParties.remove(loserParty);
                broadcastEliminationMessage(loserParty);
                lost.add(loserParty);
                matchIterator.remove();
            }
        }
    }

    public void broadcastJoinMessage() {
        int teamSize = this.getRequiredPartySize();
        int requiredTeams = this.getRequiredPartiesToStart();

        int multiplier = teamSize < 3 ? teamSize : 1;

        if (this.getCurrentRound() != -1) return;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &7A &b&lTournament&7 has started. Type &b/join&7 to play. &b(" + this.activeParties.size() * multiplier + "/" + requiredTeams * multiplier + ")"));
        Bukkit.broadcastMessage("");
    }

    private void broadcastJoinMessage(Party joiningParty) {
        if (getCurrentRound() != -1) {
            // donor join
            FancyMessage message;
            if (joiningParty.getMembers().size() == 1) {
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7 &7has &7joined &7the &btournament&7. &7(" + this.activeParties.size() + "/" + this.requiredPartiesToStart + "&7)"));
            } else if (joiningParty.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 &7and &b" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 have joined the &btournament&7. &7(" + this.activeParties.size() * 2 + "/" + this.requiredPartiesToStart * 2 + "&7)"));
            } else {
                message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7's team has joined the &btournament&7. &7(" + this.activeParties.size() + "/" + this.requiredPartiesToStart + "&7)"));
            }

            message.tooltip(ChatColor.translateAlternateColorCodes('&', "&b[Tournament] &cDonators &7can join during the tournament countdown. Purchase a rank at &b " + "store.resolve.rip" + " &7."));
            Bukkit.getOnlinePlayers().forEach(message::send);
            return;
        }

        FancyMessage message;
        if (joiningParty.getMembers().size() == 1) {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e has joined the &etournament&e. &e(&c" + activeParties.size() + "&e/&c" + requiredPartiesToStart + "&e)"));
        } else if (joiningParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e and &a" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&e have joined the &etournament&e. &e(&c" + activeParties.size() * 2 + "&e/&c" + requiredPartiesToStart * 2 + "&e)"));
        } else {
            message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&e's team has joined the &etournament&e. &e(" + activeParties.size() + "&e/&c" + requiredPartiesToStart + "&e)"));
        }

        message.command("/djm");
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&c&lCLICK &eto hide this message."));

        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (joiningParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_JOIN_MESSAGE)) {
                message.send(player);
            }
        }
    }

    private void broadcastEliminationMessage(Party loserParty) {
        FancyMessage message;
        int multiplier = requiredPartySize < 3 ? requiredPartySize : 1;
        if (loserParty.getMembers().size() == 1) {
            message=new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&7 has been eliminated. &b(" + this.activeParties.size() * multiplier + "/" + this.requiredPartiesToStart * multiplier + "&b)"));
        } else if (loserParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = loserParty.getMembers().iterator();
            message=new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 and &a" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + " &7were eliminated. &b(" + this.activeParties.size() * multiplier + "/" + this.requiredPartiesToStart * multiplier + "&b)"));
        } else {
            message=new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&a" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&7's team has been eliminated. &b(" + this.activeParties.size() * multiplier + "/" + this.requiredPartiesToStart * multiplier + "&b)"));
        }

        message.command("/dem");
        message.tooltip(ChatColor.translateAlternateColorCodes('&', "&c&lCLICK &eto hide this message."));
        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loserParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES)) {
                message.send(player);
            }
        }
    }

    @Command(names = {"djm"}, permission = "")
    public static void joinMessages(Player sender) {
        boolean oldValue = PotPvPND.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE);
        if (!oldValue) {
            sender.sendMessage(ChatColor.RED + "You have already disabled tournament join messages.");
            return;
        }

        PotPvPND.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE, false);
        sender.sendMessage(ChatColor.GREEN + "Disabled tournament join messages.");
    }

    @Command(names = {"dem"}, permission = "")
    public static void eliminationMessages(Player sender) {
        boolean oldValue = PotPvPND.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES);
        if (!oldValue) {
            sender.sendMessage(ChatColor.RED + "You have already disabled tournament elimination messages.");
            return;
        }

        PotPvPND.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES, false);
        sender.sendMessage(ChatColor.GREEN + "Disabled tournament elimination messages.");
    }


    public enum TournamentStage {
        WAITING_FOR_TEAMS,
        COUNTDOWN,
        IN_PROGRESS,
        FINISHED
    }
}
