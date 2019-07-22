package io.github.scarger.gadgets.gadgetscore.component;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import io.github.scarger.gadgets.gadgetscore.component.gadget.Gadget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GadgetsMenu {

    private GadgetsCore plugin;

    public GadgetsMenu(GadgetsCore plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        int menuSize = ((int) Math.ceil(plugin.getManager().getGadgets().size() / 9f)) * 9;
        Inventory menu = Bukkit.createInventory(null, menuSize, ChatColor.GOLD + "Gadgets");

        ItemStack bg = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(ChatColor.GRAY + "*");
        bg.setItemMeta(bgMeta);

        for(int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, bg);
        }

        for(int i = 0; i < plugin.getManager().getGadgets().size(); i++) {
            Gadget gadget = plugin.getManager().getGadgets().get(i);
            ItemStack gadgetMenuItem = gadget.getItem(player);
            ItemMeta gadgetMenuItemMeta = gadgetMenuItem.getItemMeta();
            gadgetMenuItemMeta.setLore(gadget.getDescription());
            gadgetMenuItemMeta.setDisplayName(gadget.getName());
            gadgetMenuItem.setItemMeta(gadgetMenuItemMeta);
            menu.setItem(i, gadgetMenuItem);
        }

        player.openInventory(menu);
    }
}
