package io.github.scarger.gadgets.gadgetscore.component.gadget;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;

import net.minecraft.server.v1_14_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;

public abstract class Gadget {

    GadgetsCore plugin;

    public Gadget(GadgetsCore plugin) {
        this.plugin = plugin;
    }

    public abstract String getName();

    public abstract ItemStack getItem(Player player);

    public abstract List<String> getDescription();

    @OverridingMethodsMustInvokeSuper
    public void run(Player player) {
        plugin.getManager().add(player);
    }

    public boolean isValid(Player player) {
        return true;
    }

    public String getInvalidMessage() {
        return ChatColor.RED + "You can not run this gadget in your current state / position!";
    }

    void sendPacket(Packet packet) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    void sendPacket(Packet packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
