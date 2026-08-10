package doodieman.posemaster.compat;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Runtime capability detection for version-gated features.
 *
 * Detected once at class load and cached. Detection never parses the server
 * version string; it probes for methods, materials and registry keys instead.
 * All version-specific code lives in this package.
 */
public final class ServerFeatures {

    /** EntityEquipment#getItemInOffHand / setItemInOffHand (1.9+) */
    public static final boolean HAS_OFF_HAND;
    /** Entity#setInvulnerable (1.9+) */
    public static final boolean HAS_INVULNERABLE;
    /** Entity#setGlowing (1.9+) */
    public static final boolean HAS_GLOW;
    /** Entity#setSilent (1.9+) */
    public static final boolean HAS_SILENT;
    /** ArmorStand#addEquipmentLock / removeEquipmentLock / hasEquipmentLock (1.16.2+) */
    public static final boolean HAS_EQUIPMENT_LOCK_API;
    /** minecraft:scale attribute present in the server registry (1.20.5+) */
    public static final boolean HAS_SCALE;

    // Scale attribute support (1.20.5+), resolved reflectively.
    static Class<?> attributeClass;              // org.bukkit.attribute.Attribute
    static Class<?> attributableClass;           // org.bukkit.attribute.Attributable
    static Class<?> attributeInstanceClass;      // org.bukkit.attribute.AttributeInstance
    static Method getAttributeMethod;            // Attributable#getAttribute(Attribute)
    static Method setBaseValueMethod;            // AttributeInstance#setBaseValue(double)
    static Method getBaseValueMethod;            // AttributeInstance#getBaseValue()
    static Object scaleAttribute;                // Attribute for minecraft:scale
    static Method registryGetMethod;             // Registry#get(NamespacedKey)
    static Object attributeRegistry;             // Registry.ATTRIBUTE
    static Method namespacedKeyMinecraftMethod;  // NamespacedKey#minecraft(String)

    // Equipment lock native API support (1.16.2+).
    static Class<?> lockTypeClass;               // org.bukkit.entity.ArmorStand$LockType
    static Object lockTypeAddingOrChanging;      // LockType.ADDING_OR_CHANGING
    static Object lockTypeRemovingOrChanging;    // LockType.REMOVING_OR_CHANGING
    static Object lockTypeAdding;                // LockType.ADDING (optional)

    private static final Map<MethodKey, Method> METHOD_CACHE = new HashMap<>();

    static {
        HAS_OFF_HAND = hasMethod("org.bukkit.inventory.EntityEquipment", "getItemInOffHand");
        HAS_INVULNERABLE = hasMethod("org.bukkit.entity.Entity", "setInvulnerable", boolean.class);
        HAS_GLOW = hasMethod("org.bukkit.entity.Entity", "setGlowing", boolean.class);
        HAS_SILENT = hasMethod("org.bukkit.entity.Entity", "setSilent", boolean.class);
        HAS_EQUIPMENT_LOCK_API = detectEquipmentLockApi();
        HAS_SCALE = detectScaleSupport();
    }

    private ServerFeatures() {
    }

    /**
     * Initializes the shaded NBT API. Must be called on the primary thread
     * before any entity NBT access. Returns false when initialization failed.
     */
    public static boolean preloadNbtApi() {
        try {
            return NBT.preloadApi();
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void logStatus(Logger logger) {
        logger.info("Detected server features: off-hand=" + HAS_OFF_HAND
            + ", invulnerable-api=" + HAS_INVULNERABLE
            + ", glow=" + HAS_GLOW
            + ", silent=" + HAS_SILENT
            + ", equipment-lock-api=" + HAS_EQUIPMENT_LOCK_API
            + ", scale=" + HAS_SCALE);
        if (!HAS_SCALE) logger.warning("Scale support not detected; the Scale option will be hidden in the GUI.");
        if (!HAS_OFF_HAND) logger.warning("Off-hand support not detected; the Off-Hand slot will be hidden in the GUI.");
    }

    /**
     * Cached method lookup. Returns null when the method does not exist.
     */
    public static Method findMethod(Class<?> owner, String name, Class<?>... params) {
        MethodKey key = new MethodKey(owner, name, params);
        Method cached = METHOD_CACHE.get(key);
        if (cached != null || METHOD_CACHE.containsKey(key)) return cached;
        try {
            cached = owner.getMethod(name, params);
        } catch (Exception exception) {
            cached = null;
        }
        METHOD_CACHE.put(key, cached);
        return cached;
    }

    public static boolean hasMethod(String className, String name, Class<?>... params) {
        try {
            return findMethod(Class.forName(className), name, params) != null;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    /**
     * Resolves a material by trying the given names in order (pre-1.13 legacy
     * names first). Returns null when none of the names exist.
     */
    public static Material resolveMaterial(String... names) {
        for (String name : names) {
            try {
                return Material.valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    /**
     * Whether the given enum constant exists (e.g. Sound.UI_BUTTON_CLICK).
     */
    public static boolean hasEnum(Class<? extends Enum<?>> enumClass, String constant) {
        return enumConstant(enumClass, constant) != null;
    }

    private static boolean detectEquipmentLockApi() {
        try {
            lockTypeClass = null;
            try {
                lockTypeClass = Class.forName("org.bukkit.entity.ArmorStand$LockType");
            } catch (ClassNotFoundException ignored) {
            }
            if (lockTypeClass == null) {
                try {
                    lockTypeClass = Class.forName("org.bukkit.inventory.EquipmentSlot$LockType");
                } catch (ClassNotFoundException ignored) {
                }
            }
            if (lockTypeClass == null) return false;
            lockTypeRemovingOrChanging = enumConstant(lockTypeClass, "REMOVING_OR_CHANGING");
            lockTypeAddingOrChanging = enumConstant(lockTypeClass, "ADDING_OR_CHANGING");
            if (lockTypeRemovingOrChanging == null || lockTypeAddingOrChanging == null) return false;
            lockTypeAdding = enumConstant(lockTypeClass, "ADDING");
            Class<?> armorStandClass = Class.forName("org.bukkit.entity.ArmorStand");
            return findMethod(armorStandClass, "addEquipmentLock", EquipmentSlot.class, lockTypeClass) != null
                && findMethod(armorStandClass, "removeEquipmentLock", EquipmentSlot.class) != null
                && findMethod(armorStandClass, "hasEquipmentLock", EquipmentSlot.class, lockTypeClass) != null;
        } catch (Throwable throwable) {
            return false;
        }
    }

    private static boolean detectScaleSupport() {
        try {
            attributeClass = Class.forName("org.bukkit.attribute.Attribute");
            attributableClass = Class.forName("org.bukkit.attribute.Attributable");
            attributeInstanceClass = Class.forName("org.bukkit.attribute.AttributeInstance");
            getAttributeMethod = attributableClass.getMethod("getAttribute", attributeClass);
            setBaseValueMethod = attributeInstanceClass.getMethod("setBaseValue", double.class);
            getBaseValueMethod = attributeInstanceClass.getMethod("getBaseValue");
        } catch (Throwable throwable) {
            return false;
        }

        // Registry path (1.21.3+): Registry.ATTRIBUTE.get(NamespacedKey.minecraft("scale"))
        try {
            Class<?> registryClass = Class.forName("org.bukkit.Registry");
            Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");
            namespacedKeyMinecraftMethod = namespacedKeyClass.getMethod("minecraft", String.class);
            registryGetMethod = registryClass.getMethod("get", namespacedKeyClass);
            Field attributeRegistryField = registryClass.getField("ATTRIBUTE");
            attributeRegistry = attributeRegistryField.get(null);
            Object key = namespacedKeyMinecraftMethod.invoke(null, "scale");
            scaleAttribute = registryGetMethod.invoke(attributeRegistry, key);
            if (scaleAttribute != null) return true;
        } catch (Throwable ignored) {
        }

        // Enum path (1.20.5-1.20.6): Attribute.GENERIC_SCALE
        scaleAttribute = enumConstant(attributeClass, "GENERIC_SCALE");
        return scaleAttribute != null;
    }

    private static Object enumConstant(Class<?> enumClass, String name) {
        try {
            Method valueOf = enumClass.getMethod("valueOf", String.class);
            return valueOf.invoke(null, name);
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static final class MethodKey {
        private final Class<?> owner;
        private final String name;
        private final Class<?>[] params;

        private MethodKey(Class<?> owner, String name, Class<?>[] params) {
            this.owner = owner;
            this.name = name;
            this.params = params;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof MethodKey)) return false;
            MethodKey key = (MethodKey) other;
            return owner.equals(key.owner) && name.equals(key.name) && Arrays.equals(params, key.params);
        }

        @Override
        public int hashCode() {
            return owner.hashCode() * 31 + name.hashCode() * 31 + Arrays.hashCode(params);
        }
    }
}
