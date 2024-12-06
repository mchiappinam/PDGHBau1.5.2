package pdghbau.comandos;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pdghbau.metodos.KwMetodos;

public class KwComando implements CommandExecutor {

	private KwMetodos m;

	public KwComando(KwMetodos m) {
		this.m = m;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bau")) {
			if (!(sender instanceof Player)) {
				m.getConsole("Utilize este comando apenas in-game!");
				return true;
			}
			Player p = (Player)sender;
			if((args.length==0)&&(m.checkBau(p.getName().toLowerCase(), 1))) {
				if(m.usando.keySet().contains(p.getName().toLowerCase())) {
					sender.sendMessage("§cSeu bau já está aberto!");
					return true;
				}
				/**if(m.staff.keySet().contains(p.getName().toLowerCase())) {
					p.sendMessage("§cErro. Tente novamente mais tarde!");
					return true;
				}*/
				m.usando.put(p.getName().toLowerCase(), 1);
				p.openInventory(m.getUtils().getBau(p.getName(), 1));
				return true;
			}else if((args.length==0)&&(!m.checkBau(p.getName().toLowerCase(), 1))){
				m.help(p);
				return true;
			}
			try {
				if((StringUtils.isNumeric(args[0]))&&(args.length==1)) {
					int valor=Integer.parseInt(args[0]);
					if((valor>=1)&&(valor<=m.getConfig().getInt("Bau.Quantidade"))) {
						if(!m.checkBau(p.getName().toLowerCase(), valor)) {
							m.help(p);
							return true;
						}
						if(m.usando.keySet().contains(p.getName().toLowerCase())) {
							sender.sendMessage("§cSeu bau já está aberto!");
							return true;
						}
						/**if(m.staff.keySet().contains(p.getName().toLowerCase())) {
							p.sendMessage("§cErro. Tente novamente mais tarde!");
							return true;
						}*/
						m.usando.put(p.getName().toLowerCase(), valor);
						p.openInventory(m.getUtils().getBau(p.getName(), valor));
						return true;
					}
					m.help(p);
					return true;
				}
			}catch(Exception e){
				m.help(p);
				return true;
			}
			if(args[0].equalsIgnoreCase("buy")) {
				if(args.length>1) {
					m.help(p);
					return true;
				}
				if((m.getConfig().getBoolean("Bau.Perms"))&&(!p.hasPermission(m.getConfig().getString("Bau.Permissao")))) {
					p.sendMessage(m.getConfig().getString("Bau.Sem_Permissao"));
					return true;
				}
				int quantidade=m.getConfig().getInt("Bau.Quantidade");
				for(int x = 1; x <=quantidade; x++) {
					while(!m.checkBau(p.getName().toLowerCase(), x)) {
						double taxa = m.getConfig().getDouble("Bau.Taxa");
				        if(!(m.econ.getBalance(p.getName()) >= taxa)) {
						    p.sendMessage("§cVocê não tem money suficiente.");
						    p.sendMessage("§cMoney necessário: §a$"+taxa+"§c.");
						    return true;
				        }
				        /**if(m.staff.keySet().contains(p.getName().toLowerCase())) {
							p.sendMessage("§cErro. Tente novamente mais tarde!");
							return true;
						}*/
				        m.econ.withdrawPlayer(p.getName(), taxa);
						p.sendMessage("§a§lBaú comprado com sucesso!");
						m.usando.put(p.getName().toLowerCase(), x);
						p.openInventory(m.getUtils().getBau(p.getName(), x));
						return true;
					}
				}
				p.sendMessage("§cVocê já comprou a quantidade máxima de baús!");
				return true;
			}else{
				if(!(p.hasPermission("pdgh.op")||p.hasPermission("pdgh.bauopenall"))) {
					p.sendMessage("§cSem permissões.");
					return true;
				}
				if((args.length<2)||(args.length>2)) {
					m.help(p);
					return true;
				}
				try {
					if(!StringUtils.isNumeric(args[1])) {
						m.help(p);
						return true;
					}
					int valor=Integer.parseInt(args[1]);
					if(!m.checkBau(p.getName().toLowerCase(), valor)) {
						p.sendMessage("§cO jogador §e"+args[0]+"§c não tem baú §e"+valor);
						return true;
					}
					//m.staff.put(args[0].toLowerCase(), valor);
					p.openInventory(m.getUtils().getBau(args[0], valor));
					return true;
				}catch(Exception e){
					m.help(p);
					return true;
				}
			}
		}
		return false;
	}

}
