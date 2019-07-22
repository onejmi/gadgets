package io.github.scarger.gadgets.gadgetscore.command;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GadgetsCommand implements CommandExecutor {

    private GadgetsCore plugin;

    public GadgetsCommand(GadgetsCore plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if (sender.isOp() || sender.hasPermission("*") || sender.hasPermission("gadgets.*")
                    || sender.hasPermission(plugin.GADGETS_PERMISSION)) {
                Player player = (Player) sender;
                plugin.getMenu().openMenu(player);
            } else sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
        } else {
            sender.sendMessage("Only players can run that command!");
        }
        return false;
    }
}
