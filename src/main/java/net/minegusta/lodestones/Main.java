package net.minegusta.lodestones;

import net.minegusta.lodestones.commands.LodeStoneCommand;
import net.minegusta.lodestones.dynmap.DynMapUtil;
import net.minegusta.lodestones.gui.TeleportGUI;
import net.minegusta.lodestones.listeners.LodeStoneListener;
import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.lodestones.saving.Config;
import net.minegusta.lodestones.saving.LodeStoneSave;
import net.minegusta.lodestones.saving.MGPlayer;
import net.minegusta.mglib.configs.ConfigurationFileManager;
import net.minegusta.mglib.saving.mgplayer.PlayerSaveManager;
import net.minegusta.mglib.tasks.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Plugin plugin;
	private static PlayerSaveManager<MGPlayer> saveManager;
	private static ConfigurationFileManager<Config> configManager;
	private static ConfigurationFileManager<LodeStoneSave> lodestoneSavesManager;
	private static TeleportGUI gui;
	private static Task animationTask;
	private static DynMapUtil DYNMAP_UTIL;
	private static boolean DYNMAP_ENABLED = false;

	public static PlayerSaveManager<MGPlayer> getSaveManager() {
		return saveManager;
	}

	public static ConfigurationFileManager<Config> getConfigManager() {
		return configManager;
	}

	public static ConfigurationFileManager<LodeStoneSave> getLodestoneSavesManager() {
		return lodestoneSavesManager;
	}

	public static TeleportGUI getTeleportGUI() {
		return gui;
	}

	public static boolean isDynmapEnabled() {
		return DYNMAP_ENABLED;
	}

	@Override
	public void onEnable() {
		//Initialize this instance of the plugin.
		plugin = this;

		//Init dynmap support if dynmap is present.
		if(Bukkit.getPluginManager().isPluginEnabled("dynmap"))
		{
			DYNMAP_ENABLED = true;
			DYNMAP_UTIL = new DynMapUtil();
		}

		//Create a new save manager which saves players every 3 minutes.
		saveManager = new PlayerSaveManager<>(plugin, MGPlayer.class, 60);

		//Create a new config handler
		configManager = new ConfigurationFileManager<>(plugin, Config.class, 0, "configuration");

		//Create the lodestone save file.
		lodestoneSavesManager = new ConfigurationFileManager<>(plugin, LodeStoneSave.class, 60, "lodestones");

		//Initialize the GUI.
		gui = new TeleportGUI(getConfigManager().getConfigClass().getName(), 2, "lodestonesnetworkgui");

		//Initialize the task for animation.
		animationTask = new Task();
		//start the task
		animationTask.start(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () ->
		{
			LodeStone.calculateAnimation();
			Storage.getAll().stream().forEach(LodeStone::idleAnimate);
		}, 2, 2));

		//Register commands
		getCommand("lodestones").setExecutor(new LodeStoneCommand());

		//Listener
		getServer().getPluginManager().registerEvents(new LodeStoneListener(), this);
	}

	@Override
	public void onDisable() {
		//Save everything
		saveManager.saveAllMGPlayers();
		lodestoneSavesManager.saveConfig();
	}

	public static Plugin getPlugin()
	{
		return plugin;
	}

	public static DynMapUtil getDynmapUtil()
	{
		return DYNMAP_UTIL;
	}
}
