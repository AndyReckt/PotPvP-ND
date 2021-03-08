package net.frozenorb.potpvp.party.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyItems;
import net.frozenorb.potpvp.party.command.PartyEventsCommand;
import net.frozenorb.potpvp.party.command.PartyInfoCommand;
import net.frozenorb.potpvp.party.command.PartyLeaveCommand;
import net.frozenorb.potpvp.party.menu.PartySettingsMenu;
import net.frozenorb.potpvp.party.menu.RosterMenu;
import net.frozenorb.potpvp.party.menu.otherparties.OtherPartiesMenu;
import net.frozenorb.potpvp.util.ItemListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PartyItemListener
        extends ItemListener {
    public PartyItemListener(PartyHandler partyHandler) {
        this.addHandler(PartyItems.LEAVE_PARTY_ITEM, PartyLeaveCommand::partyLeave);
        this.addHandler(PartyItems.START_TEAM_EVENTS, PartyEventsCommand::execute);
        this.addHandler(PartyItems.OTHER_PARTIES_ITEM, p -> new OtherPartiesMenu().openMenu(p));
        this.addHandler(PartyItems.ASSIGN_CLASSES, p -> new RosterMenu(partyHandler.getParty(p)).openMenu(p));
        this.addHandler(PartyItems.PARTY_SETTINGS, player -> new PartySettingsMenu().openMenu(player));
    }

    @EventHandler
    public void fastPartyIcon(PlayerInteractEvent event) {
        boolean permitted;
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }
        if (event.getItem().getType() != PartyItems.ICON_TYPE) {
            return;
        }
        boolean bl=permitted=canUseButton.getOrDefault(event.getPlayer().getUniqueId(), 0L) < System.currentTimeMillis();
        if (permitted) {
            Player player=event.getPlayer();
            Party party=PotPvPSI.getInstance().getPartyHandler().getParty(player);
            if (party != null && PartyItems.icon(party).isSimilar(event.getItem())) {
                event.setCancelled(true);
                PartyInfoCommand.partyInfo(player, player);
            }
            canUseButton.put(player.getUniqueId(), System.currentTimeMillis() + 500L);
        }
    }
}

