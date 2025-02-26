package net.frozenorb.potpvp.kit.menu.kits;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kit.menu.editkit.SaveButton;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    private final KitType kitType;

    public KitsMenu(KitType kitType) {
        super("Viewing " + kitType.getName() + " kits");

        setPlaceholder(true);
        setAutoUpdate(true);

        this.kitType = kitType;
    }

   /* @Override
    public void onClose(Player player, boolean manualClose) {
        InventoryUtils.resetInventoryDelayed(player);
    }*/

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        KitHandler kitHandler = PotPvPND.getInstance().getKitHandler();
        Map<Integer, Button> buttons = new HashMap<>();

        // kit slots are 1-indexed
        for (int kitSlot = 1; kitSlot <= KitHandler.KITS_PER_TYPE; kitSlot++) {
            Optional<Kit> kitOpt = kitHandler.getKit(player, kitType, kitSlot);
            int column = (kitSlot * 2) - 1; // - 1 to compensate for this being 0-indexed

            buttons.put(getSlot(column, 1), new KitIconButton(kitOpt, kitType, kitSlot));
            //buttons.put(getSlot(column, 2), new KitEditButton(kitOpt, kitType, kitSlot));


            if (kitOpt.isPresent()) {
                int finalKitSlot = kitSlot;
                Kit resolvedKit = kitOpt.orElseGet(() -> kitHandler.saveDefaultKit(player, kitType, finalKitSlot));
                buttons.put(getSlot(column, 2), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 3), new SaveButton(resolvedKit));
                buttons.put(getSlot(column, 4), new KitDeleteButton(kitType, kitSlot));
            } else {
                buttons.put(getSlot(column, 2), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 4), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), ""));
                buttons.put(getSlot(column, 5), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.BLACK.getWoolData(), ""));
            }
        }

        /*buttons.put(getSlot(0, 4), new MenuBackButton(p -> {
            new SelectKitTypeMenu(kitType -> {
                new KitsMenu(kitType).openMenu(p);
            }, "Select a kit type...").openMenu(p);
        }));*/

        return buttons;
    }

}