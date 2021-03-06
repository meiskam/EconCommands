package org.shininet.bukkit.econcommands;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;

public class CommandsMoney {

	public static class Parent {
		@Command(aliases = {"econcommands", "money"}, desc = "Economy commands")
		@NestedCommand({CommandsMoney.class})
		public static void money() {
		}
	}

	private static Economy econ() {
		return EconCommands.econ;
	}

	@Command(aliases = {"balance", "bal"}, usage = "[player]", desc = "Check a player balance", min = 0, max = 1)
	public static void balance(CommandContext args, CommandSender sender) throws CommandException {
		double balance;
		if (args.argsLength() == 0) {
			EconCommands.requirePermission(sender, "econcommands.balance.self");
			if (!(sender instanceof Player)) {
				throw new CommandException("Command must either be issued by a player or have player name in arguments");
			}
			Player player = (Player) sender;
			balance = econ().getBalance(player);
		} else {
			EconCommands.requirePermission(sender, "econcommands.balance.other");
			OfflinePlayer playerOther = EconCommands.offlinePlayer(args.getString(0));
			if (!(econ().hasAccount(playerOther))) {
				sender.sendMessage("That player does not have an account");
				return;
			}
			balance = econ().getBalance(playerOther);
		}
		sender.sendMessage("Balance: " + EconCommands.format(balance));
	}

	@Command(aliases = {"pay", "give"}, usage = "<player> <amount>", desc = "Transfer currency to another player", min = 2, max = 2)
	public static void pay(CommandContext args, CommandSender sender) throws CommandException {
		EconCommands.requirePermission(sender, "econcommands.pay");
		if (!(sender instanceof Player)) {
			throw new CommandException("Command must be issued by a player");
		}
		Player player = (Player) sender;
		OfflinePlayer playerOther = EconCommands.offlinePlayer(args.getString(0));
		Double amount = args.getDouble(1);
		if (player.getName().equals(playerOther.getName())) {
			sender.sendMessage("Poof, you sent yourself "+EconCommands.format(amount));
			return;
		}
		if (amount <= 0) {
			sender.sendMessage("Amount must be greater than 0");
			return;
		}
		if (!(econ().has(player, amount))) {
			sender.sendMessage("You don't have that much");
			return;
		}
		if (!(econ().hasAccount(playerOther))) {
			sender.sendMessage("That player does not have an account");
			return;
		}
		econ().withdrawPlayer(player, amount);
		econ().depositPlayer(playerOther, amount);
		sender.sendMessage(EconCommands.format(amount)+" sent to "+playerOther.getName());
	}

	@Command(aliases = {"admin"}, desc = "Admin commands")
	@NestedCommand({CommandsMoneyAdmin.class})
	public static void money() {
	}
}
