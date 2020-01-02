package me.badbones69.crazyauctions;

import me.badbones69.crazyauctions.api.Category;
import me.badbones69.crazyauctions.api.FileManager;
import me.badbones69.crazyauctions.api.ShopType;
import me.badbones69.crazyauctions.controllers.GUI;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class VillagerAuction implements Listener {

	private Main mainInstance;

	VillagerAuction(Main mainInstance) {
		this.mainInstance = mainInstance;
	}

	@EventHandler
	public void interact(final PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() instanceof Villager) {
			if (e.getRightClicked().getCustomName() != null) {
				if (e.getRightClicked().getCustomName().equalsIgnoreCase(mainInstance.getConfig().getString("Settings.NPCName"))) {
					GUI.openShop(e.getPlayer(), ShopType.BID, Category.NONE, 1);
					e.setCancelled(true);
				}
			}
		}
	}

//	@EventHandler
//	public void onDamage(EntityDamageEvent e) {
//		if (e instanceof EntityDamageByEntityEvent) {
//			return;
//		}
//		if (e.getEntity().getCustomName() != null)
//			if (e.getEntity().getCustomName().equalsIgnoreCase(config.getString("Settings.NPCName"))) {
//				e.setCancelled(true);
//				if (e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.FIRE) {
//					((LivingEntity) e.getEntity()).setFireTicks(0);
//				}
//			}
//	}
//
//	@EventHandler
//	public void onhit(EntityDamageByEntityEvent e) {
//		if (e.getEntity().getCustomName() != null)
//			if (e.getEntity().getCustomName().equalsIgnoreCase(config.getString("Settings.NPCName"))) {
//				if (!(e.getDamager() instanceof Player)) {
//					e.setCancelled(true);
//					return;
//				}
//				if (!e.getDamager().hasPermission("crazyauctions.destroy")) {
//					e.setCancelled(true);
//				} else {
//					e.getEntity().remove();
//					if (e.getEntity() instanceof Villager) {
//						((Player) e.getDamager()).sendMessage(config.getString("Settings.Prefix") + " Villager has been removed");
////						Main.tpbackto.remove(e.getEntity().getUniqueId());
////						Main.instance.getConfig().set("NPCS." + e.getEntity().getUniqueId().toString(), null);
////						Main.instance.saveConfig();
//					}
//				}
////				else {
////					Main.removeAuctions.remove(e.getDamager().getUniqueId());
////					if (e.getEntity() instanceof Villager)
////						((Player) e.getDamager()).sendMessage(Main.prefix + " Villager removal canceled");
////				}
//				}
//			}
//	}

	public static Entity spawnVillager(Location loc, Main mainInstance) {
		Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		v.setAdult();
		v.setAI(false);
		v.setSilent(true);
		v.setCustomNameVisible(true);
		v.setCustomName(mainInstance.getConfig().getString("Settings.NPCName"));
//		Main.tpbackto.put(v.getUniqueId(), loc);
//		Main.instance.getConfig().set("NPCS." + v.getUniqueId().toString(), loc);
//		Main.instance.saveConfig();
		return v;
	}
}
