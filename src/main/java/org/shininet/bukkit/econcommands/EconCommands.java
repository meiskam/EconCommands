package org.shininet.bukkit.econcommands;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;

public class EconCommands extends JavaPlugin {

	private static EconCommands instance;

	CommandsManager<CommandSender> commands;
	static Economy econ;

	public EconCommands() {
		super();
		if (instance == null) {
			instance = this;
		}
	}

	static EconCommands inst() {
		return instance;
	}

	public void onEnable() {
		if (!setupEconomy()) {
			die("Disabled due to no Vault dependency found!");
			return;
		}
		try {
			Class.forName("com.sk89q.minecraft.util.commands.CommandsManager");
		} catch (ClassNotFoundException e) {
			die("Disabled due to no WorldEdit dependency found!");
			return;
		}

		// Register commands with sk89q's manager
		commands = new CommandsManager<CommandSender>() {
			@Override
			public boolean hasPermission(CommandSender player, String perm) {
				return player.hasPermission(perm);
			}
		};

		final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);
		cmdRegister.register(CommandsMoney.Parent.class);
	}

	private void die(String message) {
		getLogger().severe(message);
		getServer().getPluginManager().disablePlugin(this);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			commands.execute(cmd.getName(), args, sender, sender);
		} catch (CommandPermissionsException e) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
		} catch (MissingNestedCommandException e) {
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (CommandUsageException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch (WrappedCommandException e) {
			if (e.getCause() instanceof NumberFormatException) {
				sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
			} else {
				sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
				e.printStackTrace();
			}
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}

	static void requirePermission(CommandSender sender, String permission) throws CommandPermissionsException {
		if (!(EconCommands.inst().commands.hasPermission(sender, permission))) {
			throw new CommandPermissionsException();
		}
	}

	@SuppressWarnings("deprecation")
	static OfflinePlayer offlinePlayer(String name) {
		return Bukkit.getOfflinePlayer(name);
	}
}
