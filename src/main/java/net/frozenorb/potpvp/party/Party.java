package net.frozenorb.potpvp.party;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.party.event.*;
import net.frozenorb.potpvp.pvpclasses.PvPClasses;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public final class Party {
    private final UUID partyId=new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong());
    public static final int MAX_SIZE=30;
    private UUID leader;
    private final Map<UUID, PvPClasses> kits=new HashMap<>();
    private final Set<UUID> members=Sets.newLinkedHashSet();
    private final Set<PartyInvite> invites=Collections.newSetFromMap(new ConcurrentHashMap<>());
    private PartyAccessRestriction accessRestriction=PartyAccessRestriction.Invite_Only;
    private String password=null;

    Party(UUID leader) {
        this.leader=Preconditions.checkNotNull(leader, "leader");
        this.members.add(leader);
        PotPvPND.getInstance().getPartyHandler().updatePartyCache(leader, this);
        Bukkit.getPluginManager().callEvent(new PartyCreateEvent(this));
    }

    public boolean isMember(UUID playerUuid) {
        return this.members.contains(playerUuid);
    }

    public boolean isLeader(UUID playerUuid) {
        return this.leader.equals(playerUuid);
    }

    public Set<UUID> getMembers() {
        return ImmutableSet.copyOf(this.members);
    }

    public Set<PartyInvite> getInvites() {
        return ImmutableSet.copyOf(this.invites);
    }

    public PartyInvite getInvite(UUID target) {
        for ( PartyInvite invite : this.invites ) {
            if (!invite.getTarget().equals(target)) continue;
            return invite;
        }
        return null;
    }

    public void revokeInvite(PartyInvite invite) {
        this.invites.remove(invite);
    }

    public void invite(Player target) {
        PartyInvite invite=new PartyInvite(this, target.getUniqueId());
        target.spigot().sendMessage(PartyLang.inviteAcceptPrompt(this));
        this.message(ChatColor.RED + target.getName() + ChatColor.YELLOW + " has been invited to join your party.");
        this.invites.add(invite);
        Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), () -> this.invites.remove(invite), 600L);
    }

    public void join(Player player) {
        if (this.members.contains(player.getUniqueId())) {
            return;
        }
        if (!PotPvPValidation.canJoinParty(player, this)) {
            return;
        }
        PartyInvite invite=this.getInvite(player.getUniqueId());
        if (invite != null) {
            this.revokeInvite(invite);
        }
        Player leaderBukkit=Bukkit.getPlayer(this.leader);
        player.sendMessage(ChatColor.GREEN + "You have joined " + ChatColor.RED + leaderBukkit.getName() + ChatColor.YELLOW + "'s party.");
        this.message(ChatColor.RED + player.getName() + ChatColor.YELLOW + " has joined your party.");
        this.members.add(player.getUniqueId());
        PotPvPND.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), this);
        Bukkit.getPluginManager().callEvent(new PartyMemberJoinEvent(player, this));
        this.forEachOnline(VisibilityUtils::updateVisibility);
        this.resetInventoriesDelayed();
    }

    public void leave(Player player) {
        if (this.isLeader(player.getUniqueId()) && this.members.size() == 1) {
            this.disband();
            return;
        }
        if (!this.members.remove(player.getUniqueId())) {
            return;
        }
        PotPvPND.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);
        if (this.leader.equals(player.getUniqueId())) {
            UUID[] membersArray=this.members.toArray(new UUID[this.members.size()]);
            Player newLeader=Bukkit.getPlayer(membersArray[ThreadLocalRandom.current().nextInt(membersArray.length)]);
            this.leader=newLeader.getUniqueId();
            this.message(ChatColor.RED + newLeader.getName() + ChatColor.YELLOW + " has been randomly promoted to leader of your party.");
        }
        player.sendMessage(ChatColor.RED + "You have left your party.");
        this.message(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " has left your party.");
        VisibilityUtils.updateVisibility(player);
        this.forEachOnline(VisibilityUtils::updateVisibility);
        Bukkit.getPluginManager().callEvent(new PartyMemberLeaveEvent(player, this));
        InventoryUtils.resetInventoryDelayed(player);
        this.resetInventoriesDelayed();
    }

    public void setLeader(Player player) {
        this.leader=player.getUniqueId();
        this.message(ChatColor.RED + player.getName() + ChatColor.YELLOW + " has been promoted to leader of your party.");
        this.resetInventoriesDelayed();
    }

    public void disband() {
        Bukkit.getPluginManager().callEvent(new PartyDisbandEvent(this));
        PotPvPND.getInstance().getPartyHandler().unregisterParty(this);
        this.forEachOnline(player -> {
            VisibilityUtils.updateVisibility(player);
            PotPvPND.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);
        });
        this.message(ChatColor.RED + "Your party has been disbanded.");
        this.resetInventoriesDelayed();
    }

    public void kick(Player player) {
        if (!this.members.remove(player.getUniqueId())) {
            return;
        }
        PotPvPND.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);
        player.sendMessage(ChatColor.RED + "You have been kicked from your party.");
        this.message(ChatColor.RED + player.getName() + ChatColor.YELLOW + " has been kicked from your party.");
        VisibilityUtils.updateVisibility(player);
        this.forEachOnline(VisibilityUtils::updateVisibility);
        Bukkit.getPluginManager().callEvent(new PartyMemberKickEvent(player, this));
        InventoryUtils.resetInventoryDelayed(player);
        this.resetInventoriesDelayed();
    }

    public void message(String message) {
        this.forEachOnline(p -> p.sendMessage(message));
    }

    public void playSound(Sound sound, float pitch) {
        this.forEachOnline(p -> p.playSound(p.getLocation(), sound, 10.0f, pitch));
    }

    public void resetInventoriesDelayed() {
        Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), this::resetInventoriesNow, 2L);
    }

    public void resetInventoriesNow() {
        this.forEachOnline(InventoryUtils::resetInventoryNow);
    }

    private void forEachOnline(Consumer<Player> consumer) {
        for ( UUID member : this.members ) {
            Player memberBukkit=Bukkit.getPlayer(member);
            if (memberBukkit == null) continue;
            consumer.accept(memberBukkit);
        }
    }

    public UUID getPartyId() {
        return this.partyId;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public Map<UUID, PvPClasses> getKits() {
        return this.kits;
    }

    public PartyAccessRestriction getAccessRestriction() {
        return this.accessRestriction;
    }

    public void setAccessRestriction(PartyAccessRestriction accessRestriction) {
        this.accessRestriction=accessRestriction;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password=password;
    }
}

