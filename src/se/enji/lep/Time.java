package se.enji.lep;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Time extends JavaPlugin {
	FileConfiguration config;
	String notAllowed, wrongWay;
	List<?> supportedArgs = Arrays.asList(new String[]{"day","night"});

	@Override
	public void onEnable() {
		config=getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		notAllowed = config.getString("messages.error.notAllowed");
		wrongWay = config.getString("messages.error.wrongWay");
	}
	
	private void timeSet(World w, String s, CommandSender cs) {
		String name = cs.getName();
		String world = w.getName();
		world = world.replaceAll("world_", "").replaceAll("_", " ");
		world = world.substring(0,1).toUpperCase() + world.substring(1,world.length());
		name = name.equals("CONSOLE") ? config.getString("alias.console") : name;
		if (!allow(cs,s)) {
			((Player)cs).sendMessage(notAllowed);
			return;
		}
		if (s.equals("day")) w.setTime(0L);
		else w.setTime(19500L);
		getServer().broadcastMessage(config.getString("messages."+s+".text").replaceAll("%p", name).replaceFirst("%w", world));
	}
	
	private boolean allow(CommandSender s, String a) {
		if (s instanceof Player) {
			if (((Player)s).hasPermission("lep.time.*") || ((Player)s).hasPermission("lep.time." + a)) return true;
			else return false;
		}
		return true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("time")) {
			if ((args.length <= 0  || args.length > 2) || !supportedArgs.contains(args[0])) {
				sender.sendMessage(wrongWay);
				return false;
			}
			int targetWorld = args.length == 2 && args[1] != null ? Integer.parseInt(args[1]) : 0;
			World w = sender instanceof Player ? ((Player)sender).getWorld() : getServer().getWorlds().get(targetWorld);
			timeSet(w,args[0],sender);
			return true;
		}
		return false; 
	}
}