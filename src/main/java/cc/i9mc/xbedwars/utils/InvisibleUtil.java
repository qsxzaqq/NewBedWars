package cc.i9mc.xbedwars.utils;

import cc.i9mc.xbedwars.game.GamePlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class InvisibleUtil {
    public void hideEquip(GamePlayer gamePlayer, boolean hide) {
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment1 = new PacketPlayOutEntityEquipment(gamePlayer.getPlayer().getEntityId(), 4, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : gamePlayer.getPlayer().getEquipment().getHelmet()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(gamePlayer.getPlayer().getEntityId(), 3, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : gamePlayer.getPlayer().getEquipment().getChestplate()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment3 = new PacketPlayOutEntityEquipment(gamePlayer.getPlayer().getEntityId(), 2, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : gamePlayer.getPlayer().getEquipment().getLeggings()));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment4 = new PacketPlayOutEntityEquipment(gamePlayer.getPlayer().getEntityId(), 1, CraftItemStack.asNMSCopy(hide ? new ItemStack(Material.AIR) : gamePlayer.getPlayer().getEquipment().getBoots()));
        for (GamePlayer gamePlayer1 : GamePlayer.getOnlinePlayers()) {
            if (gamePlayer1.equals(gamePlayer)) {
                continue;
            }
            CraftPlayer craftPlayer = (CraftPlayer) gamePlayer1.getPlayer();

            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment1);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment2);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment3);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment4);
        }
    }
}
