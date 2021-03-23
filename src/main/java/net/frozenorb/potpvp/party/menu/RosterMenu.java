package net.frozenorb.potpvp.party.menu;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.pagination.PaginatedMenu;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.pvpclasses.PvPClasses;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.*;

public class RosterMenu
        extends PaginatedMenu {
    private final Party party;

    public RosterMenu(Party party) {
        this.party=party;
        this.setAutoUpdate(true);
        this.setUpdateAfterClick(true);
        this.setPlaceholder(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Team Roster";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> toReturn=new HashMap<>();
        if (this.party.getMembers().size() == 1) {
            player.closeInventory();
            return toReturn;
        }
        for ( final UUID uuid : new ArrayList<UUID>(this.party.getKits().keySet()) ) {
            if (this.party.getMembers().contains(uuid)) continue;
            this.party.getKits().remove(uuid);
        }
        for ( final UUID uuid : this.party.getMembers() ) {
            final Player member=Bukkit.getPlayer(uuid);
            if (member == null) continue;
            final PvPClasses selected=this.party.getKits().getOrDefault(uuid, PvPClasses.DIAMOND);
            toReturn.put(toReturn.isEmpty() ? 0 : toReturn.size(), new Button() {

                @Override
                public String getName(Player player) {
                    return member.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    ArrayList<String> description=new ArrayList<String>();
                    for ( PvPClasses kit : PvPClasses.values() ) {
                        if (kit == selected) {
                            description.add(ChatColor.GREEN + "> " + kit.getName());
                            continue;
                        }
                        if (kit.allowed(RosterMenu.this.party)) {
                            description.add(ChatColor.GRAY + kit.getName());
                            continue;
                        }
                        description.add(ChatColor.RED + ChatColor.STRIKETHROUGH.toString() + kit.getName());
                    }
                    return description;
                }

                @Override
                public Material getMaterial(Player player) {
                    return selected.getIcon();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
                    if (RosterMenu.this.party.isLeader(player.getUniqueId())) {
                        List<PvPClasses> kits=Arrays.asList(PvPClasses.values());
                        int index=kits.indexOf(selected);
                        PvPClasses next2=null;
                        for ( int times=0; next2 == null && times <= 50; ++times ) {
                            if (index + 1 < kits.size()) {
                                next2=kits.get(index + 1);
                                if (next2.allowed(RosterMenu.this.party)) continue;
                                next2=null;
                                ++index;
                                continue;
                            }
                            index=-1;
                        }
                        if (next2 == null) {
                            next2=PvPClasses.DIAMOND;
                        }
                        RosterMenu.this.party.message(player.getDisplayName() + ChatColor.YELLOW + " has set " + member.getDisplayName() + ChatColor.YELLOW + "'s " + ChatColor.YELLOW + " kit to " + ChatColor.GRAY + next2.getName() + ChatColor.YELLOW + ".");
                        RosterMenu.this.party.getKits().put(uuid, next2);
                        for ( UUID other : RosterMenu.this.party.getMembers() ) {
                            PotPvPND.getInstance().getPartyHandler().updatePartyCache(other, RosterMenu.this.party);
                        }
                    }
                }
            });
        }
        return toReturn;
    }

    @Override
    public int size(Map<Integer, ? extends Button> buttons) {
        return 45;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 36;
    }
}

