package net.minegusta.lodestones.commands;

import net.minegusta.lodestones.Main;
import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.mglib.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LodeStoneCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
	{
		if(!(commandSender instanceof Player)) return false;

		Player player = (Player) commandSender;

		if(!player.hasPermission("lodestones.help") && !player.hasPermission("lodestones.admin"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}

		if(args.length == 0)
		{
			sendHelp(player);
			return true;
		}


		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + "All currently existing lodestones: ");
				for (String s : Storage.getAllNames()) {
					player.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + s);
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("permissions"))
			{
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permissions nodes for Lodestones:");
				player.sendMessage(ChatColor.GREEN + "lodestones.use" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Use lodestones.");
				player.sendMessage(ChatColor.GREEN + "lodestones.help" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Show the help command.");
				player.sendMessage(ChatColor.GREEN + "lodestones.admin" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Use all lodestone commands.");
				player.sendMessage(ChatColor.GRAY + "You can edit a specific lodestones permissions using " + ChatColor.DARK_GREEN + "/LS Edit <Name> Permission <Node>" + ChatColor.GRAY + ".");
				player.sendMessage(ChatColor.GRAY + "Then simply give that newly made permission to people/groups.");
				return true;
			}
		}

		if(player.hasPermission("lodestones.admin") || player.hasPermission("lodestones.*"))
		{
			if (args.length == 2) {

				String name = args[1];

				if (args[0].equalsIgnoreCase("create"))
				{
					if(Storage.exists(name))
					{
						player.sendMessage(ChatColor.RED + "A lodestone with that name already exists.");
					}
					else
					{
						LodeStone stone = Storage.createLodeStone(name);
						stone.setLocation(player.getLocation());
						stone.updateDynMap();
						player.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Main.getConfigManager().getConfigClass().getLodestoneBlock());
						player.sendMessage(ChatColor.GREEN + "You created a lodestone named: " + name);
						player.sendMessage(ChatColor.GREEN + "Edit it using /Lodestones Edit " + name);
					}
					return true;
				}

				LodeStone stone = null;

				if (Storage.exists(name)) {
					stone = Storage.getLodeStone(name);
				}
				if (stone == null) {
					player.sendMessage(ChatColor.RED + "That lodestone does not exist. Use the name listed in /Lodestones List.");
					return true;
				}

				if (args[0].equalsIgnoreCase("remove"))
				{
					Storage.deleteLodeStone(name);
					player.sendMessage(ChatColor.GOLD + "You deleted lodestone: " + name.toLowerCase());
					Main.getDynmapUtil().disableMarker(name.toLowerCase());

					return true;
				}

				if (args[0].equalsIgnoreCase("info"))
				{
					player.sendMessage(ChatColor.LIGHT_PURPLE + "-- Lodestone Info --");
					player.sendMessage(ChatColor.DARK_PURPLE + "Name: " + ChatColor.GRAY + stone.getName());
					player.sendMessage(ChatColor.DARK_PURPLE + "Display Name: " + ChatColor.GRAY + stone.getDisplayName());
					player.sendMessage(ChatColor.DARK_PURPLE + "Location: " + ChatColor.GRAY + LocationUtil.locationToString(stone.getLocation()));
					player.sendMessage(ChatColor.DARK_PURPLE + "Default Unlocked: " + ChatColor.GRAY + stone.isDefaultUnlocked());
					player.sendMessage(ChatColor.DARK_PURPLE + "Display Item: " + ChatColor.GRAY + stone.getMaterial());
					player.sendMessage(ChatColor.DARK_PURPLE + "Display Data Value: " + ChatColor.GRAY + stone.getDataValue());
					player.sendMessage(ChatColor.DARK_PURPLE + "Description: " + ChatColor.GRAY + stone.getDescription());
					player.sendMessage(ChatColor.DARK_PURPLE + "Swirl Effect: " + ChatColor.GRAY + stone.getSwirlEffect().toString());
					player.sendMessage(ChatColor.DARK_PURPLE + "Cost: " + ChatColor.GRAY + stone.getCost() + (stone.useMoney() ? "$" : " " + stone.getCostMaterial()));
					player.sendMessage(ChatColor.DARK_PURPLE + "Center Effect: " + ChatColor.GRAY + stone.getCenterEffect().toString());
					player.sendMessage(ChatColor.DARK_PURPLE + "Show on Dynmap: " + ChatColor.GRAY + stone.showOnMap());
					return true;
				}

				if(args[0].equalsIgnoreCase("teleport"))
				{
					if(stone.getWorld() != null && stone.getLocation() != null) player.teleport(stone.getLocation());
					player.sendMessage(ChatColor.GREEN + "You teleported to a lodestone.");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("Edit")) {
				if (args.length < 3) {
					sendEditHelp(player);
					return true;
				}

				String name = args[1];
				String assignment = args[2];
				LodeStone stone = null;

				if (Storage.exists(name)) {
					stone = Storage.getLodeStone(name);
				}
				if (stone == null) {
					player.sendMessage(ChatColor.RED + "That lodestone does not exist. Use the name listed in /Lodestones List.");
					return true;
				}

				if (args.length == 3) {
					if (assignment.equalsIgnoreCase("Default")) {
						stone.setDefaultUnlocked(!stone.isDefaultUnlocked());
						if (stone.isDefaultUnlocked())
							player.sendMessage(ChatColor.GREEN + "This lodestone is now unlocked by default.");
						else
							player.sendMessage(ChatColor.DARK_RED + "Players now have to unlock this lodestone by walking there first.");
						stone.updateDynMap();
						return true;
					} else if (assignment.equalsIgnoreCase("location")) {
						stone.setLocation(player.getLocation());
						player.sendMessage(ChatColor.GREEN + "Your location has been marked as this lodestones location.");
						stone.updateDynMap();
						return true;
					} else if (assignment.equalsIgnoreCase("showonmap")) {
						stone.setShowOnMap(!stone.showOnMap());
						player.sendMessage(stone.showOnMap() ? ChatColor.GREEN + "This lodestone is now shown in dynmap." : ChatColor.RED + "This lodestone is no longer shown on dynmap.");
						stone.updateDynMap();
						return true;
					}

				}
				if (args.length > 3) {
					String parameter = args[3];
					if (assignment.equalsIgnoreCase("name")) {
						String displayName = "";
						for(int i = 3; i < args.length; i++)
						{
							if(args.length == i + 1) displayName = displayName + args[i];
							else displayName = displayName + args[i] + " ";
						}
						try {
							stone.setDisplayName(displayName);
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid name.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set this stones displayname to " + displayName + ".");
						stone.updateDynMap();
						return true;
					}
					if (assignment.equalsIgnoreCase("description")) {
						String description = "";
						for(int i = 3; i < args.length; i++)
						{
							if(args.length == i + 1) description = description + args[i];
							else description = description + args[i] + " ";
						}
						try {
							stone.setDescription(description);
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid Description.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set this stones Description to " + description + ".");
						stone.updateDynMap();
						return true;
					}
					if (assignment.equalsIgnoreCase("permission")) {
						stone.setPermission(parameter);
						player.sendMessage(ChatColor.GREEN + "You set the lodestones permissions node to: " + parameter);
						return true;
					}
					if (assignment.equalsIgnoreCase("cost")) {
						try {
							stone.setCost(Integer.parseInt(parameter));
						} catch (Exception ignored)
						{
							player.sendMessage(ChatColor.RED + "That is an invalid cost.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "Using this lodestone now costs: " + stone.getCost() + (stone.useMoney() ? "$" : " " + stone.getCostMaterial().toString()) + ".");
						stone.updateDynMap();
						return true;
					}
					if (assignment.equalsIgnoreCase("currency")) {

						//Payment to currency $
						if(parameter.equalsIgnoreCase("$") || parameter.equalsIgnoreCase("money"))
						{
							stone.setUseMoney(true);
						}
						//Payment to item ID
						else
						{
							try {
								stone.setCostMaterial(Material.getMaterial(Integer.parseInt(parameter)));
								stone.setUseMoney(false);
							} catch (Exception ignored)
							{
								player.sendMessage(ChatColor.RED + "That is an invalid Material ID.");
								return true;
							}
						}

						player.sendMessage(ChatColor.GREEN + "Using this lodestone now costs: " + stone.getCost() + (stone.useMoney() ? "$" : " " + stone.getCostMaterial().toString()) + ".");						stone.updateDynMap();
						return true;


					}
					if (assignment.equalsIgnoreCase("material")) {
						Material material;
						try {
							material = Material.getMaterial(Integer.parseInt(parameter));
							stone.setMaterial(material);
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid material ID.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set the lodestones material to " + parameter + ".");
						return true;
					}
					if (assignment.equalsIgnoreCase("datavalue")) {
						try {
							stone.setDataValue(Integer.parseInt(parameter));
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid data value.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set the lodestones data value to " + parameter + ".");
						return true;
					}
					if (assignment.equalsIgnoreCase("swirleffect")) {
						try {
							stone.setSwirlEffect(Effect.valueOf(parameter.toUpperCase()));
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid effect.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set the lodestones swirl particle to " + parameter + ".");
						return true;
					}
					if (assignment.equalsIgnoreCase("centereffect")) {
						try {
							stone.setCenterEffect(Effect.valueOf(parameter.toUpperCase()));
						} catch (Exception ignored) {
							player.sendMessage(ChatColor.RED + "That is not a valid effect.");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "You set the lodestones center particle to " + parameter + ".");
						return true;
					}
				}
				sendEditHelp(player);
				return true;
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}

		sendHelp(player);
		return true;
	}

	private static void sendHelp(Player player)
	{
		player.sendMessage(ChatColor.LIGHT_PURPLE + "--- This is the Lodestones help menu. ---");
		player.sendMessage(ChatColor.AQUA + "/Lodestones" + ChatColor.GRAY + " - Show this menu");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Create <Name>" + ChatColor.GRAY + " - Create a new lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Remove <Name>" + ChatColor.GRAY + " - Remove a lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones List" + ChatColor.GRAY + " - List all existing lodestones.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Permissions" + ChatColor.GRAY + " - List the permissions nodes for lodestones.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name>" + ChatColor.GRAY + " - Edit a lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Teleport <Name>" + ChatColor.GRAY + " - Teleport to a lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Info <Name>" + ChatColor.GRAY + " - Show info about a lodestone.");
	}

	private static void sendEditHelp(Player player)
	{
		player.sendMessage(ChatColor.LIGHT_PURPLE + "--- This is the Lodestones help menu. ---");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Name <New Name>" + ChatColor.GRAY + " - Change the display name of a lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Location" + ChatColor.GRAY + " - Changes this lodestones destination to you.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Default" + ChatColor.GRAY + " - Toggle whether this lodestone is unlocked by default.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> ShowOnMap" + ChatColor.GRAY + " - Show this lodestone on dynmap.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Permission <Permission>" + ChatColor.GRAY + " - Give this lodestone a special permissions string.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Material <ID>" + ChatColor.GRAY + " - Set the interface item ID for this lodestone.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> DataValue <ID>" + ChatColor.GRAY + " - Give the item displayed a data value.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Cost <Amount>" + ChatColor.GRAY + " - Set the cost for using.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> Currency <ID OR $>" + ChatColor.GRAY + " - Set this lodestones usage cost to this item.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> SwirlEffect <Effect>" + ChatColor.GRAY + " - Change the swirl effect to another particle.");
		player.sendMessage(ChatColor.AQUA + "/Lodestones Edit <Name> CenterEffect <Effect>" + ChatColor.GRAY + " - Change the center effect to another particle.");
		player.sendMessage(ChatColor.GRAY + "All usable effects:" + ChatColor.DARK_BLUE + " https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Effect.html");

	}

}
