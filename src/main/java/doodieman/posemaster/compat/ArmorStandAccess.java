package doodieman.posemaster.compat;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * All armor-stand state that touches version-specific APIs.
 *
 * NBT (via the shaded NBT API) is only used as a fallback where no Bukkit
 * API exists: the Invulnerable flag and the DisabledSlots equipment lock mask
 * on 1.8.8. All entity NBT access must happen on the primary thread.
 */
public final class ArmorStandAccess {

    private static final EquipmentSlot[] LEGACY_SLOTS = {
        EquipmentSlot.HAND,
        EquipmentSlot.FEET,
        EquipmentSlot.LEGS,
        EquipmentSlot.CHEST,
        EquipmentSlot.HEAD
    };

    private ArmorStandAccess() {
    }

    // ------------------------------------------------------------------
    // Main hand / off hand
    // ------------------------------------------------------------------

    /**
     * Null-safe main-hand resolver. Never returns null (AIR when empty).
     * Uses getItemInMainHand when present (1.9+), getItemInHand otherwise.
     */
    public static ItemStack getMainHand(EntityEquipment equipment) {
        if (equipment == null) return new ItemStack(Material.AIR);
        ItemStack item = null;
        Method method = ServerFeatures.findMethod(EntityEquipment.class, "getItemInMainHand");
        try {
            item = method != null ? (ItemStack) method.invoke(equipment) : equipment.getItemInHand();
        } catch (Exception exception) {
            item = equipment.getItemInHand();
        }
        return item != null ? item : new ItemStack(Material.AIR);
    }

    public static ItemStack getMainHand(ArmorStand stand) {
        return getMainHand(stand.getEquipment());
    }

    public static void setMainHand(ArmorStand stand, ItemStack item) {
        ItemStack value = item != null ? item : new ItemStack(Material.AIR);
        Method method = ServerFeatures.findMethod(EntityEquipment.class, "setItemInMainHand", ItemStack.class);
        try {
            if (method != null) {
                method.invoke(stand.getEquipment(), value);
            } else {
                stand.getEquipment().setItemInHand(value);
            }
        } catch (Exception exception) {
            stand.getEquipment().setItemInHand(value);
        }
    }

    /** 1.9+; returns null when off-hand is unsupported. */
    public static ItemStack getOffHand(ArmorStand stand) {
        if (!ServerFeatures.HAS_OFF_HAND) return null;
        try {
            Method method = ServerFeatures.findMethod(EntityEquipment.class, "getItemInOffHand");
            ItemStack item = (ItemStack) method.invoke(stand.getEquipment());
            return item != null ? item : new ItemStack(Material.AIR);
        } catch (Exception exception) {
            return null;
        }
    }

    /** No-op when off-hand is unsupported. */
    public static void setOffHand(ArmorStand stand, ItemStack item) {
        if (!ServerFeatures.HAS_OFF_HAND) return;
        try {
            Method method = ServerFeatures.findMethod(EntityEquipment.class, "setItemInOffHand", ItemStack.class);
            method.invoke(stand.getEquipment(), item != null ? item : new ItemStack(Material.AIR));
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------
    // Invulnerable (1.9+ API, NBT fallback on 1.8.8)
    // ------------------------------------------------------------------

    public static boolean isInvulnerable(ArmorStand stand) {
        if (ServerFeatures.HAS_INVULNERABLE) {
            try {
                Method method = ServerFeatures.findMethod(Entity.class, "isInvulnerable");
                return Boolean.TRUE.equals(method.invoke(stand));
            } catch (Exception ignored) {
            }
        }
        return nbtGetInteger(stand, "Invulnerable") == 1;
    }

    public static void setInvulnerable(ArmorStand stand, boolean value) {
        if (ServerFeatures.HAS_INVULNERABLE) {
            try {
                Method method = ServerFeatures.findMethod(Entity.class, "setInvulnerable", boolean.class);
                method.invoke(stand, value);
                return;
            } catch (Exception ignored) {
            }
        }
        nbtSetInteger(stand, "Invulnerable", value ? 1 : 0);
    }

    // ------------------------------------------------------------------
    // Glow / silent (1.9+)
    // ------------------------------------------------------------------

    public static boolean isGlowing(Entity entity) {
        if (!ServerFeatures.HAS_GLOW) return false;
        try {
            Method method = ServerFeatures.findMethod(Entity.class, "isGlowing");
            return Boolean.TRUE.equals(method.invoke(entity));
        } catch (Exception exception) {
            return false;
        }
    }

    public static void setGlowing(Entity entity, boolean value) {
        if (!ServerFeatures.HAS_GLOW) return;
        try {
            Method method = ServerFeatures.findMethod(Entity.class, "setGlowing", boolean.class);
            method.invoke(entity, value);
        } catch (Exception ignored) {
        }
    }

    public static boolean isSilent(Entity entity) {
        if (!ServerFeatures.HAS_SILENT) return false;
        try {
            Method method = ServerFeatures.findMethod(Entity.class, "isSilent");
            return Boolean.TRUE.equals(method.invoke(entity));
        } catch (Exception exception) {
            return false;
        }
    }

    public static void setSilent(Entity entity, boolean value) {
        if (!ServerFeatures.HAS_SILENT) return;
        try {
            Method method = ServerFeatures.findMethod(Entity.class, "setSilent", boolean.class);
            method.invoke(entity, value);
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------
    // Equipment locks (native API on 1.16.2+, NBT DisabledSlots fallback)
    // ------------------------------------------------------------------

    /**
     * Returns the exact legacy DisabledSlots mask (31 = the five 1.8 slots
     * locked against removal). The toggle logic reads back exactly what it
     * writes on every version.
     */
    public static int getDisabledSlots(ArmorStand stand) {
        if (ServerFeatures.HAS_EQUIPMENT_LOCK_API) return nativeGetDisabledSlots(stand);
        return nbtGetInteger(stand, "DisabledSlots");
    }

    /**
     * Writes the legacy DisabledSlots mask. On the native path the mask is
     * mapped to REMOVING_OR_CHANGING locks on the five legacy slots; the
     * off-hand stays independently editable. Mask 31 is preserved exactly.
     */
    public static void setDisabledSlots(ArmorStand stand, int mask) {
        if (ServerFeatures.HAS_EQUIPMENT_LOCK_API) {
            nativeSetDisabledSlots(stand, mask);
            return;
        }
        nbtSetInteger(stand, "DisabledSlots", mask);
    }

    private static int nativeGetDisabledSlots(ArmorStand stand) {
        int mask = 0;
        try {
            Method hasLock = ServerFeatures.findMethod(
                ArmorStand.class, "hasEquipmentLock", EquipmentSlot.class, ServerFeatures.lockTypeClass);
            for (int i = 0; i < LEGACY_SLOTS.length; i++) {
                boolean locked = Boolean.TRUE.equals(hasLock.invoke(
                    stand, LEGACY_SLOTS[i], ServerFeatures.lockTypeRemovingOrChanging));
                if (locked) mask |= (1 << i);
            }
        } catch (Exception ignored) {
        }
        return mask;
    }

    private static void nativeSetDisabledSlots(ArmorStand stand, int mask) {
        try {
            Method addLock = ServerFeatures.findMethod(
                ArmorStand.class, "addEquipmentLock", EquipmentSlot.class, ServerFeatures.lockTypeClass);
            Method removeLock = ServerFeatures.findMethod(ArmorStand.class, "removeEquipmentLock", EquipmentSlot.class);
            for (int i = 0; i < LEGACY_SLOTS.length; i++) {
                if ((mask & (1 << i)) != 0) {
                    addLock.invoke(stand, LEGACY_SLOTS[i], ServerFeatures.lockTypeRemovingOrChanging);
                } else {
                    removeLock.invoke(stand, LEGACY_SLOTS[i]);
                }
            }
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------
    // Scale (1.20.5+)
    // ------------------------------------------------------------------

    public static boolean hasScale(ArmorStand stand) {
        return ServerFeatures.HAS_SCALE;
    }

    public static double getScale(ArmorStand stand) {
        if (!ServerFeatures.HAS_SCALE) return 1.0;
        try {
            Object instance = ServerFeatures.getAttributeMethod.invoke(stand, ServerFeatures.scaleAttribute);
            if (instance == null) return 1.0;
            Object value = ServerFeatures.getBaseValueMethod.invoke(instance);
            return value instanceof Number ? ((Number) value).doubleValue() : 1.0;
        } catch (Exception exception) {
            return 1.0;
        }
    }

    /** Clamped to Minecraft's valid range 0.0625 - 16.0. */
    public static void setScale(ArmorStand stand, double value) {
        if (!ServerFeatures.HAS_SCALE) return;
        double clamped = Math.max(0.0625, Math.min(16.0, value));
        try {
            Object instance = ServerFeatures.getAttributeMethod.invoke(stand, ServerFeatures.scaleAttribute);
            if (instance == null) return;
            ServerFeatures.setBaseValueMethod.invoke(instance, clamped);
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------
    // NBT helpers (fallback only; entity NBT access, primary thread only)
    // ------------------------------------------------------------------

    private static int nbtGetInteger(ArmorStand stand, String key) {
        try {
            Integer value = NBT.get(stand, nbt -> {
                return nbt.getInteger(key);
            });
            return value != null ? value : 0;
        } catch (Exception exception) {
            return 0;
        }
    }

    private static void nbtSetInteger(ArmorStand stand, String key, int value) {
        try {
            NBT.modify(stand, nbt -> {
                nbt.setInteger(key, value);
            });
        } catch (Exception ignored) {
        }
    }
}
