package net.frozenorb.potpvp.party.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.party.PartyAccessRestriction;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyManage;
import net.frozenorb.potpvp.party.command.PartyLockCommand;
import net.frozenorb.potpvp.party.command.PartyOpenCommand;
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


//Drizzy Start
public class PartySettingsMenu extends Menu {
    @Override
    public String getTitle(final Player player) {
        return "&7Party Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons=new HashMap<>();
        buttons.put(3, new SelectManageButton(PartyManage.MEMBER_MANAGE));
        buttons.put(5, new SelectManageButton(PartyManage.PUBLIC));
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectManageButton extends Button {

        private final PartyManage partyManage;

        @Override
        public String getName(final Player player) {

            if (partyManage == PartyManage.MEMBER_MANAGE) {
                return PotPvPND.getInstance().getDominantColor() + this.partyManage.getName();
            } else if (partyManage == PartyManage.PUBLIC) {
                return PotPvPND.getInstance().getDominantColor() + this.partyManage.getName();
            }
            return "Shouldn't appear";
        }

        @Override
        public List<String> getDescription(Player player) {
            ArrayList<String> description=new ArrayList<>();
            PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
            if (partyManage == PartyManage.MEMBER_MANAGE) {
                description.add(CC.MENU_BAR);
                description.add("&7Click to select a");
                description.add("&7Member to manage");
                description.add(CC.MENU_BAR);
            }
            if (partyManage == PartyManage.PUBLIC) {
                description.add(CC.MENU_BAR);
                description.add("&fState: " + PotPvPND.getInstance().getDominantColor() + partyHandler.getParty(player).getAccessRestriction());
                description.add("");
                description.add("&7Click to change state");
                description.add("&7of the party");
                description.add(CC.MENU_BAR);
            }
            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            if (partyManage == PartyManage.MEMBER_MANAGE) {
                return Material.PAPER;
            } else {
                return Material.CHEST;
            }
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
            PartyHandler partyHandler=PotPvPND.getInstance().getPartyHandler();
            if (partyHandler.getParty(player) == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                Menu.getCurrentlyOpenedMenus().get(player.getName()).setManualClose(false);
                player.closeInventory();
                return;
            }
            if (!player.hasPermission("practice.donator")) {
                player.sendMessage(CC.RED + "You need a Donator Rank for this");
                Menu.getCurrentlyOpenedMenus().get(player.getName()).setManualClose(false);
                player.closeInventory();
                return;
            }
            if (!player.getUniqueId().equals(partyHandler.getParty(player).getLeader())) {
                player.sendMessage(CC.RED + "You can only manage settings as a leader.");
                return;
            }
            if (partyManage == PartyManage.MEMBER_MANAGE) {
                player.closeInventory();
                new PartyListMenu().openMenu(player);
            }
            if (partyManage == PartyManage.PUBLIC) {
                if (partyHandler.getParty(player).getAccessRestriction().equals(PartyAccessRestriction.Invite_Only)) {
                    player.closeInventory();
                    PartyOpenCommand.partyOpen(player);
                } else if (partyHandler.getParty(player).getAccessRestriction().equals(PartyAccessRestriction.Public)) {
                    player.closeInventory();
                    PartyLockCommand.partyLock(player);
                }
            }
        }
    }
}

//Drizzy End
