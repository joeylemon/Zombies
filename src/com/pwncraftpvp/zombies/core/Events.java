package com.pwncraftpvp.zombies.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BlockIterator;

import com.pwncraftpvp.zombies.events.PlayerTargetBlockEvent;
import com.pwncraftpvp.zombies.game.Door;
import com.pwncraftpvp.zombies.game.Status;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.tasks.CountdownTask;
import com.pwncraftpvp.zombies.tasks.PlayerHealTask;
import com.pwncraftpvp.zombies.tasks.WindowRepairTask;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.Utils;

public class Events implements Listener {
	
	private Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String red = ChatColor.RED + "";
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		
		int online = Bukkit.getOnlinePlayers().length;
		int min = Utils.getMinimumPlayers();
		if(online < min){
			int req = min - online;
			String s = "s";
			if(req == 1){
				s = "";
			}
			zplayer.sendMessage("You are in intermission. The game needs " + red + req + gray + " more player" + s + " to begin.");
		}else{
			if(main.game.getStatus() == Status.WAITING){
				CountdownTask task = new CountdownTask();
				task.runTaskTimer(main, 0, 20);
				main.game.votingtask = task;
				main.game.setStatus(Status.VOTING);
				main.game.setVoteables();
			}
		}
		
		player.setHealth(20);
		zplayer.setInventory(main.game.getStatus());
		
		Statistics stats = new Statistics(player);
		stats.pull();
		main.stats.put(player.getName(), stats);
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		zplayer.logout();
	}
	
	@EventHandler
	public void playerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		zplayer.logout();
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		int slot = player.getInventory().getHeldItemSlot();
		
		if(slot == 0 || slot == 1){
			if(event.getAction() == Action.RIGHT_CLICK_AIR){
				event.setCancelled(true);
				zplayer.shootWeapon(slot);
			}else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				event.setCancelled(true);
				zplayer.reloadWeapon(slot);
			}
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			if(block != null){
				if(block.getType() == Material.IRON_FENCE){
					Door door = null;
					for(Door d : main.game.getAllDoors()){
						if(d.isBlock(block) == true){
							door = d;
							break;
						}
					}
					if(door != null){
						if(zplayer.getScore() >= door.getPrice()){
							for(Block b : door.getBlocks()){
								for(Player p : Bukkit.getOnlinePlayers()){
									p.playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
								}
								b.setType(Material.AIR);
							}
							if(main.game.isUnlocked(door.getAreaID()) == false){
								main.game.addUnlockedArea(main.game.getArea(door.getAreaID()));
							}
							zplayer.removeScore(door.getPrice());
							zplayer.sendMessage("You have removed this door for " + red + door.getPrice() + gray + " points.");
						}else{
							zplayer.sendError("Insufficient points.");
						}
					}
				}else if(block.getType() == Material.WALL_SIGN){
					Sign sign = (Sign) block.getState();
					if(sign.getLine(1).contains("Repair") == true && sign.getLine(2).contains("Window") == true){
						if(main.game.repair.containsKey(player.getName()) == false){
							Block down = block.getRelative(BlockFace.DOWN);
							Window window = null;
							for(BlockFace f : main.faces){
								Window w = main.game.getWindow(down.getRelative(f).getLocation());
								if(w != null){
									window = w;
									break;
								}
							}
							if(window != null){
								if(main.game.windowhealth.get(window.getID()) < 6){
									WindowRepairTask task = new WindowRepairTask(player, window);
									task.runTaskTimer(main, 0, 30);
									main.game.repair.put(player.getName(), task);
								}
							}
						}else{
							main.game.repair.get(player.getName()).clicking = true;
						}
					}
				}else{
					zplayer.shootWeapon(slot);
				}
			}
		}
	}
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Zombie){
			Zombie zombie = (Zombie) event.getEntity();
			zombie.setNoDamageTicks(0);
			if(event.getDamager() instanceof Egg){
				Egg egg = (Egg) event.getDamager();
				if(egg.getShooter() != null && egg.getShooter() instanceof Player){
					Player player = (Player) egg.getShooter();
					ZPlayer zplayer = new ZPlayer(player);
					Weapon weapon = zplayer.getWeaponInHand();
					if(weapon != null){
						boolean upgraded = zplayer.isWeaponUpgraded();
						double damage = weapon.getDamage(upgraded);
						
						boolean headshot = false;
						if(egg.getLocation().getY() - zombie.getLocation().getY() > 1.4){
							damage = weapon.getHeadshotDamage(upgraded);
							headshot = true;
						}
						
						EffectUtils.playBloodEffect(zombie, headshot);
						event.setCancelled(true);
						
						double newhealth = zombie.getHealth() - damage;
						if(newhealth > 1){
							zombie.setHealth(newhealth);
							zombie.playEffect(EntityEffect.HURT);
							zplayer.addScore(10);
						}else{
							zombie.setHealth(0);
							zplayer.giveBrains(1);
							if(headshot == false){
								zplayer.addScore(60);
							}else{
								zplayer.addScore(100);
							}
						}
					}
				}
			}else{
				event.setCancelled(true);
			}
		}else if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			ZPlayer zplayer = new ZPlayer(player);
			if(event.getDamager() instanceof Zombie){
				if(main.game.dead.containsKey(player.getName()) == false && main.game.ending == false){
					final Zombie zombie = (Zombie) event.getDamager();
					if(main.game.nodamage.contains(zombie.getEntityId()) == false){
						double damage = 12;
						event.setDamage(damage);
						double newhealth = player.getHealth() - damage;
						if(newhealth < 1){
							zplayer.toggleDead(true, false);
							event.setCancelled(true);
							player.damage(0);
							player.setHealth(1);
							if(main.game.heal.containsKey(player.getName()) == true){
								main.game.heal.get(player.getName()).cancelTask(false);
								main.game.heal.remove(player.getName());
							}
						}else{
							if(main.game.heal.containsKey(player.getName()) == true){
								main.game.heal.get(player.getName()).runtime = 0;
							}else{
								PlayerHealTask task = new PlayerHealTask(player);
								task.runTaskTimer(main, 0, 20);
								main.game.heal.put(player.getName(), task);
							}
						}
						main.game.nodamage.add(zombie.getEntityId());
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								main.game.nodamage.remove(new Integer(zombie.getEntityId()));
							}
						}, 40);
					}else{
						event.setCancelled(true);
					}
				}else{
					event.setCancelled(true);
				}
			}else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void entityDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Zombie){
			Zombie zombie = (Zombie) event.getEntity();
			if(zombie.getHealth() <= 1){
				main.game.killed++;
				if(main.game.killed >= Utils.getZombiesForRound(main.game.getRound())){
					main.game.endRound();
				}
			}
		}
	}
	
	@EventHandler
	public void playerItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		int slot = event.getNewSlot();
		
		if(main.game.reload.containsKey(player.getName()) == true){
			main.game.reload.get(player.getName()).cancelTask(false);
			main.game.reload.remove(player.getName());
		}
		
		Weapon weapon = zplayer.getWeaponInSlot(slot);
		if(weapon != null){
			zplayer.sendAmmo(zplayer.getAmmo(slot, weapon));
		}else{
			zplayer.sendActionBar("");
		}
	}
	
	@EventHandler
	public void playerTargetBlock(PlayerTargetBlockEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		Block block = event.getNewBlock();
		Block old = event.getOldBlock();
		if(main.game.getStatus() == Status.STARTED){
			if(block.getType() == Material.IRON_FENCE){
				Door door = null;
				for(Door d : main.game.getAllDoors()){
					if(d.isBlock(block) == true){
						door = d;
						break;
					}
				}
				if(door != null){
					zplayer.sendActionBar(gray + "Press right-click to open. [" + red + door.getPrice() + " points" + gray + "]");
				}
			}else if(block.getType() == Material.WALL_SIGN){
				Sign sign = (Sign) block.getState();
				if(sign.getLine(1).contains("Repair") == true && sign.getLine(2).contains("Window") == true){
					Block down = block.getRelative(BlockFace.DOWN);
					Window window = null;
					for(BlockFace f : main.faces){
						Window w = main.game.getWindow(down.getRelative(f).getLocation());
						if(w != null){
							window = w;
							break;
						}
					}
					if(window != null){
						if(main.game.windowhealth.get(window.getID()) < 6){
							zplayer.sendActionBar(gray + "Press right-click to repair.");
						}
					}
				}
			}else{
				if(old.getType() == Material.WALL_SIGN && block.getType() != Material.WALL_SIGN){
					if(main.game.repair.containsKey(player.getName()) == true){
						main.game.repair.get(player.getName()).cancelTask();
						zplayer.sendMessage("You have stopped repairing the window.");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void projectileHit(ProjectileHitEvent event){
	    BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0, 4);
	    Block hitBlock = null;

	    while(iterator.hasNext()) {
	        hitBlock = iterator.next();
	        if(hitBlock.getTypeId() != 0){
	            break;
	        }
	    }
		
		if(hitBlock != null){
			for(Entity e : event.getEntity().getNearbyEntities(20, 20, 20)){
				if(e instanceof Player){
					Player p = (Player) e;
					p.playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, hitBlock.getTypeId());
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player) event.getWhoClicked();
			if(player.isOp() == false){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
		if(event.getSpawnReason() != SpawnReason.CUSTOM){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityTarget(EntityTargetEvent event){
		if(event.getEntity() instanceof Zombie && event.getTarget() instanceof Player){
			Player player = (Player) event.getTarget();
			if(main.game.dead.containsKey(player.getName()) == true){
				event.setCancelled(true);
				LivingEntity nearby = null;
				for(Entity e : player.getNearbyEntities(20, 20, 20)){
					if(e instanceof LivingEntity){
						nearby = (LivingEntity) e;
						break;
					}
				}
				if(nearby != null){
					event.setTarget(nearby);
				}
			}
		}
	}
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			player.setFoodLevel(20);
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void playerPickupItem(PlayerPickupItemEvent event){
		event.getItem().remove();
		event.setCancelled(true);
	}

}
