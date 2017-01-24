package net.minegusta.lodestones.listeners;

import net.minegusta.lodestones.Main;
import net.minegusta.lodestones.lodestones.LodeStone;
import net.minegusta.lodestones.lodestones.Storage;
import net.minegusta.lodestones.saving.MGPlayer;
import net.minegusta.mglib.utils.EffectUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LodeStoneListener implements Listener
{
	@EventHandler
	public void onLodestoneContact(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) && !p.isSneaking())
		{
			if(e.getClickedBlock().getType() == Main.getConfigManager().getConfigClass().getLodestoneBlock())
			{
				for(LodeStone stone : Storage.getAll())
				{
					World world;
					if((world = stone.getWorld()) != null && world.equals(p.getWorld()) && stone.getLocation().distance(e.getClickedBlock().getLocation()) < 2)
					{
						e.setCancelled(true);

						MGPlayer mgp = Main.getSaveManager().getMGPlayer(p);

						if(!p.hasPermission(stone.getPermission()))
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to use this lodestone.");
							return;
						}

						if(!mgp.hasUnlocked(stone.getName()))
						{
							mgp.unlock(stone.getName());
							p.sendMessage(ChatColor.GREEN + "You have now unlocked this lodestone!");
							EffectUtil.playSound(p, Sound.BLOCK_NOTE_PLING);
							return;
						}
						//Teleport player onto the lodestone.
						p.teleport(stone.getLocation());
						Main.getTeleportGUI().openInventory(p);
						break;
					}
				}
			}
		}
	}
}
