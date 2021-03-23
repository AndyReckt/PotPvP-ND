package net.frozenorb.potpvp.morpheus.menu;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import com.qrakn.morpheus.game.event.GameEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.morpheus.menu.parameter.HostParametersMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

public class HostEventButton
extends Button {
    private final GameEvent event;

    HostEventButton(GameEvent event) {
        this.event = event;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (player.hasPermission(this.event.getPermission())) {
            if (this.event.getParameters().isEmpty()) {
                for (Game game : GameQueue.INSTANCE.getGames()) {
                    if (!game.getHost().equals(player)) continue;
                    player.sendMessage(ChatColor.RED + "You've already queued an event!");
                    player.closeInventory();
                    return;
                }
                if (GameQueue.INSTANCE.size() > 9) {
                    player.sendMessage(ChatColor.RED + "The game queue is currently full! Try again later.");
                } else {
                    GameQueue.INSTANCE.add(new Game(this.event, player, new ArrayList()));
                    player.sendMessage(ChatColor.GRAY + "You've added a " + ChatColor.AQUA + this.event.getName().toLowerCase() + ChatColor.GRAY + " event to the queue.");
                }
                player.closeInventory();
            } else {
                new HostParametersMenu(this.event).openMenu(player);
            }
        }
    }

    @Override
    public String getName(Player player) {
        if (player.hasPermission(this.event.getPermission())) {
            return ChatColor.GREEN + this.event.getName();
        }
        return ChatColor.RED + this.event.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Collections.singletonList(ChatColor.GRAY + this.event.getDescription());
    }

    @Override
    public Material getMaterial(Player player) {
        return this.event.getIcon().getType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte)this.event.getIcon().getDurability();
    }
}

