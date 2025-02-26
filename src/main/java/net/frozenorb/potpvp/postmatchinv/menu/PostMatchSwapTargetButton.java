package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.postmatchinv.PostMatchPlayer;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.List;

final class PostMatchSwapTargetButton extends Button {

    private final PostMatchPlayer newTarget;

    PostMatchSwapTargetButton(PostMatchPlayer newTarget) {
        this.newTarget = Preconditions.checkNotNull(newTarget, "newTarget");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "View " + PotPvPND.getInstance().getUuidCache().name(newTarget.getPlayerUuid()) + "'s inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to view " + PotPvPND.getInstance().getUuidCache().name(newTarget.getPlayerUuid()) + "'s inventory"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEVER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        new PostMatchMenu(newTarget).openMenu(player);
    }

}