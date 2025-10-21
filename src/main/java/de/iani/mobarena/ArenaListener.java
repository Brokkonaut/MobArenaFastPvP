package de.iani.mobarena;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.types.BossWave;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Set;

public class ArenaListener implements Listener {
    private final MobArenaAddOns plugin;

    private static final NamespacedKey attackSpeedKey = NamespacedKey.fromString("mobarenaaddons:attackspeed");
    private static final NamespacedKey attackDamageKey = NamespacedKey.fromString("mobarenaaddons:attackdamage");

    private HashMap<String, Wave> lastWaves;

    public ArenaListener(MobArenaAddOns plugin) {
        this.plugin = plugin;
        lastWaves = new HashMap<>();
    }

    @EventHandler
    public void onMobArenaStart(ArenaStartEvent e) {
        lastWaves.put(e.getArena().configName(), null);
    }

    @EventHandler
    public void onMobArenaNewWave(NewWaveEvent e) {
        fastPvPPlayers(e.getArena());

        if (plugin.isEventTime()) {
            Arena arena = e.getArena();
            Wave wave = e.getWave();
            String arenaName = arena.configName();
            Set<Player> playersInArena = arena.getPlayersInArena();
            Wave lastWave = lastWaves.get(arenaName);
            if (lastWave != null) {
                plugin.addRoundWonScore(playersInArena, (lastWave instanceof BossWave) ? 5 : 1);
            }
            lastWaves.put(arenaName, wave);
        }
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
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(attackSpeedKey, 1.5, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(attackDamageKey, addDamage, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
