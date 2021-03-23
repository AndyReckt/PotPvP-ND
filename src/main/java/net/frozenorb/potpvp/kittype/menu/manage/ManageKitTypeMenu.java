package net.frozenorb.potpvp.kittype.menu.manage;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.command.ManageCommand;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.util.menu.BooleanTraitButton;
import net.frozenorb.potpvp.util.menu.MenuBackButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageKitTypeMenu
        extends Menu {
    private final KitType type;

    public ManageKitTypeMenu(KitType type) {
        super("Editing " + type.getName());
        this.setNoncancellingInventory(true);
        this.setUpdateAfterClick(false);
        this.type=type;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        int i;
        HashMap<Integer, Button> buttons=new HashMap<>();
        for ( i=1; i <= 5; ++i ) {
            buttons.put(this.getSlot(1, i), Button.placeholder(Material.OBSIDIAN));
        }
        for ( i=1; i <= 8; ++i ) {
            buttons.put(this.getSlot(i, 1), Button.placeholder(Material.OBSIDIAN));
        }
        buttons.put(this.getSlot(0, 1), new BooleanTraitButton<KitType>(this.type, "Hidden", KitType::setHidden, KitType::isHidden, KitType::saveAsync));
        buttons.put(this.getSlot(0, 2), new BooleanTraitButton<KitType>(this.type, "Editor Item Spawn", KitType::setEditorSpawnAllowed, KitType::isEditorSpawnAllowed, KitType::saveAsync));
        buttons.put(this.getSlot(0, 3), new BooleanTraitButton<KitType>(this.type, "Health Shown", KitType::setHealthShown, KitType::isHealthShown, KitType::saveAsync));
        buttons.put(this.getSlot(0, 4), new BooleanTraitButton<KitType>(this.type, "Building Allowed", KitType::setBuildingAllowed, KitType::isBuildingAllowed, KitType::saveAsync));
        buttons.put(this.getSlot(0, 5), new BooleanTraitButton<KitType>(this.type, "Hardcore Healing", KitType::setHardcoreHealing, KitType::isHardcoreHealing, KitType::saveAsync));
        buttons.put(this.getSlot(0, 6), new BooleanTraitButton<KitType>(this.type, "Pearl Damage", KitType::setPearlDamage, KitType::isPearlDamage, KitType::saveAsync));
        buttons.put(this.getSlot(0, 7), new BooleanTraitButton<KitType>(this.type, "Supports Ranked", KitType::setSupportsRanked, KitType::isSupportsRanked, KitType::saveAsync));
        buttons.put(this.getSlot(0, 8), new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED.toString() + ChatColor.BOLD + "Wipe existing kits";
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of("", ChatColor.RED + "Removes all saved " + ManageKitTypeMenu.this.type.getName() + " kits", ChatColor.RED + "(includes online and offline players)", "", ChatColor.RED + "For safety reasons this button is disabled,", ChatColor.RED + "use /kit wipekits type " + ManageKitTypeMenu.this.type.getId().toLowerCase());
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TNT;
            }
        });
        buttons.put(this.getSlot(1, 0), new SaveKitTypeButton(this.type));
        buttons.put(this.getSlot(2, 0), new CancelKitTypeEditButton());
        buttons.put(this.getSlot(8, 0), new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));
        ItemStack[] kit=this.type.getEditorItems();
        int x=0;
        int y=0;
        for ( ItemStack editorItem : kit ) {
            if (editorItem != null && editorItem.getType() != Material.AIR) {
                buttons.put(this.getSlot(x + 2, y + 2), this.nonCancellingItem(editorItem));
            }
            if (++x <= 6) continue;
            x=0;
            if (++y >= 4) break;
        }
        return buttons;
    }

    private Button nonCancellingItem(final ItemStack stack) {
        return new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return stack;
            }

            @Override
            public String getName(Player player) {
                return stack.getItemMeta().getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return stack.getItemMeta().getLore();
            }

            @Override
            public Material getMaterial(Player player) {
                return stack.getType();
            }

            @Override
            public boolean shouldCancel(Player player, int slot, ClickType clickType) {
                return false;
            }
        };
    }
}

