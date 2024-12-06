package pdghbau.eventos;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pdghbau.metodos.KwMetodos;

public class KwEventos implements Listener {

	private KwMetodos m;

	public KwEventos(KwMetodos m) {
		this.m = m;
	}

	@EventHandler
	void quandoFechar(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		if (inv.getName().equalsIgnoreCase(m.getReplaced("Bau.GUI.Nome").replace("{p}", p.getName()))) {
			if(!m.usando.keySet().contains(p.getName().toLowerCase())) {
				p.sendMessage("§cSeu bau não está aberto!");
				return;
			}
			int valor = m.usando.get(p.getName().toLowerCase());
			m.usando.remove(p.getName().toLowerCase());
			ArrayList<String> array = new ArrayList<>();
			for (ItemStack is : inv.getContents()) {
				if (is != null) {
					array.add(m.getUtils().conveterItemStack(is));
				}
			}
			if (m.getBau(p.getName(), valor) != null) {
				String i = m.getBau(p.getName(), valor);
				if (i.equalsIgnoreCase(array.toString())) {
					return;
				}
			}
			if (!m.checkBau(p.getName(), valor)) {
				m.criarBau(p.getName(), valor, array.toString());
				p.sendMessage(m.getReplaced("Bau.Salvo").replace("{x}", ""+valor));
				return;
			}
			m.atualizarBau(p.getName(), valor, array.toString());
			p.sendMessage(m.getReplaced("Bau.Atualizado").replace("{x}", ""+valor));
		}
	}

}
