package net.minegusta.lodestones.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {

	private Economy economy;

	public VaultUtil(Plugin plugin)
	{
		RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		economy = economyProvider.getProvider();
	}

	public boolean playerHasMoney(Player p, int amount)
	{
		if(economy != null)
		{
			return economy.has(p, amount);
		}
		return true;
	}

	public boolean removeMoney(Player p, int amount)
	{
		if(economy != null)
		{
			economy.withdrawPlayer(p, amount);
			return true;
		}
		return false;
	}
}
