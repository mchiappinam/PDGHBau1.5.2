package pdghbau.metodos;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.kewi.PDGHBau;
import net.milkbowl.vault.economy.Economy;
import pdghbau.comandos.KwComando;
import pdghbau.eventos.KwEventos;

public class KwMetodos {
	public HashMap<String,Integer> usando = new HashMap<String,Integer>();
	//public HashMap<String,Integer> staff = new HashMap<String,Integer>();

	public Economy econ=null;
	private PDGHBau pl;
	private Server s;
	private ConsoleCommandSender ccs;
	private PluginManager pm;
	private String prefix = "§b[PDGHBau] §3";
	private FileConfiguration fc;
	private KwUtils util;
	private KwMySQL sql;

	public KwMetodos(PDGHBau pl) {
		this.pl = pl;
		s = pl.getServer();
		ccs = s.getConsoleSender();
		pm = s.getPluginManager();
		fc = pl.getConfig();
		util = new KwUtils(this);
		if(!setupEconomy()) {
			getConsole("ERRO: Vault (Economia) nao encontrado!");
			getConsole("Desativando o plugin...");
			pl.getServer().getPluginManager().disablePlugin(pl);
        }
	}
	
	private boolean setupEconomy() {
        if (pl.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp=pl.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ=rsp.getProvider();
        return econ != null;
	}

	public void getConsole(String ce) {
		ce = prefix + ce;
		ccs.sendMessage(ce);
	}

	public FileConfiguration getConfig() {
		return fc;
	}

	public String getCnf(String s) {
		return fc.getString(s);
	}

	public String replaced(String rd) {
		return rd.replaceAll("&", "§");
	}

	public String getReplaced(String s) {
		return replaced(getCnf(s));
	}

	private void saveMetodos() {
		int quantidade=fc.getInt("Bau.Quantidade");
		if((quantidade>=1)&&(quantidade<=1000)) {
			for(int x = 1; x <=quantidade; x++) {
				getConsole("Verificando bau "+x+"...");
				if (fc.getBoolean("MySQL.Ativar")) {
					sql = KwMySQL.load(getCnf("MySQL.Host"), getCnf("MySQL.Database"), getCnf("MySQL.Usuario"),
							getCnf("MySQL.Senha"));
					sql.update("CREATE TABLE IF NOT EXISTS pdghbau"+x+" (jogador varchar(255) not null, bau TEXT not null);");
				} else {
					sql = KwMySQL.load(new File(pl.getDataFolder(), "pdghbau.db"));
					sql.update("CREATE TABLE IF NOT EXISTS pdghbau"+x+" (jogador varchar(255) not null, bau TEXT not null);");
				}
			}
		}else{
			getConsole("Quantidade de baus nao pode ser superior que 1000 ou inferior que 1");
			pl.getServer().getPluginManager().disablePlugin(pl);
			return;
		}
			
		pm.registerEvents(new KwEventos(this), pl);
		s.getPluginCommand("bau").setExecutor(new KwComando(this));
	}

	public void getMetodos() {
		getConsole("Registrando Eventos!");
		saveMetodos();
		getConsole("Eventos Registrados!");
		getConsole("Plugin Habilitado! Autor: Kewilleen_ & mchiappinam");
	}

	public PDGHBau getPlugin() {
		return pl;
	}

	public KwUtils getUtils() {
		return util;
	}

	public String getBau(String jogador, int Bau) {
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM pdghbau"+Bau+" WHERE jogador=?;");
			ps.setString(1, jogador);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getString("bau");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean checkBau(String jogador, int Bau) {
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM pdghbau"+Bau+" WHERE jogador=?;");
			ps.setString(1, jogador);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void criarBau(String jogador, int Bau, String itens) {
		try {
			PreparedStatement ps = sql.getConnection()
					.prepareStatement("INSERT INTO pdghbau"+Bau+" (jogador, bau) VALUES (?,?);");
			ps.setString(1, jogador);
			ps.setString(2, itens);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void atualizarBau(String jogador, int Bau, String itens) {
		try {
			PreparedStatement ps = sql.getConnection().prepareStatement("UPDATE pdghbau"+Bau+" SET bau=? WHERE jogador=?;");
			ps.setString(1, itens);
			ps.setString(2, jogador);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){
		sql.close();
	}

	public void help(Player p) {
		double taxa = getConfig().getDouble("Bau.Taxa");
		int quantidade = getConfig().getInt("Bau.Quantidade");
		p.sendMessage("§3§lPDGH Baú - Comandos:");
		if(quantidade==1)
			p.sendMessage("§2/bau -§a- Acessa seu baú virtual.");
		else
			p.sendMessage("§2/bau [1-"+quantidade+"] -§a- Acessa seu baú virtual.");
		p.sendMessage("§2/bau buy -§a- Compra um baú virtual.");
		p.sendMessage("§cTaxa unitária para compra do baú: §a§l$"+taxa+"§c.");
		if(p.hasPermission("pdgh.op")||p.hasPermission("pdgh.bauopenall")) {
			p.sendMessage("§c/bau <nick> [1-"+quantidade+"] -§2- Abre o baú de outros jogadores.");
		}
	}
}
