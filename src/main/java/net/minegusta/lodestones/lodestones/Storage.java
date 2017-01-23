package net.minegusta.lodestones.lodestones;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class Storage
{
	private static ConcurrentMap<String, LodeStone> lodestones = Maps.newConcurrentMap();

	public static LodeStone createLodeStone(String name)
	{
		LodeStone stone = LodeStone.createLodeStone(name);

		lodestones.put(name.toLowerCase(), stone);

		return stone;
	}

	public static LodeStone getLodeStone(String name)
	{
		if(exists(name))
		{
			return lodestones.get(name.toLowerCase());
		}
		return null;
	}

	public static boolean exists(String name)
	{
		return lodestones.containsKey(name.toLowerCase());
	}

	public static void deleteLodeStone(String name)
	{
		if(exists(name.toLowerCase()))
		{
			lodestones.remove(name.toLowerCase());
		}
	}

	public static Collection<LodeStone> getAll()
	{
		return lodestones.values();
	}

	public static Set<String> getAllNames()
	{
		return lodestones.keySet();
	}

}
