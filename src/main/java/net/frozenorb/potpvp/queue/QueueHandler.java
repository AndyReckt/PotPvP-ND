package net.frozenorb.potpvp.queue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.listener.QueueGeneralListener;
import net.frozenorb.potpvp.queue.listener.QueueItemListener;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class QueueHandler {
    public static final int RANKED_WINDOW_GROWTH_PER_SECOND=5;
    private static final String JOIN_SOLO_MESSAGE=ChatColor.GREEN + "You are now queued for %s %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_SOLO_MESSAGE=ChatColor.GREEN + "You are no longer queued for %s %s" + ChatColor.GREEN + ".";
    private static final String JOIN_PARTY_MESSAGE=ChatColor.GREEN + "Your party is now queued for %s %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_PARTY_MESSAGE=ChatColor.GREEN + "Your party is no longer queued for %s %s" + ChatColor.GREEN + ".";
    private final Table<KitType, Boolean, MatchQueue> soloQueues=HashBasedTable.create();
    private final Table<KitType, Boolean, MatchQueue> partyQueues=HashBasedTable.create();
    private final Map<UUID, SoloMatchQueueEntry> soloQueueCache=new ConcurrentHashMap<>();
    private final Map<Party, PartyMatchQueueEntry> partyQueueCache=new ConcurrentHashMap<Party, PartyMatchQueueEntry>();
    private int queuedCount=0;

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueGeneralListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), PotPvPSI.getInstance());
        for ( KitType kitType : KitType.getAllTypes() ) {
            this.soloQueues.put(kitType, true, new MatchQueue(kitType, true));
            this.soloQueues.put(kitType, false, new MatchQueue(kitType, false));
            this.partyQueues.put(kitType, true, new MatchQueue(kitType, true));
            this.partyQueues.put(kitType, false, new MatchQueue(kitType, false));
        }
        Bukkit.getScheduler().runTaskTimer(PotPvPSI.getInstance(), () -> {
            this.soloQueues.values().forEach(MatchQueue::tick);
            this.partyQueues.values().forEach(MatchQueue::tick);
            int i=0;
            for ( MatchQueue queue : this.soloQueues.values() ) {
                i+=queue.countPlayersQueued();
            }
            for ( MatchQueue queue : this.partyQueues.values() ) {
                i+=queue.countPlayersQueued();
            }
            this.queuedCount=i;
        }, 20L, 20L);
    }

    public void addQueues(KitType kitType) {
        this.soloQueues.put(kitType, true, new MatchQueue(kitType, true));
        this.soloQueues.put(kitType, false, new MatchQueue(kitType, false));
        this.partyQueues.put(kitType, true, new MatchQueue(kitType, true));
        this.partyQueues.put(kitType, false, new MatchQueue(kitType, false));
    }

    public void removeQueues(KitType kitType) {
        this.soloQueues.remove(kitType, true);
        this.soloQueues.remove(kitType, false);
        this.partyQueues.remove(kitType, true);
        this.partyQueues.remove(kitType, false);
    }

    public int countPlayersQueued(KitType kitType, boolean ranked) {
        return this.soloQueues.get(kitType, ranked).countPlayersQueued() + this.partyQueues.get(kitType, ranked).countPlayersQueued();
    }

    public boolean joinQueue(Player player, KitType kitType, boolean ranked) {
        if (!PotPvPValidation.canJoinQueue(player)) {
            return false;
        }
        MatchQueue queue=this.soloQueues.get(kitType, ranked);
        SoloMatchQueueEntry entry=new SoloMatchQueueEntry(queue, player.getUniqueId());
        queue.addToQueue(entry);
        this.soloQueueCache.put(player.getUniqueId(), entry);
        player.sendMessage(String.format(JOIN_SOLO_MESSAGE, ranked ? "Ranked" : "Unranked", kitType.getColoredDisplayName()));
        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean leaveQueue(Player player, boolean silent) {
        SoloMatchQueueEntry entry=this.getQueueEntry(player.getUniqueId());
        if (entry == null) {
            return false;
        }
        MatchQueue queue=entry.getQueue();
        queue.removeFromQueue(entry);
        this.soloQueueCache.remove(player.getUniqueId());
        if (!silent) {
            player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, queue.isRanked() ? "Ranked" : "Unranked", queue.getKitType().getColoredDisplayName()));
        }
        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean joinQueue(Party party, KitType kitType, boolean ranked) {
        if (!PotPvPValidation.canJoinQueue(party)) {
            return false;
        }
        MatchQueue queue=this.partyQueues.get(kitType, ranked);
        PartyMatchQueueEntry entry=new PartyMatchQueueEntry(queue, party);
        queue.addToQueue(entry);
        this.partyQueueCache.put(party, entry);
        party.message(String.format(JOIN_PARTY_MESSAGE, ranked ? "Ranked" : "Unranked", kitType.getColoredDisplayName()));
        party.resetInventoriesDelayed();
        return true;
    }

    public boolean leaveQueue(Party party, boolean silent) {
        PartyMatchQueueEntry entry=this.getQueueEntry(party);
        if (entry == null) {
            return false;
        }
        MatchQueue queue=entry.getQueue();
        queue.removeFromQueue(entry);
        this.partyQueueCache.remove(party);
        if (!silent) {
            party.message(String.format(LEAVE_PARTY_MESSAGE, queue.isRanked() ? "Ranked" : "Unranked", queue.getKitType().getColoredDisplayName()));
        }
        party.resetInventoriesDelayed();
        return true;
    }

    public boolean isQueued(UUID player) {
        return this.soloQueueCache.containsKey(player);
    }

    public boolean isQueuedRanked(UUID player) {
        SoloMatchQueueEntry entry=this.getQueueEntry(player);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(UUID player) {
        SoloMatchQueueEntry entry=this.getQueueEntry(player);
        return entry != null && !entry.getQueue().isRanked();
    }

    public SoloMatchQueueEntry getQueueEntry(UUID player) {
        return this.soloQueueCache.get(player);
    }

    public boolean isQueued(Party party) {
        return this.partyQueueCache.containsKey(party);
    }

    public boolean isQueuedRanked(Party party) {
        PartyMatchQueueEntry entry=this.getQueueEntry(party);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(Party party) {
        PartyMatchQueueEntry entry=this.getQueueEntry(party);
        return entry != null && !entry.getQueue().isRanked();
    }

    public PartyMatchQueueEntry getQueueEntry(Party party) {
        return this.partyQueueCache.get(party);
    }

    void removeFromQueueCache(MatchQueueEntry entry) {
        if (entry instanceof SoloMatchQueueEntry) {
            this.soloQueueCache.remove(((SoloMatchQueueEntry) entry).getPlayer());
        } else if (entry instanceof PartyMatchQueueEntry) {
            this.partyQueueCache.remove(((PartyMatchQueueEntry) entry).getParty());
        }
    }

    public int getQueuedCount() {
        return this.queuedCount;
    }
}

