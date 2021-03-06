package net.minegusta.lodestones.gui;

import com.google.common.collect.Lists;
import net.minegusta.lodestones.Main;
import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.lodestones.saving.MGPlayer;
import net.minegusta.mglib.gui.InventoryGUI;
import net.minegusta.mglib.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportGUI extends InventoryGUI {
	/**
	 * Create a new insance of a GUI. Extend this class and then make an instance of it to create a new GUI. Use this GUI object to perform actions, such as opening it.
	 *
	 * @param name The name of the GUI.
	 * @param rows The amount of rows in the GUI. Every row has 9 slots.
	 * @param key  The key for this GUI. Has to be unique.
	 */
	public TeleportGUI(String name, int rows, String key)
	{
		super(name, rows, key);
	}

	private static int nextButton = 16;
	private static int backButton = 15;
	private static int pageNumber = 17;

	private static void addButtons(Inventory inv, int page)
	{
		if(page > 1)
		{
			inv.setItem(backButton, new ItemStack(Material.SPECTRAL_ARROW, 1)
			{
				{
					ItemMeta meta = getItemMeta();
					meta.setDisplayName(ChatColor.DARK_GRAY + "Previous Page");
					meta.setLore(Lists.newArrayList(ChatColor.GRAY + "View the previous page."));
					setItemMeta(meta);
				}
			});
		}

		inv.setItem(pageNumber, new ItemStack(Material.BOOK, page < 65 ? page : 1)
		{
			{
				ItemMeta meta = getItemMeta();
				meta.setDisplayName(ChatColor.DARK_GRAY + "Current Page:");
				meta.setLore(Lists.newArrayList(ChatColor.GRAY + "" + page));
				setItemMeta(meta);
			}
		});

		inv .setItem(nextButton, new ItemStack(Material.ARROW, 1)
		{
			{
				{
					ItemMeta meta = getItemMeta();
					meta.setDisplayName(ChatColor.DARK_GRAY + "Next Page");
					meta.setLore(Lists.newArrayList(ChatColor.GRAY + "View the previous page."));
					setItemMeta(meta);
				}
			}
		});
	}

	private static void buildTeleportMenu(MGPlayer mgp, Inventory inventory)
	{
		//Clear the inventory.
		inventory.clear();

		//Add buttons to the inventory.
		addButtons(inventory, mgp.getPage());

		int page = mgp.getPage();

		//Add all the lodestones.
		for(int i = (page * 9) - 9; (i < page * 9) && i < mgp.unlockedSize(); i++)
		{
			String name = mgp.getNameByIndex(i);
			LodeStone stone = Storage.getLodeStone(name);
			if(stone != null)
			{
				int slot = i - ((page - 1) * 9);
				inventory.setItem(slot, new ItemStack(stone.getMaterial(), 1, (short) stone.getDataValue())
				{
					{
						ItemMeta meta = getItemMeta();
						meta.setDisplayName(stone.getDisplayName());
						meta.setLore(Lists.newArrayList(stone.getDescription(), ChatColor.YELLOW + "Cost: " + (stone.getCost() > 0 ? ChatColor.DARK_GREEN + " " + stone.getCost() +  (stone.useMoney() ? "$" : " " + stone.getCostMaterial().toString()) : ChatColor.GREEN + "Free!"), ChatColor.GRAY + "World: " + ChatColor.DARK_GRAY + stone.getWorld().getName(), ChatColor.GRAY + "X: " + ChatColor.DARK_GRAY + (int) stone.getLocation().getX(), ChatColor.GRAY + "Z: " + ChatColor.DARK_GRAY + (int) stone.getLocation().getZ()));
						setItemMeta(meta);
					}
				});
			}
		}
	}

	@Override
	public Inventory buildInventory(Player player, int slots, InventoryHolder holder, String name)
	{
		Inventory inventory = Bukkit.createInventory(holder, slots, name);

		MGPlayer mgp = Main.getSaveManager().getMGPlayer(player);
		mgp.setPage(1);

		buildTeleportMenu(mgp, inventory);

		return inventory;
	}

	@Override
	public void processClick(Player player, int slot, InventoryClickEvent e)
	{
		MGPlayer mgp = Main.getSaveManager().getMGPlayer(player);
		int page = mgp.getPage();
		if(slot < 9)
		{
			if(page * slot < mgp.unlockedSize())
			{
				String name = mgp.getNameByIndex(((page - 1) * 9) + slot);
				LodeStone stone = Storage.getLodeStone(name);

				if(stone == null)
				{
					player.sendMessage(ChatColor.RED + "This lodestone could not be found. Report this to an admin.");
					return;
				}

				if(player.hasPermission(stone.getPermission()))
				{
					int cost = stone.getCost();
					Material material = stone.getCostMaterial();
					boolean money = stone.useMoney();

					if(cost > 0)
					{
						//Money
						if(money)
						{
							if(Main.isVaultEnabled())
							{
								if(Main.getVaultUtil().playerHasMoney(player, cost))
								{
									LodeStone.teleport(player, stone);
									if(!Main.getVaultUtil().removeMoney(player, cost))
									{
										player.sendMessage(ChatColor.GREEN + "An economy plugin could not be found. Teleporting for free.");
									}
									else
									{
										player.sendMessage(ChatColor.GREEN + "You paid " + cost + "$" + " to teleport.");
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You cannot afford to teleport there.");
								}
							}
							else
							{
								player.sendMessage(ChatColor.GREEN + "Vault plugin could not be found. Teleporting for free.");
								LodeStone.teleport(player, stone);
							}
						}
						//Items
						else
						{
							//Pay the items and teleport
							if (player.getInventory().contains(material, cost))
							{
								LodeStone.teleport(player, stone);
								PlayerUtil.removeAmount(player, material, cost);
								player.sendMessage(ChatColor.GREEN + "You paid " + cost + " " + material + " to teleport.");
							}
							//Player could not afford the items
							else
							{
								player.sendMessage(ChatColor.RED + "You cannot afford to teleport there.");
							}
						}

					}
					//Cost is free
					else
					{
						LodeStone.teleport(player, stone);
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You do not have permission to use this lodestone.");
				}
			}
		}
		else if(slot == backButton && page > 1)
		{
			mgp.setPage(mgp.getPage() - 1);
			buildTeleportMenu(mgp, e.getClickedInventory());
		}
		else if(slot == nextButton)
		{
			mgp.setPage(mgp.getPage() + 1);
			buildTeleportMenu(mgp, e.getClickedInventory());
		}
	}

	@Override
	public void animate(Inventory inv) {

	}

	@Override
	public int getAnimationInterval() {
		return 0;
	}
}
