package doodieman.posemaster.compat;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * Glow color (Entity#setGlowColor on 1.17-1.21.x, scoreboard-team fallback
     * elsewhere). Null when unset or unsupported.
     */
    public static Color getGlowColor(Entity entity) {
        if (!ServerFeatures.HAS_GLOW_COLOR) return null;
        if (ServerFeatures.glowColorGetter != null) {
            try {
                return (Color) ServerFeatures.glowColorGetter.invoke(entity);
            } catch (Exception exception) {
            }
        }
        return teamGetGlowColor(entity);
    }

    /** Sets the glow color; null resets to the default color. */
    public static void setGlowColor(Entity entity, Color color) {
        if (!ServerFeatures.HAS_GLOW_COLOR) return;
        if (ServerFeatures.glowColorSetter != null) {
            try {
                ServerFeatures.glowColorSetter.invoke(entity, color);
                return;
            } catch (Exception ignored) {
            }
        }
        teamSetGlowColor(entity, color);
    }

    // ------------------------------------------------------------------
    // Glow color via scoreboard teams (Team#setColor, 1.13+)
    //
    // Newer versions removed Entity#setGlowColor; the colored glow is
    // team-based there. Teams only support the 16 classic colors, so hex
    // input is snapped to the nearest one. One shared team per color on
    // the main scoreboard (only it is sent to clients).
    // ------------------------------------------------------------------

    private static final Map<ChatColor, Color> TEAM_COLOR_RGB = new LinkedHashMap<>();

    static {
        TEAM_COLOR_RGB.put(ChatColor.BLACK, Color.fromRGB(0x000000));
        TEAM_COLOR_RGB.put(ChatColor.DARK_BLUE, Color.fromRGB(0x0000AA));
        TEAM_COLOR_RGB.put(ChatColor.DARK_GREEN, Color.fromRGB(0x00AA00));
        TEAM_COLOR_RGB.put(ChatColor.DARK_AQUA, Color.fromRGB(0x00AAAA));
        TEAM_COLOR_RGB.put(ChatColor.DARK_RED, Color.fromRGB(0xAA0000));
        TEAM_COLOR_RGB.put(ChatColor.DARK_PURPLE, Color.fromRGB(0xAA00AA));
        TEAM_COLOR_RGB.put(ChatColor.GOLD, Color.fromRGB(0xFFAA00));
        TEAM_COLOR_RGB.put(ChatColor.GRAY, Color.fromRGB(0xAAAAAA));
        TEAM_COLOR_RGB.put(ChatColor.DARK_GRAY, Color.fromRGB(0x555555));
        TEAM_COLOR_RGB.put(ChatColor.BLUE, Color.fromRGB(0x5555FF));
        TEAM_COLOR_RGB.put(ChatColor.GREEN, Color.fromRGB(0x55FF55));
        TEAM_COLOR_RGB.put(ChatColor.AQUA, Color.fromRGB(0x55FFFF));
        TEAM_COLOR_RGB.put(ChatColor.RED, Color.fromRGB(0xFF5555));
        TEAM_COLOR_RGB.put(ChatColor.LIGHT_PURPLE, Color.fromRGB(0xFF55FF));
        TEAM_COLOR_RGB.put(ChatColor.YELLOW, Color.fromRGB(0xFFFF55));
        TEAM_COLOR_RGB.put(ChatColor.WHITE, Color.fromRGB(0xFFFFFF));
    }

    private static Color teamGetGlowColor(Entity entity) {
        try {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            if (board == null) return null;
            String entry = entity.getUniqueId().toString();
            for (Map.Entry<ChatColor, Color> teamColor : TEAM_COLOR_RGB.entrySet()) {
                Team team = board.getTeam(glowTeamName(teamColor.getKey()));
                if (team != null && team.hasEntry(entry)) return teamColor.getValue();
            }
        } catch (Exception exception) {
        }
        return null;
    }

    private static void teamSetGlowColor(Entity entity, Color color) {
        try {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            if (board == null) return;
            String entry = entity.getUniqueId().toString();

            //Reset: remove the entity from every plugin glow team
            if (color == null) {
                for (Map.Entry<ChatColor, Color> teamColor : TEAM_COLOR_RGB.entrySet()) {
                    Team team = board.getTeam(glowTeamName(teamColor.getKey()));
                    if (team != null && team.hasEntry(entry)) team.removeEntry(entry);
                }
                return;
            }

            ChatColor nearest = nearestChatColor(color);
            Team team = board.getTeam(glowTeamName(nearest));
            if (team == null) team = board.registerNewTeam(glowTeamName(nearest));
            ServerFeatures.teamColorSetter.invoke(team, nearest);
            team.addEntry(entry);
        } catch (Exception ignored) {
        }
    }

    private static ChatColor nearestChatColor(Color color) {
        ChatColor nearest = ChatColor.WHITE;
        double nearestDistance = Double.MAX_VALUE;
        for (Map.Entry<ChatColor, Color> teamColor : TEAM_COLOR_RGB.entrySet()) {
            double distance = colorDistanceSquared(color, teamColor.getValue());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = teamColor.getKey();
            }
        }
        return nearest;
    }

    private static double colorDistanceSquared(Color first, Color second) {
        int red = first.getRed() - second.getRed();
        int green = first.getGreen() - second.getGreen();
        int blue = first.getBlue() - second.getBlue();
        return red * red + green * green + blue * blue;
    }

    private static String glowTeamName(ChatColor color) {
        return "PMGlow_" + color.ordinal();
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
     * Abstract equipment-lock mask: bit i = the i-th legacy slot (main hand,
     * feet, legs, chest, head) is locked against BOTH placing and removing.
     * EQUIPMENT_LOCK_MASK (31) locks all five legacy slots. The off-hand
     * stays independently editable, matching the original 1.8 behavior.
     *
     * Each backend translates this abstract mask to its native encoding:
     * <ul>
     *   <li>NBT DisabledSlots: "adding or changing" bits 0-4 + "removing or
     *       changing" bits 8-12 per locked slot (mask 31 = 7967).</li>
     *   <li>Native API: LockType.ADDING_OR_CHANGING + LockType.REMOVING_OR_CHANGING
     *       per locked slot.</li>
     * </ul>
     */
    public static final int EQUIPMENT_LOCK_MASK = 0x1F;

    /** DisabledSlots "adding or changing" group for the five legacy slots (bits 0-4). */
    private static final int NBT_LOCK_ADDING_BITS = 0x1F;

    /** DisabledSlots "removing or changing" group for the five legacy slots (bits 8-12). */
    private static final int NBT_LOCK_REMOVING_BITS = 0x1F << 8;

    /**
     * Returns the abstract lock mask (bit i = legacy slot i fully locked).
     * The toggle logic reads back exactly what it writes on every version.
     */
    public static int getDisabledSlots(ArmorStand stand) {
        if (ServerFeatures.HAS_EQUIPMENT_LOCK_API) return nativeGetDisabledSlots(stand);
        return nbtGetDisabledSlots(stand);
    }

    /**
     * Applies the abstract lock mask. Locked slots are protected against
     * both placing and removing items; unlocked slots have every lock
     * removed.
     */
    public static void setDisabledSlots(ArmorStand stand, int mask) {
        if (ServerFeatures.HAS_EQUIPMENT_LOCK_API) {
            nativeSetDisabledSlots(stand, mask);
            return;
        }
        int locked = mask & EQUIPMENT_LOCK_MASK;
        int addingOrChanging = locked & NBT_LOCK_ADDING_BITS;
        int removingOrChanging = (locked << 8) & NBT_LOCK_REMOVING_BITS;
        nbtSetInteger(stand, "DisabledSlots", addingOrChanging | removingOrChanging);
    }

    private static int nativeGetDisabledSlots(ArmorStand stand) {
        int mask = 0;
        try {
            Method hasLock = ServerFeatures.findMethod(
                ArmorStand.class, "hasEquipmentLock", EquipmentSlot.class, ServerFeatures.lockTypeClass);
            for (int i = 0; i < LEGACY_SLOTS.length; i++) {
                if (nativeHasAnyLock(stand, LEGACY_SLOTS[i], hasLock)) mask |= (1 << i);
            }
        } catch (Exception ignored) {
        }
        return mask;
    }

    private static boolean nativeHasAnyLock(ArmorStand stand, EquipmentSlot slot, Method hasLock) {
        return nativeHasLock(stand, slot, hasLock, ServerFeatures.lockTypeAddingOrChanging)
            || nativeHasLock(stand, slot, hasLock, ServerFeatures.lockTypeRemovingOrChanging)
            || nativeHasLock(stand, slot, hasLock, ServerFeatures.lockTypeAdding);
    }

    private static boolean nativeHasLock(ArmorStand stand, EquipmentSlot slot, Method hasLock, Object lockType) {
        if (lockType == null) return false;
        try {
            return Boolean.TRUE.equals(hasLock.invoke(stand, slot, lockType));
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void nativeSetDisabledSlots(ArmorStand stand, int mask) {
        try {
            Method addLock = ServerFeatures.findMethod(
                ArmorStand.class, "addEquipmentLock", EquipmentSlot.class, ServerFeatures.lockTypeClass);
            Method removeLock = ServerFeatures.findMethod(ArmorStand.class, "removeEquipmentLock", EquipmentSlot.class);
            for (int i = 0; i < LEGACY_SLOTS.length; i++) {
                if ((mask & (1 << i)) != 0) {
                    if (ServerFeatures.lockTypeAddingOrChanging != null)
                        addLock.invoke(stand, LEGACY_SLOTS[i], ServerFeatures.lockTypeAddingOrChanging);
                    if (ServerFeatures.lockTypeRemovingOrChanging != null)
                        addLock.invoke(stand, LEGACY_SLOTS[i], ServerFeatures.lockTypeRemovingOrChanging);
                } else {
                    removeLock.invoke(stand, LEGACY_SLOTS[i]);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static int nbtGetDisabledSlots(ArmorStand stand) {
        int raw = nbtGetInteger(stand, "DisabledSlots");
        int mask = 0;
        for (int i = 0; i < LEGACY_SLOTS.length; i++) {
            int slotBits = (1 << i) | (1 << (i + 8)) | (1 << (i + 16));
            if ((raw & slotBits) != 0) mask |= (1 << i);
        }
        return mask;
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
