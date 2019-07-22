package io.github.scarger.gadgets.gadgetscore.component.gadget;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GadgetManager {

    private GadgetsCore plugin;

    private List<String> currentPlayers;

    //gadgets
    private List<Gadget> gadgets;

    public GadgetManager(GadgetsCore plugin) {
        this.plugin = plugin;

        gadgets = new ArrayList<>();
        registerGadgets();

        currentPlayers = new ArrayList<>();
    }

    private void registerGadgets() {
        gadgets.add(new RocketHeadGadget(plugin));
        gadgets.add(new IceHatchGadget(plugin));
        gadgets.add(new LaunchShotGadget(plugin));
    }

    public boolean hasGadgetInProgress(Player player) {
        return currentPlayers.contains(player.getUniqueId().toString());
    }

    public void add(Player player) {
        currentPlayers.add(player.getUniqueId().toString());
    }

    public void remove(Player player) {
        currentPlayers = currentPlayers
                .stream()
                .filter(uuid -> !uuid.equals(player.getUniqueId().toString()))
                .collect(Collectors.toList());
    }

    public List<Gadget> getGadgets() {
        return gadgets;
    }


}
