package net.frozenorb.potpvp.party.menu;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.party.PartyEventsENUM;
import net.frozenorb.potpvp.party.command.PartyFfaCommand;
import net.frozenorb.potpvp.party.command.PartyTeamSplitCommand;
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


public class PartyEventsMenu extends Menu {

    public String getTitle(final Player player) {
        return CC.GRAY + "Party Events";
    }

    @Override
    public Map<Integer, net.frozenorb.potpvp.kt.menu.Button> getButtons(Player player) {
        final HashMap<Integer, net.frozenorb.potpvp.kt.menu.Button> buttons=new HashMap<>();
        buttons.put(3, new SelectEventButton(PartyEventsENUM.PartyFFA));
        buttons.put(5, new SelectEventButton(PartyEventsENUM.PartySplit));
        return buttons;
    }

    private static class SelectEventButton extends net.frozenorb.potpvp.kt.menu.Button {

        private final PartyEventsENUM partyevents;

        public String getName(final Player player) {
            if (partyevents == PartyEventsENUM.PartySplit) {
                return CC.AQUA + "Split Party";
            } else {
                return CC.AQUA + "Party FFA";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            ArrayList<String> description=new ArrayList<>();
            if (partyevents == PartyEventsENUM.PARTYUnranked) {
                description.add(CC.MENU_BAR);
                description.add(CC.GRAY + "Join Unranked 2v2 Queue with Party");
                description.add(CC.GRAY + "Must have Min and Max" + CC.RED + " 2" + CC.GRAY + " Players");
                description.add(CC.MENU_BAR);
            }
            if (partyevents == PartyEventsENUM.PartyFFA) {
                description.add(CC.MENU_BAR);
                description.add(CC.GRAY + "Start a Party FFA Match");
                description.add(CC.GRAY + "Must have at least 2 Players");
                description.add(CC.MENU_BAR);
            }
            if (partyevents == PartyEventsENUM.PartySplit) {
                description.add(CC.MENU_BAR);
                description.add(CC.GRAY + "Start a Party Split Match");
                description.add(CC.GRAY + "Must have at least 2 Players");
                description.add(CC.MENU_BAR);
            }
            if (partyevents == PartyEventsENUM.PARTYRanked) {
                description.add(CC.MENU_BAR);
                description.add(CC.GRAY + "Join Ranked 2v2 Queue with Party");
                description.add(CC.GRAY + "Must have Min and Max" + CC.RED + " 2" + CC.GRAY + " Players");
                description.add(CC.MENU_BAR);
            }
            return description;
        }


        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            if (PotPvPND.getInstance().getPartyHandler().getParty(player).getMembers().size() < 2) {
                player.sendMessage(CC.RED + "You need to have at least 2 players in your party!");
                player.closeInventory();
                return;
            }
            if (partyevents == PartyEventsENUM.PartyFFA) {
                player.closeInventory();
                PartyFfaCommand.partyFfa(player);
            }
            if (partyevents == PartyEventsENUM.PartySplit) {
                player.closeInventory();
                PartyTeamSplitCommand.partyTeamSplit(player);
            }
        }

        @Override
        public Material getMaterial(Player player) {
            if (partyevents == PartyEventsENUM.PartyFFA) {
                return Material.LEASH;
            }
            if (partyevents == PartyEventsENUM.PartySplit) {
                return Material.SLIME_BALL;
            }
            return Material.DIAMOND_SWORD;
        }

        @Override
        public int getAmount(Player player) {
            return 1;
        }

        @Override
        public byte getDamageValue(Player player) {
            return 0;
        }

        @ConstructorProperties({"partyEvents"})
        public SelectEventButton(final PartyEventsENUM partyEventsENUM) {
            this.partyevents=partyEventsENUM;
        }
    }
}