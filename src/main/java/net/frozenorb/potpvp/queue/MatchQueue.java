package net.frozenorb.potpvp.queue;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MatchQueue {
    private final KitType kitType;
    private final boolean ranked;
    private final List<MatchQueueEntry> entries=new CopyOnWriteArrayList<>();

    MatchQueue(KitType kitType, boolean ranked) {
        this.kitType=Preconditions.checkNotNull(kitType, "kitType");
        this.ranked=ranked;
    }

    void tick() {
        ArrayList<MatchQueueEntry> entriesCopy=new ArrayList<>(this.entries);
        EloHandler eloHandler=PotPvPSI.getInstance().getEloHandler();
        if (this.ranked) {
            entriesCopy.sort(Comparator.comparing(e -> eloHandler.getElo(e.getMembers(), this.kitType)));
        }
        while (entriesCopy.size() >= 2) {
            MatchQueueEntry a=entriesCopy.remove(0);
            MatchQueueEntry b=entriesCopy.remove(0);
            if (this.ranked) {
                int aElo=eloHandler.getElo(a.getMembers(), this.kitType);
                int bElo=eloHandler.getElo(b.getMembers(), this.kitType);
                int aEloWindow=a.getWaitSeconds() * 5;
                int bEloWindow=b.getWaitSeconds() * 5;
                if (Math.abs(aElo - bElo) > Math.max(aEloWindow, bEloWindow)) continue;
            }
            this.createMatchAndRemoveEntries(a, b);
        }
    }

    public int countPlayersQueued() {
        int count=0;
        for ( MatchQueueEntry entry : this.entries ) {
            count+=entry.getMembers().size();
        }
        return count;
    }

    void addToQueue(MatchQueueEntry entry) {
        this.entries.add(entry);
    }

    void removeFromQueue(MatchQueueEntry entry) {
        this.entries.remove(entry);
    }

    private void createMatchAndRemoveEntries(MatchQueueEntry entryA, MatchQueueEntry entryB) {
        MatchTeam teamB;
        MatchHandler matchHandler=PotPvPSI.getInstance().getMatchHandler();
        QueueHandler queueHandler=PotPvPSI.getInstance().getQueueHandler();
        MatchTeam teamA=new MatchTeam(entryA.getMembers());
        Match match=matchHandler.startMatch(ImmutableList.of(teamA, teamB=new MatchTeam(entryB.getMembers())), this.kitType, this.ranked, !this.ranked);
        if (match != null) {
            queueHandler.removeFromQueueCache(entryA);
            queueHandler.removeFromQueueCache(entryB);
            String teamAElo="";
            String teamBElo="";
            if (this.ranked) {
                EloHandler eloHandler=PotPvPSI.getInstance().getEloHandler();
                teamAElo=" (" + eloHandler.getElo(teamA.getAliveMembers(), this.kitType) + ")";
                teamBElo=" (" + eloHandler.getElo(teamB.getAliveMembers(), this.kitType) + ")";
            }
            String foundStart=ChatColor.AQUA.toString() + ChatColor.BOLD + "Match found!" + ChatColor.WHITE + " Opponent: " + ChatColor.AQUA;
            teamA.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamB.getAllMembers())) + teamBElo);
            teamB.messageAlive(foundStart + Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(teamA.getAllMembers())) + teamAElo);
            this.entries.remove(entryA);
            this.entries.remove(entryB);
        }
    }

    public KitType getKitType() {
        return this.kitType;
    }

    public boolean isRanked() {
        return this.ranked;
    }
}

