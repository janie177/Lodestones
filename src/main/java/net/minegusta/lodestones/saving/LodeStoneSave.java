package net.minegusta.lodestones.saving;

import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.mglib.configs.ConfigurationModel;
import net.minegusta.mglib.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class LodeStoneSave extends ConfigurationModel {

	@Override
	public void onLoad(FileConfiguration conf) {
		for(String s : getConfig().getKeys(false))
		{
			LodeStone stone = Storage.createLodeStone(s);

			stone.setDefaultUnlocked(conf.getBoolean(s + ".default", false));
			stone.setPermission(conf.getString(s + ".permission-string", "lodestones.use"));
			Location location = LocationUtil.stringToLocation(conf.getString(s + ".location", LocationUtil.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) ;
			if(location != null)
			{
				stone.setLocation(location);
			}
			stone.setDescription(conf.getString(s + ".description", "Description."));

			Material material = Material.DIAMOND;
			Material costMaterial = Material.GOLD_NUGGET;
			Effect swirlEffect = Effect.WITCH_MAGIC;
			Effect centerEffect = Effect.SPELL;
			try {
				swirlEffect = Effect.valueOf(conf.getString(s + ".swirleffect", null));
			} catch (Exception ignored){}
			try {
				centerEffect = Effect.valueOf(conf.getString(s + ".centereffect", null));
			} catch (Exception ignored){}
			try
			{
				material = Material.valueOf(conf.getString(s + ".material", null));
			} catch (Exception ignored){}

			try
			{
				costMaterial = Material.valueOf(conf.getString(s + ".costmaterial", null));
			} catch (Exception ignored){}

			stone.setCostMaterial(costMaterial);
			stone.setCost(conf.getInt(s + ".cost", 0));

			stone.setCenterEffect(centerEffect);
			stone.setSwirlEffect(swirlEffect);
			stone.setMaterial(material);
			stone.setDataValue(conf.getInt(s + ".datavalue", 0));
			stone.setDisplayName(conf.getString(s + ".displayname", s));

			stone.updateDynMap();
		}
	}

	@Override
	public void onSave(FileConfiguration conf) {
		//Remove all old lodestones that have been removed from memory
		for(String s : getConfig().getKeys(false))
		{
			if(!Storage.getAllNames().contains(s))
			{
				conf.set(s, null);
			}
		}

		//Save all lodestones that are in the map.
		for(LodeStone stone : Storage.getAll())
		{
			String name = stone.getName();
			conf.set(name + ".default", stone.isDefaultUnlocked());
			conf.set(name + ".permission-string", stone.getPermission());
			conf.set(name + ".location", LocationUtil.locationToString(stone.getLocation()));
			conf.set(name + ".description", stone.getDescription());
			conf.set(name + ".material", stone.getMaterial().toString());
			conf.set(name + ".costmaterial", stone.getCostMaterial().toString());
			conf.set(name + ".cost", stone.getCost());
			conf.set(name + ".datavalue", stone.getDataValue());
			conf.set(name + ".displayname", stone.getDisplayName());
			conf.set(name + ".swirleffect", stone.getSwirlEffect().toString());
			conf.set(name + ".centereffect", stone.getCenterEffect().toString());

		}
	}
}
