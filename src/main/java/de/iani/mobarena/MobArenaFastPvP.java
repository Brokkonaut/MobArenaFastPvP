package de.iani.mobarena;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;

public class MobArenaFastPvP extends JavaPlugin implements Listener {
    private static final UUID attackSpeedUUID = UUID.fromString("34f5963d-3fc6-474b-a576-c4f05c2af419");
    private static final UUID attackDamageUUID = UUID.fromString("ea434c08-d745-4b5c-858e-1db76cc70088");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onMobArenaNewWave(NewWaveEvent e) {
        fastPvPPlayers(e.getArena());
    }

    private void fastPvPPlayers(Arena arena) {
        for (ArenaPlayer p : arena.getArenaPlayerSet()) {
            PlayerInventory inv = p.getPlayer().getInventory();
            int size = inv.getSize();
            for (int i = 0; i < size; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack != null) {
                    Material t = stack.getType();
                    if (t == Material.WOODEN_SWORD || t == Material.GOLDEN_SWORD || t == Material.STONE_SWORD || t == Material.IRON_SWORD || t == Material.DIAMOND_SWORD) {
                        inv.setItem(i, fastPvP(stack));
                    }
                }
            }
        }
    }

    public ItemStack fastPvP(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        Material type = itemStack.getType();
        double addDamage = Double.NaN;
        if (type == Material.WOODEN_SWORD) {
            addDamage = 3;
        } else if (type == Material.GOLDEN_SWORD) {
            addDamage = 3;
        } else if (type == Material.STONE_SWORD) {
            addDamage = 4;
        } else if (type == Material.IRON_SWORD) {
            addDamage = 5;
        } else if (type == Material.DIAMOND_SWORD) {
            addDamage = 6;
        }
        if (!Double.isNaN(addDamage)) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasAttributeModifiers()) {
                return itemStack;
            }
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(attackSpeedUUID, "1.8-attackspeed", 1.5, Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(attackDamageUUID, "1.8-attackdamage", addDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND));

            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
