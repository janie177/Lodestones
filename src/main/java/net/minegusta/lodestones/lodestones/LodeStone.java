package net.minegusta.lodestones.lodestones;

import net.minegusta.lodestones.Main;
import net.minegusta.lodestones.saving.MGPlayer;
import net.minegusta.mglib.bossbars.BossBarUtil;
import net.minegusta.mglib.utils.EffectUtil;
import net.minegusta.mglib.utils.LocationUtil;
import net.minegusta.mglib.utils.TitleUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.List;

public class LodeStone {

	private String name = "name";
	private String description = "description";
	private Location location = Main.getPlugin().getServer().getWorlds().get(0).getSpawnLocation();
	private boolean defaultUnlocked = false;
	private String permission = "lodestones.use";
	private Material material = Material.DIAMOND;
	private int dataValue = 0;
	private int cost = 0;
	private Effect swirlEffect = Effect.WITCH_MAGIC;
	private Effect centerEffect = Effect.SPELL;
	private Material costMaterial = Material.GOLD_NUGGET;
	private boolean useMoney = false;
	private String displayName;
	private boolean showOnMap = true;

	private LodeStone(String name) {
		this.name = name;
		this.displayName = ChatColor.translateAlternateColorCodes('&', name);
	}

	static LodeStone createLodeStone(String name)
	{
		return new LodeStone(name);
	}

	public String getName() {
		return name;
	}

	public World getWorld()
	{
		if(location != null && location.getWorld() != null && Bukkit.getWorlds().contains(location.getWorld()))
		{
			return location.getWorld();
		}
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = ChatColor.translateAlternateColorCodes('&', description);
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * Make sure this lodestone is actually loaded in an active world before doing anything.
	 * @return A boolean indicating if the lodestone is loaded in a loaded chunk in an existing world.
	 */
	public boolean isLoaded()
	{
		return location != null && location.getWorld() != null && Bukkit.getWorlds().contains(location.getWorld()) && location.getChunk().isLoaded();
	}

	private static int rotationAngle = 0;
	private static double xOffset = 0;
	private static double zOffset = 0;

	public static void calculateAnimation()
	{
		rotationAngle = rotationAngle < 354 ? rotationAngle+6 : 0;
		xOffset = 2.2 * Math.sin(rotationAngle);
		zOffset = 2.2 * Math.cos(rotationAngle);
	}

	public void idleAnimate()
	{
		if(isLoaded())
		{
			location.getWorld().spigot().playEffect(location.clone().add(0, 0.1F, 0), centerEffect, 0, 0, 0.2F, 0, 0.2F, 1 / 30, 3, 35);
			location.getWorld().spigot().playEffect(location.clone().add(xOffset, 0, zOffset), swirlEffect, 0, 0, 0, 0, 0, 1 / 20, 1, 35);
			location.getWorld().spigot().playEffect(location.clone().add(xOffset, 3, zOffset), swirlEffect, 0, 0, 0, 0, 0, 1 / 20, 1, 35);
			location.getWorld().spigot().playEffect(location.clone().add(-xOffset, 0, -zOffset), swirlEffect, 0, 0, 0, 0, 0, 1 / 20, 1, 35);
			location.getWorld().spigot().playEffect(location.clone().add(-xOffset, 3, -zOffset), swirlEffect, 0, 0, 0, 0, 0, 1 / 20, 1, 35);
		}
	}

	public static void teleport(Player player, LodeStone destination)
	{
		MGPlayer mgp = Main.getSaveManager().getMGPlayer(player);

		player.closeInventory();

		if(mgp.isTeleporting())
		{
			player.sendMessage(ChatColor.RED + "You are already teleporting.");
			return;
		}

		BossBarUtil.createSecondCountdown("Teleporting in:", BarColor.PURPLE, BarStyle.SEGMENTED_10, 8).addPlayer(player);

		mgp.setTeleporting();
		player.sendMessage(ChatColor.LIGHT_PURPLE + "You will be teleported shortly.");

		final Location teleportFrom = player.getLocation();
		final List<Location> circle = LocationUtil.getPointsOnCircle(teleportFrom, 45, 2);

		for(int i = 0; i <= 160; i+=5)
		{
			final int k = i;
			if(i != 160)
			{
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () ->
				{
					//Set back player if they move too far.
					if(teleportFrom.distance(player.getLocation()) >= 1.0)
					{
						player.sendMessage(ChatColor.RED + "You cannot walk out of a teleport!");
						player.teleport(teleportFrom);
					}
					for(Location l : circle)
					{
						l.getWorld().spigot().playEffect(l, Effect.WITCH_MAGIC, 0, 0, 0, k/50F, 0, 1/20, k/10, 25);
					}
					if(k % 20 == 0) EffectUtil.playSound(teleportFrom, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

				}, i);
			}
			else
			{
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () ->
				{
					if(destination.getLocation() != null && destination.getWorld() != null)
					{
						EffectUtil.playParticle(player, Effect.CLOUD, 30);
						player.teleport(destination.getLocation());
						if(Main.getConfigManager().getConfigClass().showTitleOnTeleport())
						{
							TitleUtil.sendTitle(TitleUtil.createTitle(destination.getDisplayName(), destination.getDescription(), 20, 60, 20, true), player);
						}
					}
				}, i);
			}
		}
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isDefaultUnlocked() {
		return defaultUnlocked;
	}

	public void setDefaultUnlocked(boolean defaultUnlocked) {
		this.defaultUnlocked = defaultUnlocked;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getDataValue() {
		return dataValue;
	}

	public void setDataValue(int dataValue) {
		this.dataValue = dataValue;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Material getCostMaterial() {
		return costMaterial;
	}

	public void setCostMaterial(Material costMaterial) {
		this.costMaterial = costMaterial;
	}

	public Effect getSwirlEffect() {
		return swirlEffect;
	}

	public void setSwirlEffect(Effect swirlEffect) {
		this.swirlEffect = swirlEffect;
	}

	public Effect getCenterEffect() {
		return centerEffect;
	}

	public void setCenterEffect(Effect centerEffect) {
		this.centerEffect = centerEffect;
	}

	public boolean showOnMap() {
		return showOnMap;
	}

	public void setShowOnMap(boolean showOnMap) {
		this.showOnMap = showOnMap;
	}

	public void updateDynMap()
	{
		if(Main.isDynmapEnabled() && Main.getConfigManager().getConfigClass().useDynMapMarkers())
		{
			if(showOnMap())
			{
				Main.getDynmapUtil().addMarker(this);
			}
			else
			{
				Main.getDynmapUtil().disableMarker(getName());
			}
		}
	}

	public boolean useMoney() {
		return useMoney;
	}

	public void setUseMoney(boolean useMoney) {
		this.useMoney = useMoney;
	}
}
