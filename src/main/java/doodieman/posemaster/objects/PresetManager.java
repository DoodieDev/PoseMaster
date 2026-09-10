package doodieman.posemaster.objects;

import doodieman.posemaster.PoseMaster;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and stores global presets in plugins/PoseMaster/presets.yml.
 * All presets are shared between every player on the server.
 */
public class PresetManager {

    private final File file;
    private final List<Preset> presets = new ArrayList<>();

    public PresetManager() {
        this.file = new File(PoseMaster.getInstance().getDataFolder(), "presets.yml");
        this.load();
    }

    public List<Preset> getPresets() {
        return this.presets;
    }

    public Preset getPreset(String name) {
        for (Preset preset : this.presets) {
            if (preset.getName().equalsIgnoreCase(name)) return preset;
        }
        return null;
    }

    public void savePreset(Preset preset) {
        Preset existing = this.getPreset(preset.getName());
        if (existing != null) {
            this.presets.remove(existing);
        }
        this.presets.add(preset);
        this.save();
    }

    public void deletePreset(String name) {
        Preset preset = this.getPreset(name);
        if (preset == null) return;
        this.presets.remove(preset);
        this.save();
    }

    private void load() {
        if (!this.file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.file);
        ConfigurationSection section = config.getConfigurationSection("presets");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Preset preset = Preset.load(section.getConfigurationSection(key));
            if (preset != null) this.presets.add(preset);
        }
    }

    private void save() {
        YamlConfiguration config = new YamlConfiguration();

        for (int i = 0; i < this.presets.size(); i++) {
            Preset preset = this.presets.get(i);
            preset.save(config.createSection("presets." + i));
        }

        try {
            config.save(this.file);
        } catch (IOException exception) {
            PoseMaster.getInstance().getLogger().severe("Failed to save presets.yml: " + exception.getMessage());
        }
    }
}
