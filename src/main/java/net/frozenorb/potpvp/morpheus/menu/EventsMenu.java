package net.frozenorb.potpvp.morpheus.menu;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.GameState;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EventsMenu extends Menu {
    public EventsMenu() {
        super(ChatColor.GRAY + "Join an event");
        this.setAutoUpdate(true);
    }

    @NotNull
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> toReturn=new HashMap<>();
        for ( final Game game : GameQueue.INSTANCE.getCurrentGames() ) {
            toReturn.put(toReturn.size(), new Button() {

                @NotNull
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + game.getEvent().getName() + " Event";
                }

                @NotNull
                @Override
                public List<String> getDescription(Player player) {
                    ArrayList<String> lines=new ArrayList<>();
                    for ( String line : Arrays.asList("&7&m-------------------------", "&fPlayers&7: &b" + game.getPlayers().size() + (game.getMaxPlayers() == -1 ? "" : "&7/" + game.getMaxPlayers()), "&fState&7: &b" + StringUtils.capitalize(game.getState().name().toLowerCase()), "&fHosted By&7: &b" + game.getHost().getDisplayName(), " ", game.getState() == GameState.STARTING ? "&7Click here to join." : "&7Click here to spectate.", "&7&m-------------------------") ) {
                        lines.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                    return lines;
                }

                @NotNull
                @Override
                public Material getMaterial(Player player) {
                    return game.getEvent().getIcon().getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) game.getEvent().getIcon().getDurability();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
                    player.closeInventory();
                    if (game.getState() == GameState.STARTING) {
                        if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                            player.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                            return;
                        }
                        game.add(player);
                    } else {
                        game.addSpectator(player);
                    }
                }
            });
        }
        if (toReturn.isEmpty()) {
            player.closeInventory();
        }
        return toReturn;
    }
}

