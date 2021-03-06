package org.ctp.coldstorage.utils.alias;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

final class AliasCommand extends Command {
	private static Field commandMap;
	private static Field knownCommands;
	private final Alias alias;
	private boolean registered;

	static {
		try {
			commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommands.setAccessible(true);
		} catch (Exception e) {
			/* do nothing */
		}
	}

	public AliasCommand(Alias alias) {
		super(alias.getName(), alias.getDescription(), "", new ArrayList<String>());
		this.alias = alias;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		alias.execute(sender, args);
		return true;
	}

	@Override
	public boolean testPermissionSilent(CommandSender target) {
		if(target instanceof Player) {
			return ((Player) target).hasPermission(alias.getPermission());
		}
		return true;
	}

	public boolean register() throws IllegalStateException {
		if (registered)
			throw new IllegalStateException("Command is already registered");
		try {
			return registered = ((SimpleCommandMap) commandMap.get(Bukkit.getServer())).register(alias.getPrefix(), this);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean unregister() throws IllegalStateException {
		if (!registered)
			throw new IllegalStateException("Command is not registered");
		try {
			((Map<String, Command>) knownCommands.get((SimpleCommandMap) commandMap.get(Bukkit.getServer()))).remove(getName());
			registered = false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Alias getAlias() {
		return this.alias;
	}

	public boolean isRegistered() {
		return this.registered;
	}
}