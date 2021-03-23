package net.frozenorb.potpvp.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.List;
import java.util.Optional;

final class KitIconButton extends Button {

    private final Optional<Kit> kitOpt;
    private final KitType kitType;
    private final int slot;

    KitIconButton(Optional<Kit> kitOpt, KitType kitType, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + (kitOpt.map(Kit::getName).orElse("Create Kit"));
    }

    @Override
    public List<String> getDescription(Player player) {
        return kitOpt.map(kit -> ImmutableList.of(
            "",
            ChatColor.GREEN + "Heals: " + ChatColor.WHITE + kit.countHeals(),
            ChatColor.RED + "Debuffs: " + ChatColor.WHITE + kit.countDebuffs()
        )).orElse(ImmutableList.of());
    }

    @Override
    public Material getMaterial(Player player) {
        return kitOpt.isPresent() ? Material.DIAMOND_SWORD : Material.STONE_SWORD;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (kitOpt.isPresent()) {
            Kit kit = kitOpt.get();
            kit.apply(player);
            player.sendMessage(CC.GREEN + "Kit " + kit.getName() + " loaded successfully.");
            return;
        }
        kitOpt.orElseGet(() -> {
            KitHandler kitHandler = PotPvPND.getInstance().getKitHandler();
            player.sendMessage(CC.GREEN + "Kit created successfully.");

            Kit created = Kit.ofDefaultKit(kitType, "Kit " + this.slot, this.slot);
            created.setInventoryContents(player.getInventory().getContents());

            return kitHandler.saveDefaultKit(player, this.kitType, this.slot);
        });

        //new EditKitMenu(resolvedKit).openMenu(player);
    }

}