package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public final class EditKitMenu extends Menu {

    private static final int EDITOR_X_OFFSET = 0;
    private static final int EDITOR_Y_OFFSET = 0;

    private final KitType kit;

    public EditKitMenu(KitType kit) {
        super("Editing " + kit.getName());

        setNoncancellingInventory(true);
        setUpdateAfterClick(false);
        setAutoUpdate(true);

        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    /*@Override
    public void onOpen(Player player) {
        player.getInventory().setContents(kit.getDefaultInventory());
        player.getInventory().setContents(kit.getDefaultArmor());

        Bukkit.getScheduler().runTaskLater(PotPvPND.getInstance(), player::updateInventory, 1L);
    }*/

    /* @Override
    public void onClose(Player player, boolean manualClose) {
        InventoryUtils.resetInventoryDelayed(player);
    }*/

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        if (kit.isEditorSpawnAllowed()) {
            short splashHealPotionDura = -1;
            int x = 0;
            int y = 0;

            for (ItemStack editorItem : kit.getEditorItems()) {
                if (editorItem != null) {
                    if (editorItem.getType() == Material.POTION) {
                        Potion potion = Potion.fromItemStack(editorItem);

                        if (potion.isSplash() && potion.getType() == PotionType.INSTANT_HEAL) {
                            splashHealPotionDura = editorItem.getDurability();
                        }
                    }

                    if (editorItem.getType() != Material.AIR) {
                        buttons.put(getSlot(x + EDITOR_X_OFFSET, y + EDITOR_Y_OFFSET), new TakeItemButton(editorItem));
                    }
                }

                x++;

                if (x >= 9) {
                    x = 0;
                    y++;
                    if (y >= 6) {
                        break;
                    }
                }
            }

            // Set fill all button with the potion id we detected
            if (splashHealPotionDura > 0) {
                buttons.put(buttons.size(), new FillHealPotionsButton(splashHealPotionDura));
            }
        }

        return buttons;
    }

}