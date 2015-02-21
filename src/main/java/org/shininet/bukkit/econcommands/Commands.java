package org.shininet.bukkit.econcommands;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.NestedCommand;

public class Commands {

	public static class Parent {
		@Command(aliases = {"econcommands", "money"}, desc = "Economy commands")
		@NestedCommand({Commands.class})
		public static void money() {
		}
	}

	private static void requirePermission(CommandSender sender, String permission) throws CommandPermissionsException {
		if (!(EconCommands.inst().commands.hasPermission(sender, permission))) {
			throw new CommandPermissionsException();
		}
	}

	private static Economy econ() {
		return EconCommands.econ;
	}

	@SuppressWarnings("deprecation")
	private static OfflinePlayer offlinePlayer(String name) {
		return Bukkit.getOfflinePlayer(name);
	}

	@Command(aliases = {"balance"}, usage = "[player]", desc = "Check a player balance", min = 0, max = 1)
	public static void balance(CommandContext args, CommandSender sender) throws CommandException {
		double balance;
		if (args.argsLength() == 0) {
			requirePermission(sender, "econcommands.balance.self");
			if (!(sender instanceof Player)) {
				throw new CommandException("Command must either be issued by a player or have player name in arguments");
			}
			Player player = (Player) sender;
			balance = econ().getBalance(player);
		} else {
			requirePermission(sender, "econcommands.balance.other");
			OfflinePlayer playerOther = offlinePlayer(args.getString(0));
			if (!(econ().hasAccount(playerOther))) {
				sender.sendMessage("That player does not have an account");
				return;
			}
			balance = econ().getBalance(playerOther);
		}
		sender.sendMessage("Balance: " + econ().format(balance) + " " + ((balance == 1)?econ().currencyNameSingular():econ().currencyNamePlural()));
	}
}
