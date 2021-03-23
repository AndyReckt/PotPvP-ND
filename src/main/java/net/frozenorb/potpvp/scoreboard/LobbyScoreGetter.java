package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kt.util.TimeUtils;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueue;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.tournament.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, LinkedList<String>> {
    @Override
    public void accept(final Player player, final LinkedList<String> scores) {
        final MatchHandler matchHandler=PotPvPND.getInstance().getMatchHandler();
        final PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        final QueueHandler queueHandler=PotPvPND.getInstance().getQueueHandler();
        final EloHandler eloHandler=PotPvPND.getInstance().getEloHandler();
        final Tournament tournament=PotPvPND.getInstance().getTournamentHandler().getTournament();
        final Party playerParty=partyHandler.getParty(player);
        if (playerParty != null && tournament == null) {
            scores.add("&9Your Party: &f" + PotPvPND.getInstance().getUuidCache().name(playerParty.getLeader()));
        }
        scores.add("&fOnline" + ChatColor.GRAY + ": " + "&b" + PotPvPND.getInstance().getCache().getOnlineCount());
        scores.add("&fIn Queue" + ChatColor.GRAY + ": " + "&b" + PotPvPND.getInstance().getCache().getQueuesCount());
        scores.add("&fIn Fights" + ChatColor.GRAY + ": " + "&b" + PotPvPND.getInstance().getCache().getFightsCount());
        final MatchQueueEntry entry=this.getQueueEntry(player);
        if (entry != null) {
            final String waitTimeFormatted=TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            final MatchQueue queue=entry.getQueue();
            scores.add("&7&m--------------------");
            scores.add("&b" + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getName());
            scores.add("&fWaited: &b " + waitTimeFormatted);
            if (queue.isRanked()) {
                final int elo=eloHandler.getElo(entry.getMembers(), queue.getKitType());
                final int window=entry.getWaitSeconds() * 5;
                scores.add("&fRange: &b" + Math.max(0, elo - window) + " - " + (elo + window));
            }
        }
        if (tournament != null) {
            scores.add("");
            scores.add("&b&lTournament: &r");
            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                final int teamSize=tournament.getRequiredPartySize();
                scores.add("&fKit&7: &b" + tournament.getType().getName());
                scores.add("&fTeam Size&7: &b" + teamSize + " &7vs&b " + teamSize);
                final int multiplier=(teamSize < 3) ? teamSize : 1;
                scores.add("&f" + ((teamSize < 3) ? "Players" : "Teams") + "&7: &b" + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    scores.add("&b");
                    scores.add("&7Begins in &b" + tournament.getBeginNextRoundIn() + "&7 second" + ((tournament.getBeginNextRoundIn() == 1) ? "." : "s."));
                } else {
                    scores.add("&b");
                    scores.add("&b&lRound " + (tournament.getCurrentRound() + 1));
                    scores.add("&7Begins in &b" + tournament.getBeginNextRoundIn() + "&7 second" + ((tournament.getBeginNextRoundIn() == 1) ? "." : "s."));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                scores.add("&fRound&7: &b" + tournament.getCurrentRound());
                final int teamSize=tournament.getRequiredPartySize();
                final int multiplier=(teamSize < 3) ? teamSize : 1;
                scores.add("&f" + ((teamSize < 3) ? "Players" : "Teams") + "&7: &b" + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
                scores.add("&fDuration&7: &b" + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }
        }
    }

    private MatchQueueEntry getQueueEntry(final Player player) {
        final PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
        final QueueHandler queueHandler=PotPvPND.getInstance().getQueueHandler();
        final Party playerParty=partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        }
        return queueHandler.getQueueEntry(player.getUniqueId());
    }
}
