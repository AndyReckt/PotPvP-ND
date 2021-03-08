/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.party.menu.oddmanout;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;
import net.frozenorb.potpvp.kt.util.Callback;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class OddManOutMenu
        extends Menu {
    private final Callback<Boolean> callback;

    public OddManOutMenu(Callback<Boolean> callback) {
        super("Continue with unbalanced teams?");
        this.callback=Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons=new HashMap<Integer, Button>();
        buttons.put(this.getSlot(2, 0), new OddManOutButton(true, this.callback));
        buttons.put(this.getSlot(6, 0), new OddManOutButton(false, this.callback));
        return buttons;
    }
}

