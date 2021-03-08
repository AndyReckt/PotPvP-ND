package net.frozenorb.potpvp.party.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyListMenu extends Menu {
    @Override
    public String getTitle(final Player player) {
        return "&bSelect a Member to Manage";
    }

    @Override
    public Map<Integer, net.frozenorb.potpvp.kt.menu.Button> getButtons(final Player player) {
        final Map<Integer, net.frozenorb.potpvp.kt.menu.Button> buttons=new HashMap<>();
        final PartyHandler partyHandler=PotPvPSI.getInstance().getPartyHandler();
        final Map<Integer, PartyDisplayButton> map;
        final PartyDisplayButton partyDisplayButton;
        partyHandler.getParty(player).getMembers().forEach(pplayer -> buttons.put(buttons.size(), new PartyDisplayButton(PotPvPSI.getInstance().getServer().getPlayer(pplayer))));
        return buttons;
    }

    public static class PartyDisplayButton extends net.frozenorb.potpvp.kt.menu.Button {
        private final Player pplayer;

        @Override
        public String getName(final Player player) {
            return "&d" + this.pplayer.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
            final PartyHandler partyHandler=PotPvPSI.getInstance().getPartyHandler();
            final String lore=partyHandler.getParty(player).isLeader(player.getUniqueId()) ? "&fClick to manage" : "";
            ArrayList<String> description=new ArrayList<>();
            description.add(lore);
            return description;
        }

        @Override
        public Material getMaterial(Player plyaer) {
            return Material.SKULL_ITEM;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 0;
        }

        @Override
        public int getAmount(Player player) {
            return 1;
        }


        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            Menu.getCurrentlyOpenedMenus().get(player.getName()).setManualClose(false);
            final PartyHandler partyHandler=PotPvPSI.getInstance().getPartyHandler();
            if (!player.getUniqueId().equals(partyHandler.getParty(player).getLeader())) {
                player.sendMessage(CC.RED + "You can only manage players as a leader.");
                return;
            }
            if (this.pplayer.getUniqueId().equals(partyHandler.getParty(this.pplayer).getLeader())) {
                player.sendMessage(CC.RED + "You cannot manage yourself.");
                return;
            }
            if (partyHandler.getParty(player) != null && partyHandler.getParty(this.pplayer) == null) {
                player.sendMessage(CC.RED + "That player is not in a party. (Left just now?)");
                return;
            }
            new PartyManageMembers(this.pplayer).openMenu(player);
        }

        @ConstructorProperties({"pplayer"})
        public PartyDisplayButton(final Player pplayer) {
            this.pplayer=pplayer;
        }
    }
}
