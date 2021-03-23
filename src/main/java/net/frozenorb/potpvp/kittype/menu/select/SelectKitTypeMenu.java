package net.frozenorb.potpvp.kittype.menu.select;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class SelectKitTypeMenu
        extends Menu {
    private final boolean reset;
    private final Callback<KitType> callback;

    public SelectKitTypeMenu(Callback<KitType> callback, String title) {
        this(callback, true, title);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset, String title) {
        super(ChatColor.GRAY.toString() + title);
        this.callback=Preconditions.checkNotNull(callback, "callback");
        this.reset=reset;
    }

    @Override
    public void onClose(Player player, boolean manualClose) {
        if (this.reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons=new HashMap<>();
        int index=0;
        for ( KitType kitType : KitType.getAllTypes() ) {
            if (!player.isOp() && kitType.isHidden()) continue;
            buttons.put(index++, new KitTypeButton(kitType, this.callback));
        }
        Party party=PotPvPND.getInstance().getPartyHandler().getParty(player);
        if (party != null) {
            buttons.put(8, new KitTypeButton(KitType.teamFight, this.callback));
        }
        return buttons;
    }
}

