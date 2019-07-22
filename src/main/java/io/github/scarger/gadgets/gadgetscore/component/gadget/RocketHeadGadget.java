package io.github.scarger.gadgets.gadgetscore.component.gadget;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RocketHeadGadget extends Gadget {

    private final int maxHeight = 35;
    private final int highSpeedFactor = 3;

    public RocketHeadGadget(GadgetsCore plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return ChatColor.GOLD + "ROCKET HEAD";
    }

    @Override
    public ItemStack getItem(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwningPlayer(player);
        playerHead.setItemMeta(meta);
        return playerHead;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "Shoot your head into the sky",
                "and watch it " + ChatColor.RED + "explode!"
        );
    }

    @Override
    public void run(Player player) {
        super.run(player);

        final EntityArmorStand stand = setupStand(player, getItem(player));

        move(player, stand, 1);

    }

    private EntityArmorStand setupStand(Player player, ItemStack playerHead) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle(),
                player.getLocation().getX(),player.getLocation().getY() - 1.5, player.getLocation().getZ());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);

        PacketPlayOutSpawnEntity standPacket = new PacketPlayOutSpawnEntity(armorStand, 78);
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment
                (armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(playerHead));
        PacketPlayOutEntityMetadata metadataPacket =
                new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);

        sendPacket(standPacket);
        sendPacket(metadataPacket);
        sendPacket(equipmentPacket);

        return armorStand;
    }

    private void move(Player player, EntityArmorStand stand, int state) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(state < 10) {
                moveBall(stand.getId(), state, 1);
                addParticles(stand, state, 1);
            }
            else {
                moveBall(stand.getId(), state, highSpeedFactor);
                addParticles(stand, state, highSpeedFactor);
            }
            int updatedState = state + 1;
            if(state > maxHeight) {
                finish(player, stand);
            }
            else move(player, stand, updatedState);
        }, 2);
    }

    private void moveBall(int standID, int state, int speedFactor) {
        PacketPlayOutEntity.PacketPlayOutRelEntityMove movePacket =
                new PacketPlayOutEntity
                        .PacketPlayOutRelEntityMove(standID, (short) 0, (short) (1024 * speedFactor), (short) 0, false);
        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket =
                new PacketPlayOutEntity.PacketPlayOutEntityLook(standID, (byte) (25 * state * speedFactor), (byte) 0, false);

        sendPacket(movePacket);
        sendPacket(lookPacket);
    }

    private void addParticles(EntityArmorStand stand, int state, int speedFactor) {
        PacketPlayOutWorldParticles particlesPacket = new PacketPlayOutWorldParticles(
                Particles.FLAME, true, (float) stand.locX,
                (float) (stand.locY + ((state / 4) * speedFactor)), (float) stand.locZ, 0, 0, 0, 1, 21
        );
        sendPacket(particlesPacket);
    }

    private void finish(Player player, EntityArmorStand stand) {
        double x = stand.locX;
        double y = stand.locY + (maxHeight / 4.0) * highSpeedFactor;
        double z = stand.locZ;
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(stand.getId());
        sendPacket(destroyPacket);

        PacketPlayOutExplosion explosion =
                new PacketPlayOutExplosion(x, y, z, 3, new ArrayList<>(), new Vec3D(0, 0, 0));
        PacketPlayOutWorldParticles explosionParticles =
                new PacketPlayOutWorldParticles(Particles.CAMPFIRE_COSY_SMOKE, true, (float) x, (float) y, (float) z, 0, 0, 0, 1, 121);

        sendPacket(explosion);
        sendPacket(explosionParticles);

        plugin.getManager().remove(player);
    }


}