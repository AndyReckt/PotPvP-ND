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

import com.qrakn.morpheus.game.parameter.GameParameter;
import com.qrakn.morpheus.game.parameter.GameParameterOption;
import net.frozenorb.potpvp.kt.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

public class HostParameterButton
        extends Button {
    private final GameParameter parameter;
    private GameParameterOption selectedOption;

    HostParameterButton(GameParameter parameter) {
        this.parameter=parameter;
        this.selectedOption=parameter.getOptions().get(0);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        int index=this.parameter.getOptions().indexOf(this.selectedOption);
        index=index + 1 == this.parameter.getOptions().size() ? 0 : ++index;
        this.selectedOption=this.parameter.getOptions().get(index);
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + this.parameter.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> toReturn=new ArrayList<String>();
        for ( GameParameterOption option : this.parameter.getOptions() ) {
            if (option.equals(this.selectedOption)) {
                toReturn.add(ChatColor.GREEN + "\u00bb " + ChatColor.GRAY + option.getDisplayName());
                continue;
            }
            toReturn.add(ChatColor.GRAY + option.getDisplayName());
        }
        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return this.selectedOption.getIcon().getType();
    }

    @Override
    public int getAmount(Player player) {
        return this.selectedOption.getIcon().getAmount();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) this.selectedOption.getIcon().getDurability();
    }

    public GameParameterOption getSelectedOption() {
        return this.selectedOption;
    }
}

