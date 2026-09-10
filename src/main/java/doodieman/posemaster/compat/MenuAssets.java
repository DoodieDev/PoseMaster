package doodieman.posemaster.compat;

import doodieman.posemaster.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Menu assets whose material names or sounds changed across versions
 * (mainly the 1.13 material rename). Resolved once at class load.
 */
public final class MenuAssets {

    private static final Material GRASS;
    private static final Material PISTON;
    private static final Material STONE_SLAB;
    private static final Material GOLDEN_LEGGINGS;
    private static final Material SIGN;
    private static final Material SHIELD;
    private static final Material CARPET;
    private static final Material WOOL;

    private static final ItemStack GRAY_GLASS_PANE;
    private static final ItemStack WHITE_GLASS_PANE;

    private static final String CLICK_SOUND;

    static {
        GRASS = resolve("DOUBLE_PLANT", "TALL_GRASS", "SHORT_GRASS");
        PISTON = resolve("PISTON_BASE", "PISTON");
        STONE_SLAB = resolve("STEP", "STONE_SLAB");
        GOLDEN_LEGGINGS = resolve("GOLD_LEGGINGS", "GOLDEN_LEGGINGS");
        SIGN = resolve("SIGN", "OAK_SIGN");
        SHIELD = resolve("SHIELD");
        CARPET = resolve("CARPET", "WHITE_CARPET");
        WOOL = resolve("WOOL", "WHITE_WOOL");

        GRAY_GLASS_PANE = buildPane("STAINED_GLASS_PANE", (short) 7, "GRAY_STAINED_GLASS_PANE", "");
        WHITE_GLASS_PANE = buildPane("STAINED_GLASS_PANE", (short) 0, "WHITE_STAINED_GLASS_PANE", "§r");

        CLICK_SOUND = ServerFeatures.hasEnum(Sound.class, "UI_BUTTON_CLICK") ? "ui.button.click" : "random.click";
    }

    private MenuAssets() {
    }

    public static Material grassMaterial() {
        return GRASS;
    }

    public static Material pistonMaterial() {
        return PISTON;
    }

    public static Material stoneSlabMaterial() {
        return STONE_SLAB;
    }

    public static Material goldenLeggingsMaterial() {
        return GOLDEN_LEGGINGS;
    }

    public static Material signMaterial() {
        return SIGN;
    }

    public static Material shieldMaterial() {
        return SHIELD;
    }

    public static Material carpetMaterial() {
        return CARPET;
    }

    public static Material woolMaterial() {
        return WOOL;
    }

    public static ItemStack grayGlassPane() {
        return GRAY_GLASS_PANE.clone();
    }

    public static ItemStack whiteGlassPane() {
        return WHITE_GLASS_PANE.clone();
    }

    /**
     * String-overload playSound exists on every version; the sound key differs
     * (1.8: random.click, 1.9+: ui.button.click).
     */
    public static void playClickSound(Player player) {
        player.playSound(player.getLocation(), CLICK_SOUND, 0.5f, 1.2f);
    }

    private static Material resolve(String... names) {
        Material material = ServerFeatures.resolveMaterial(names);
        return material != null ? material : Material.STONE;
    }

    /**
     * Legacy colored panes use durability, 1.13+ uses the named material.
     */
    private static ItemStack buildPane(String legacyName, short legacyDurability, String modernName, String displayName) {
        Material legacy = ServerFeatures.resolveMaterial(legacyName);
        if (legacy != null) {
            return new ItemBuilder(legacy, 1, legacyDurability, displayName).build();
        }
        Material modern = ServerFeatures.resolveMaterial(modernName);
        if (modern != null) {
            return new ItemBuilder(modern, displayName).build();
        }
        return new ItemBuilder(Material.STONE, displayName).build();
    }
}
