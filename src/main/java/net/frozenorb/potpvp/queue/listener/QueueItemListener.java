package net.frozenorb.potpvp.queue.listener;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.CustomSelectKitTypeMenu;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class QueueItemListener
        extends ItemListener {
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked=this.selectionMenuAddition(true);
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked=this.selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler=queueHandler;
        this.addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, this.joinSoloConsumer(false));
        this.addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, this.joinSoloConsumer(true));
        this.addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, this.joinPartyConsumer(false));
        this.addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, this.joinPartyConsumer(true));
        this.addHandler(QueueItems.LEAVE_SOLO_QUEUE_ITEM, player -> PotPvPND.getInstance().getQueueHandler().leaveQueue(player, false));
        Consumer<Player> leaveQueuePartyConsumer=player -> {
            Party party=PotPvPND.getInstance().getPartyHandler().getParty(player);
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };
        this.addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
        this.addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked && !RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                int needed=RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You need " + needed + " more wins to join ranked queue!");
                return;
            }
            if (PotPvPValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    this.queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? this.selectionAdditionRanked : this.selectionAdditionUnranked, ChatColor.GRAY.toString() + ChatColor.BOLD + "Join " + (ranked ? "Ranked" : "Unranked") + " Queue...", ranked).openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party=PotPvPND.getInstance().getPartyHandler().getParty(player);
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }
            if (ranked) {
                for ( UUID member : party.getMembers() ) {
                    if (RankedMatchQualificationListener.isQualified(member)) continue;
                    int needed=RankedMatchQualificationListener.getWinsNeededToQualify(member);
                    player.sendMessage(ChatColor.RED + "Your party can't join ranked queues because " + PotPvPND.getInstance().getUuidCache().name(member) + " has less than " + 10 + " unranked 1v1 wins. They need " + needed + " more wins!");
                    return;
                }
            }
            if (PotPvPValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    this.queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? this.selectionAdditionRanked : this.selectionAdditionUnranked, "Play " + (ranked ? "Ranked" : "Unranked"), ranked).openMenu(player);
            }
        };
    }

    private Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return (kitType) -> {
            MatchHandler matchHandler=PotPvPND.getInstance().getMatchHandler();
            int inFightsRanked=matchHandler.countPlayersPlayingMatches((m) -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked=this.queueHandler.countPlayersQueued(kitType, true);
            int inFightsUnranked=matchHandler.countPlayersPlayingMatches((m) -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked=this.queueHandler.countPlayersQueued(kitType, false);
            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)), ranked ? ImmutableList.of("", ChatColor.WHITE + "In Fights: " + PotPvPND.getInstance().getDominantColor() +  inFightsRanked, ChatColor.WHITE + "Queued: " + PotPvPND.getInstance().getDominantColor() + inQueueRanked, ChatColor.WHITE + "Anticheat: " + ChatColor.RED + "On", "", CC.GREEN + "Click here to select " + kitType.getDisplayName()) : ImmutableList.of("", ChatColor.WHITE + "In Fights: " +PotPvPND.getInstance().getDominantColor() + inFightsUnranked, ChatColor.WHITE + "Queued: " +PotPvPND.getInstance().getDominantColor() + inQueueUnranked, ChatColor.WHITE + "Anticheat: " + ChatColor.RED + "Off", "", CC.GREEN + "Click here to select " + kitType.getDisplayName()));
        };
    }
}


