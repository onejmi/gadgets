package io.github.scarger.gadgets.gadgetscore.listen;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import io.github.scarger.gadgets.gadgetscore.component.gadget.Gadget;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private GadgetsCore plugin;

    public MenuListener(GadgetsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(ChatColor.GOLD + "Gadgets")) {
            event.setCancelled(true);
            if(event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().hasDisplayName() &&
                    event.getCurrentItem().getItemMeta().getDisplayName().length() > 3) {
                for(Gadget gadget : plugin.getManager().getGadgets()) {
                    String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                    if(displayName.equals(gadget.getName())) {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().getInventory().addItem(event.getCurrentItem().clone());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHotBarInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getItem() != null) {
                if(event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    for(Gadget gadget : plugin.getManager().getGadgets()) {
                        if(event.getItem().getItemMeta().getDisplayName().equals(gadget.getName())) {
                            if(!plugin.getManager().hasGadgetInProgress(event.getPlayer())) {
                                if(gadget.isValid(event.getPlayer())) {
                                    gadget.run(event.getPlayer());
                                }
                                else {
                                    event.getPlayer().sendMessage(gadget.getInvalidMessage());
                                }
                            }
                            else event.getPlayer().sendMessage(ChatColor.RED + "Please wait a few moments before re-using your gadget.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack potentialGadget = event.getItemDrop().getItemStack();
        if(potentialGadget.hasItemMeta() && potentialGadget.getItemMeta().hasDisplayName()){
            for(Gadget gadget : plugin.getManager().getGadgets()) {
                if(potentialGadget.getItemMeta().getDisplayName().equals(gadget.getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
