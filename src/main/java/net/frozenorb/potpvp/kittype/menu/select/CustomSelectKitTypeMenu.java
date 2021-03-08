package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class CustomSelectKitTypeMenu extends Menu {

    private final Callback<KitType> callback;

    private final Function<KitType, CustomKitTypeMeta> metaFunc;

    private final boolean ranked;

    public CustomSelectKitTypeMenu(Callback<KitType> callback, Function<KitType, CustomKitTypeMeta> metaFunc, String title, boolean ranked) {
        super(ChatColor.AQUA + title);
        this.setAutoUpdate(true);
        this.callback=Preconditions.checkNotNull(callback, "callback");
        this.metaFunc=Preconditions.checkNotNull(metaFunc, "metaFunc");
        this.ranked=ranked;
    }

    @Override
    public void onClose(@NotNull Player player, boolean manualClose) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(@NotNull Player player) {
        HashMap<Integer, Button> buttons=new HashMap<>();
        int index=0;
        for ( KitType kitType : KitType.getAllTypes() ) {
            if (!player.isOp() && kitType.isHidden() || this.ranked && !kitType.isSupportsRanked()) continue;
            CustomKitTypeMeta meta=this.metaFunc.apply(kitType);
            buttons.put(index++, new KitTypeButton(kitType, this.callback, meta.getDescription(), meta.getQuantity()));
        }
        return buttons;
    }

    public static final class CustomKitTypeMeta {
        private final int quantity;
        private final List<String> description;

        @ConstructorProperties(value={"quantity", "description"})
        public CustomKitTypeMeta(int quantity, List<String> description) {
            this.quantity=quantity;
            this.description=description;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public List<String> getDescription() {
            return this.description;
        }
    }
}

