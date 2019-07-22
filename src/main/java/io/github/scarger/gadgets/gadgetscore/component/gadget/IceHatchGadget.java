package io.github.scarger.gadgets.gadgetscore.component.gadget;

import io.github.scarger.gadgets.gadgetscore.GadgetsCore;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutExplosion;
import net.minecraft.server.v1_14_R1.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IceHatchGadget extends Gadget {

    private final int[][] blockLocations = {
            {1, 0, 0},
            {-1, 0, 0},
            {0, 0, 1},
            {0, 0, -1},
            {1, 1, 0},
            {-1, 1, 0},
            {0, 1, 1},
            {0, 1, -1},
            {0, 2, 0}
    };

    private BlockData iceBlockData;

    public IceHatchGadget(GadgetsCore plugin) {
        super(plugin);

        iceBlockData = Material.BLUE_ICE.createBlockData();
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "ICE HATCH";
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemStack(Material.HEART_OF_THE_SEA);
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.BLUE + "Creates a small ice cage around you",
                ChatColor.BLUE + "which cracks until it's broken."
        );
    }

    @Override
    public void run(Player player) {
        super.run(player);
        BlockData oldBottomData = setup(player);

        hatch(player, 1, oldBottomData);
    }

    @Override
    public boolean isValid(Player player) {
        return player.isOnGround();
    }

    @Override
    public String getInvalidMessage() {
        return ChatColor.RED + "You can only run this gadget when on the ground!";
    }

    private BlockData setup(Player player) {
        Location bottomBlockLocation = player.getLocation().add(new Vector(0, -1, 0));
        BlockData oldBlockData = player.getWorld().getBlockAt(bottomBlockLocation).getBlockData();
        for(Player user : Bukkit.getOnlinePlayers()) {
            for(int[] relativeLocation : blockLocations) {
                user.sendBlockChange(player
                                .getLocation()
                                .add(new Vector(relativeLocation[0], relativeLocation[1], relativeLocation[2])),
                        iceBlockData);
            }
            user.sendBlockChange(bottomBlockLocation, Material.GLOWSTONE.createBlockData());
        }
        return oldBlockData;
    }

    private void hatch(Player player, int state, BlockData oldBottomData) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int blockEntityID = 0;
            if(state < 10) {
                for(int[] relativeLocation : blockLocations) {
                    BlockPosition position = toBlockPosition(relativeLocation, player);
                    triggerBreakAnimation(position, state, blockEntityID);
                    blockEntityID++;
                }
                hatch(player, state + 1, oldBottomData);
            }
            else {
                //explode
                List<BlockPosition> explodedBlocks = Arrays.stream(blockLocations)
                        .map(pos -> toBlockPosition(pos, player))
                        .collect(Collectors.toList());

                for(BlockPosition position : explodedBlocks) {
                    triggerBreakAnimation(position, 0, blockEntityID);
                    blockEntityID++;
                }

                player.sendBlockChange(player.getLocation().add(new Vector(0, -1, 0)), oldBottomData);

                PacketPlayOutExplosion explosion =
                        new PacketPlayOutExplosion(player.getLocation().getX(),
                                player.getLocation().getY(),
                                player.getLocation().getZ(),
                                5,
                                explodedBlocks,
                                new Vec3D(0, 2, 0)
                        );
                sendPacket(explosion);

                Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getManager().remove(player), 65);
            }
        }, 5);
    }

    private BlockPosition toBlockPosition(int[] relativeLocation, Player player) {
        Location location = player.getLocation().add(
                new Vector(relativeLocation[0], relativeLocation[1], relativeLocation[2])
        );
        return new BlockPosition(location.getX(), location.getY(), location.getZ());
    }

    private void triggerBreakAnimation(BlockPosition position, int state, int blockEntityID) {
        PacketPlayOutBlockBreakAnimation breakAnimation =
                new PacketPlayOutBlockBreakAnimation(blockEntityID, position, state);
        sendPacket(breakAnimation);
    }


}
