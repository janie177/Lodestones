package net.minegusta.lodestones.saving;

import net.md_5.bungee.api.ChatColor;
import net.minegusta.mglib.configs.ConfigurationModel;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Config extends ConfigurationModel {

	private String name;
	private boolean showTitleOnTeleport;
	private int cooldownSeconds;
	private Material lodestoneBlock;


	@Override
	public void onLoad(FileConfiguration conf) {
		this.name = conf.getString("interface-name", "&eLodestone Network");
		this.showTitleOnTeleport = conf.getBoolean("show-title-on-teleport", true);
		this.cooldownSeconds = conf.getInt("teleport-cooldown-seconds", 0);
		try {
			lodestoneBlock = Material.getMaterial(conf.getString("lodestone-block-material", "IRON_BLOCK"));
		} catch (Exception ignored)
		{
			lodestoneBlock = Material.IRON_BLOCK;
		}
	}

	@Override
	public void onSave(FileConfiguration conf) {

	}

	public String getName()
	{
		return ChatColor.translateAlternateColorCodes('&', name);
	}

	public String getNameRaw()
	{
		return name;
	}

	public boolean showTitleOnTeleport() {
		return showTitleOnTeleport;
	}

	public int getCooldownSeconds() {
		return cooldownSeconds;
	}

	public Material getLodestoneBlock() {
		return lodestoneBlock;
	}
}
