package me.kewi;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pdghbau.metodos.KwMetodos;

public class PDGHBau extends JavaPlugin {

	private KwMetodos m;

	@Override
	public void onEnable() {
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}
			catch(Exception e) {}
		}
		m = new KwMetodos(this);
		m.getMetodos();
	}

	@Override
	public void onDisable() {
		for(Player p : getServer().getOnlinePlayers()){
			p.closeInventory();
		}
		m.closeConnection();
	}

	public KwMetodos getMetodos() {
		return m;
	}

}
