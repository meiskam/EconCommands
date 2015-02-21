package org.shininet.bukkit.econcommands;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

public class CommandsMoneyAdmin {

	private static Economy econ() {
		return EconCommands.econ;
	}
	
	@Command(aliases = {"add"}, usage = "<player> <amount>", desc = "Add currency from player's account", min = 2, max = 2)
	public static void add(CommandContext args, CommandSender sender) throws CommandException {
		EconCommands.requirePermission(sender, "econcommands.admin.add");
		OfflinePlayer playerOther = EconCommands.offlinePlayer(args.getString(0));
		if (!(econ().hasAccount(playerOther))) {
			sender.sendMessage("That player does not have an account");
			return;
		}
		Double amount = args.getDouble(1);
		if (amount <= 0) {
			sender.sendMessage("Amount must be greater than 0");
			return;
		}
		if (econ().depositPlayer(playerOther, amount).transactionSuccess()) {
			sender.sendMessage("Added "+EconCommands.format(amount)+" to "+playerOther.getName()+"'s account, new balance: "+econ().getBalance(playerOther));
		} else {
			sender.sendMessage("Error, could not add to "+playerOther.getName()+"'s account");
		}
	}
}
