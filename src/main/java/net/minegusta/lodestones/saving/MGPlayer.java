package net.minegusta.lodestones.saving;

import com.google.common.collect.Lists;
import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.mglib.saving.mgplayer.MGPlayerModel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MGPlayer extends MGPlayerModel
{
	private List<String> unlocked = Lists.newArrayList();
	private long teleporting = 0;
	private int page = 1;

	public boolean hasUnlocked(String name)
	{
		return unlocked.contains(name.toLowerCase());
	}

	public void unlock(String name)
	{
		if(!hasUnlocked(name))
		{
			unlocked.add(name.toLowerCase());
		}
	}

	public void lock(String name)
	{
		if(hasUnlocked(name))
		{
			unlocked.remove(name.toLowerCase());
		}
	}

	public String getNameByIndex(int index)
	{
		return unlocked.get(index);
	}

	@Override
	public void onLoad(FileConfiguration fileConfiguration)
	{
		ConfigurationSection section;

		if((section = fileConfiguration.getConfigurationSection("lodestones")) != null)
		{
			section.getKeys(false).stream().filter(Storage::exists).forEach(this::unlock);
		}

		Storage.getAll().stream().filter(LodeStone::isDefaultUnlocked).forEach(ls -> unlock(ls.getName()));
	}

	@Override
	public void updateConf(FileConfiguration fileConfiguration)
	{
		ConfigurationSection section;
		if((section = fileConfiguration.getConfigurationSection("lodestones")) != null)
		{
			section.getKeys(false).stream().filter(s -> !unlocked.contains(s.toLowerCase())).forEach(s -> fileConfiguration.set("lodestones." + s.toLowerCase(), null));
		}

		unlocked.stream().filter(Storage::exists).forEach(s -> fileConfiguration.set("lodestones." + s.toLowerCase(), true));
	}

	public boolean isTeleporting()
	{
		return System.currentTimeMillis() < teleporting;
	}

	public void setTeleporting() {
		this.teleporting = System.currentTimeMillis() + 8500;
	}

	public int unlockedSize()
	{
		return unlocked.size();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
