package net.minegusta.lodestones.dynmap;

import net.minegusta.lodestones.Main;
import net.minegusta.lodestones.lodestones.LodeStone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.InputStream;

public class DynMapUtil
{
	private DynmapAPI dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
	private String setName = "Lodestones";
	private String markerPrefix = "lodestone_";
	private String markerIconName = "lodestones_marker_icon";
	private MarkerIcon icon;
	private MarkerSet set;

	public DynMapUtil()
	{
		MarkerAPI api = dynmap.getMarkerAPI();
		icon = api.getMarkerIcon(markerIconName);
		if(icon != null)
		{
			icon.deleteIcon();
		}
		try {
			InputStream in =  Main.getPlugin().getResource("markers/lodestone-icon.png");
			icon = api.createMarkerIcon(markerIconName, markerIconName, in);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		set = api.getMarkerSet(setName);
		if(set != null)
		{
			set.deleteMarkerSet();
		}
		set = api.createMarkerSet(setName, setName, null, false);
	}

	//Methods
	public void addMarker(LodeStone stone)
	{
		disableMarker(stone.getName());

		String name = stone.getName();
		name = markerPrefix + name;

		Marker marker = set.findMarker(name);
		if(marker == null)
		{
			marker = set.createMarker(name, ChatColor.stripColor(stone.getDisplayName()), stone.getWorld().getName(), stone.getLocation().getX(), stone.getLocation().getY(), stone.getLocation().getZ(), icon, false);
		}
		marker.setDescription(formatMenu(stone));
	}

	private String formatMenu(LodeStone stone)
	{

		String returned = "<div class=\"infowindow\"><span style=\"font-size:135%; font-weight:bold; color:purple;\">%name%</span><br /><span style=\"font-weight:bold;\">Description: </span>%description%<br /><span style=\"font-weight:bold;\">Price: </span>%price%<br /><span style=\"font-weight:bold;\">Default Unlocked: </span>%default%<br /></div>";
		returned = returned.replace("%name%", ChatColor.stripColor(stone.getDisplayName()));
		returned = returned.replace("%description%", ChatColor.stripColor(stone.getDescription()));
		returned = returned.replace("%price%", stone.getCost() > 0 ? (stone.getCost() + (stone.useMoney() ? "$" : " " + stone.getCostMaterial().toString())) : "Free!");
		returned = returned.replace("%default%", Boolean.toString(stone.isDefaultUnlocked()));
		return returned;
	}

	public void disableMarker(String name)
	{

		name = markerPrefix + name;
		Marker marker = set.findMarker(name);
		if(marker != null)
		{
			marker.deleteMarker();
		}
	}
}
