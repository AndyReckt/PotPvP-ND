/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 */
package net.frozenorb.potpvp.morpheus.menu.parameter;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.event.GameEvent;
import com.qrakn.morpheus.game.parameter.GameParameter;
import com.qrakn.morpheus.game.parameter.GameParameterOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

public class HostParametersMenu
extends Menu {
    private final GameEvent event;
    private final List<HostParameterButton> buttons = new ArrayList<HostParameterButton>();

    public HostParametersMenu(GameEvent event) {
        super((Object)ChatColor.DARK_PURPLE + event.getName() + " options");
        this.setUpdateAfterClick(true);
        this.setPlaceholder(true);
        for (GameParameter parameter : event.getParameters()) {
            this.buttons.add(new HostParameterButton(parameter));
        }
        this.event = event;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<Integer, Button>();
        for (HostParameterButton button : this.buttons) {
            toReturn.put(toReturn.size(), button);
        }
        toReturn.put(8, new Button(){

            @Override
            public String getName(Player player) {
                return (Object)ChatColor.GREEN + "Start " + HostParametersMenu.this.event.getName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList((Object)ChatColor.GRAY + "Click to start the event.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
                for (Game game : GameQueue.INSTANCE.getGames()) {
                    if (!game.getHost().equals((Object)player)) continue;
                    player.sendMessage((Object)ChatColor.RED + "You've already queued an event!");
                    player.closeInventory();
                    return;
                }
                if (GameQueue.INSTANCE.size() > 9) {
                    player.sendMessage((Object)ChatColor.RED + "The game queue is currently full! Try again later.");
                } else {
                    ArrayList<GameParameterOption> options = new ArrayList<GameParameterOption>();
                    for (HostParameterButton hostParameterButton : HostParametersMenu.this.buttons) {
                        options.add(hostParameterButton.getSelectedOption());
                    }
                    GameQueue.INSTANCE.add(new Game(HostParametersMenu.this.event, player, options));
                    player.sendMessage((Object)ChatColor.GREEN + "You've added a " + HostParametersMenu.this.event.getName().toLowerCase() + " event to the queue.");
                }
                player.closeInventory();
            }
        });
        return toReturn;
    }
}

