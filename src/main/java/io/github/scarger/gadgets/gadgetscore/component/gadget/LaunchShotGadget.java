package io.github.scarger.gadgets.gadgetscore.component.gadget;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LaunchShotGadget extends Gadget {

    public LaunchShotGadget(GadgetsCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return ChatColor.GREEN + "LAUNCH SHOT";
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemStack(Material.BLAZE_ROD);
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList(ChatColor.RED + "Launches you to where you shoot!");
    }

    @Override
    public void run(Player player) {
        super.run(player);

        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setMetadata("gadget", new FixedMetadataValue(plugin, getName()));
        arrow.setMetadata("player", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        arrow.setGlowing(true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getManager().remove(player), 20 * 3);
    }
}
