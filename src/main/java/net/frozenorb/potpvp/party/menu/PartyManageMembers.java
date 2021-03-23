package net.frozenorb.potpvp.party.menu;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.party.PartyManage;
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
public class PartyManageMembers extends Menu {
    Player target;

    @Override
    public String getTitle(final Player player) {
        return "&bSelect an action for &f" + this.target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons=new HashMap<>();
        buttons.put(3, new SelectManageButton(PartyManage.LEADER));
        buttons.put(5, new SelectManageButton(PartyManage.KICK));
        return buttons;
    }

    @ConstructorProperties({"target"})
    public PartyManageMembers(final Player target) {
        this.target=target;
    }

    private class SelectManageButton extends Button {
        private final PartyManage partyManage;

        @Override
        public String getName(final Player player) {
            if (partyManage == PartyManage.KICK) {
                return PartyManage.KICK.getName();
            } else {
                return PartyManage.LEADER.getName();
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            ArrayList<String> description=new ArrayList<>();
            if (partyManage == PartyManage.KICK) {
                description.add("Click to Kick" + target.getName());
            }
            if (partyManage == PartyManage.LEADER) {
                description.add("Click to promote" + target.getName());
            }
            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return (partyManage == PartyManage.KICK ? Material.GOLD_SWORD : Material.REDSTONE);
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
            if (partyManage == PartyManage.KICK) {
                player.closeInventory();
                player.performCommand("party kick" + target);
            }
            if (partyManage == PartyManage.LEADER) {
                player.closeInventory();
                player.performCommand("party leader" + target);

            }
        }

        @ConstructorProperties({"partyManage"})
        public SelectManageButton(final PartyManage partyManage) {
            this.partyManage=partyManage;
        }
    }
}
//Drizzy End