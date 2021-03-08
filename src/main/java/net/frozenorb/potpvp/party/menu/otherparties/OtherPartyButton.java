/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 */
package net.frozenorb.potpvp.party.menu.otherparties;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.duel.command.DuelCommand;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class OtherPartyButton
        extends Button {
    private final Party party;

    OtherPartyButton(Party party) {
        this.party=Preconditions.checkNotNull(party, "party");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + PotPvPSI.getInstance().getUuidCache().name(this.party.getLeader());
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> description=new ArrayList<String>();
        description.add("");
        for ( UUID member : this.party.getMembers() ) {
            ChatColor color=this.party.isLeader(member) ? ChatColor.DARK_PURPLE : ChatColor.YELLOW;
            description.add(color + PotPvPSI.getInstance().getUuidCache().name(member));
        }
        description.add("");
        description.add(ChatColor.GREEN + "\u00bb Click to duel \u00ab");
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 3;
    }

    @Override
    public int getAmount(Player player) {
        return this.party.getMembers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        Party senderParty=PotPvPSI.getInstance().getPartyHandler().getParty(player);
        if (senderParty == null) {
            return;
        }
        if (senderParty.isLeader(player.getUniqueId())) {
            DuelCommand.duel(player, Bukkit.getPlayer(this.party.getLeader()));
        } else {
            player.sendMessage(ChatColor.RED + "Only the leader can duel other parties!");
        }
    }
}

