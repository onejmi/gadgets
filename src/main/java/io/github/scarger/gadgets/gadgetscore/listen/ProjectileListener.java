package io.github.scarger.gadgets.gadgetscore.listen;

import net.minecraft.server.v1_14_R1.PacketPlayOutExplosion;
import net.minecraft.server.v1_14_R1.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class ProjectileListener implements Listener {

    @EventHandler
    public void onShoot(ProjectileHitEvent event) {
        if(event.getHitBlock() != null) {
            Projectile projectile = event.getEntity();
            if (projectile.hasMetadata("gadget")) {
                event.getEntity().remove();
                //currently only one gadget shoots a projectile which has custom metadata, which is LaunchShotGadget
                MetadataValue value = projectile.getMetadata("player").get(0);
                UUID uuid = UUID.fromString(value.asString());
                Player player = Bukkit.getPlayer(uuid);

                if(player != null && player.isOnline()) {
                    fire(player, event.getHitBlock().getLocation());
                }
            }
        }
    }

    private void fire(Player player, Location location) {
        Location playerLoc = player.getLocation();
        Vector trajectory = new Vector(
                location.getX() - playerLoc.getX(),
                location.getY() - playerLoc.getY(),
                location.getZ() - playerLoc.getZ()
        ).normalize().multiply(7);

        PacketPlayOutExplosion launchPacket =
                new PacketPlayOutExplosion(
                        playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(),
                        1,
                        new ArrayList<>(),
                        new Vec3D(trajectory.getX(), trajectory.getY(), trajectory.getZ())
                );

        for(Player user : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) user).getHandle().playerConnection.sendPacket(launchPacket);
        }
    }
}
