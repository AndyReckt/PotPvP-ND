package net.frozenorb.potpvp.setting.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import net.frozenorb.potpvp.setting.Setting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Menu used by /settings to let players toggle settings
 */
public final class SettingsMenu extends Menu {

    public SettingsMenu() {
        super("Edit settings");

        setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new DisplayButton(new ItemBuilder(Material.DIAMOND_SWORD).name("&7Practice Settings").build(),
            true));

        for (int i = 9; i < 54; i++) {
            buttons.put(i, new DisplayButton(new ItemUtils.ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 0).build(), true));
        }

        buttons.put(19, new SettingButton(Setting.ALLOW_SPECTATORS));
        buttons.put(21, new SettingButton(Setting.VIEW_OTHER_SPECTATORS));
        buttons.put(23, new SettingButton(Setting.RECEIVE_DUELS));
        buttons.put(25, new SettingButton(Setting.PARTY_INVITE));

        buttons.put(37, new SettingButton(Setting.VIEW_OTHERS_LIGHTNING));
        buttons.put(39, new SettingButton(Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES));
        buttons.put(41, new SettingButton(Setting.SEE_TOURNAMENT_JOIN_MESSAGE));
        buttons.put(43, new SettingButton(Setting.SELECT_MAP));

        return buttons;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class DisplayButton extends Button {

        private ItemStack itemStack;
        private boolean cancel;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (this.itemStack == null) {
                return new ItemStack(Material.AIR);
            } else {
                return this.itemStack;
            }
        }

        @Override
        public boolean shouldCancel(@NotNull Player player, int slot, @NotNull ClickType clickType) {
            return cancel;
        }
    }

}