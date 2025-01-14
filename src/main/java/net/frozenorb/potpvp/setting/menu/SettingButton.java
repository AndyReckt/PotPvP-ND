package net.frozenorb.potpvp.setting.menu;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.util.CC;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Button used by {@link net.frozenorb.potpvp.setting.menu.SettingsMenu} to render a {@link Setting}
 */
public final class SettingButton extends Button {

    private static final String ENABLED_ARROW = CC.BLUE + StringEscapeUtils.unescapeJava(" » ");
    private static final String DISABLED_SPACER = "    ";

    private final Setting setting;

    SettingButton(Setting setting) {
        this.setting = Preconditions.checkNotNull(setting, "setting");
    }

    @Override
    public String getName(Player player) {
        return setting.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");
        description.addAll(setting.getDescription());
        description.add("");

        if (PotPvPND.getInstance().getSettingHandler().getSetting(player, setting)) {
            description.add(ENABLED_ARROW + setting.getEnabledText());
            description.add(DISABLED_SPACER + setting.getDisabledText());
        } else {
            description.add(DISABLED_SPACER + setting.getEnabledText());
            description.add(ENABLED_ARROW + setting.getDisabledText());
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return setting.getIcon();
    }

    @Override
    public byte getDamageValue(@NotNull Player player) {
        return (byte) setting.getDamage();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        if (!setting.canUpdate(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return;
        }

        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();

        boolean enabled = !settingHandler.getSetting(player, setting);
        settingHandler.updateSetting(player, setting, enabled);
    }

}