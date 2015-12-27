package com.pwncraftpvp.zombies.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.pwncraftpvp.zombies.creator.BoxCreator;
import com.pwncraftpvp.zombies.creator.Creator;
import com.pwncraftpvp.zombies.creator.DogSpawnCreator;
import com.pwncraftpvp.zombies.creator.DoorCreator;
import com.pwncraftpvp.zombies.creator.EditorItem;
import com.pwncraftpvp.zombies.creator.PerkCreator;
import com.pwncraftpvp.zombies.creator.UpgradeCreator;
import com.pwncraftpvp.zombies.creator.WindowCreator;
import com.pwncraftpvp.zombies.creator.ZombieSpawnCreator;
import com.pwncraftpvp.zombies.events.PlayerTargetBlockEvent;
import com.pwncraftpvp.zombies.game.CustomSound;
import com.pwncraftpvp.zombies.game.Door;
import com.pwncraftpvp.zombies.game.Perk;
import com.pwncraftpvp.zombies.game.PowerUp;
import com.pwncraftpvp.zombies.game.Status;
import com.pwncraftpvp.zombies.game.Weapon;
import com.pwncraftpvp.zombies.game.Window;
import com.pwncraftpvp.zombies.tasks.CountdownTask;
import com.pwncraftpvp.zombies.tasks.GrenadeTask;
import com.pwncraftpvp.zombies.tasks.MysteryBoxTask;
import com.pwncraftpvp.zombies.tasks.PerkTask;
import com.pwncraftpvp.zombies.tasks.PlayerDeathTask;
import com.pwncraftpvp.zombies.tasks.PlayerHealTask;
import com.pwncraftpvp.zombies.tasks.UpgradeTask;
import com.pwncraftpvp.zombies.tasks.WindowRepairTask;
import com.pwncraftpvp.zombies.utils.EffectUtils;
import com.pwncraftpvp.zombies.utils.TextUtils;
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
			zplayer.sendMessage("You are in intermission. The game needs " + red + req + gray + " more " + ((req == 1) ? "player" : "players") + " to begin.");
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
		
		main.login.put(player.getName(), System.currentTimeMillis());
		
		Utils.createDatabaseEntry(player);
		
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
		final Player player = event.getPlayer();
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
		}else if(slot == 2){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				event.setCancelled(true);
			}
		}else if(slot == 3){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				if(!main.game.grenadecooldown.contains(player.getName()) && player.getItemInHand() != null && zplayer.getWeaponInHand() == Weapon.HAND_GRENADE){
					ItemStack hand = player.getItemInHand();
					if(hand.getAmount() > 1){
						hand.setAmount(hand.getAmount() - 1);
					}else{
						player.setItemInHand(null);
					}
					
					Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.FIREWORK_CHARGE));
					item.setVelocity(player.getLocation().getDirection());
					
					GrenadeTask task = new GrenadeTask(player, item);
					task.runTaskTimer(main, 0, 20);
					
					main.game.grenadecooldown.add(player.getName());
					main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
						public void run(){
							main.game.grenadecooldown.remove(player.getName());
						}
					}, 30);
				}
			}
		}
		
		if(zplayer.isInEditor()){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
				ItemStack hand = player.getItemInHand();
				if(hand != null){
					Material material = hand.getType();
					EditorItem editor = null;
					for(EditorItem e : EditorItem.values()){
						if(e.getMaterial() == material){
							editor = e;
							break;
						}
					}
					if(editor != null){
						event.setCancelled(true);
						if(editor == EditorItem.DOOR_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new DoorCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.BOX_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new BoxCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.WINDOW_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new WindowCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.UPGRADE_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new UpgradeCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.PERK_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new PerkCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.ZOMBIE_SPAWN_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new ZombieSpawnCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.DOG_SPAWN_CREATOR){
							if(!zplayer.isInCreator()){
								zplayer.enterCreator(new DogSpawnCreator(player, zplayer.getEditorMap()));
							}else{
								zplayer.getCreator().advanceStep();
							}
						}else if(editor == EditorItem.MAP_SPAWN_SETTER){
							String map = zplayer.getEditorMap().getName();
							main.getConfig().set("maps." + map + ".spawn.x", player.getLocation().getX());
							main.getConfig().set("maps." + map + ".spawn.y", player.getLocation().getY());
							main.getConfig().set("maps." + map + ".spawn.z", player.getLocation().getZ());
							main.getConfig().set("maps." + map + ".spawn.yaw", player.getLocation().getYaw());
							main.getConfig().set("maps." + map + ".spawn.pitch", player.getLocation().getPitch());
							main.saveConfig();
							
							zplayer.sendMessage("You have set the map spawn.");
						}
					}
				}
			}else if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR){
				Creator creator = zplayer.getCreator();
				if(creator != null){
					if(player.getItemInHand().getType() == creator.getEditorItem().getMaterial()){
						event.setCancelled(true);
						if(creator instanceof DoorCreator){
							if(event.getClickedBlock() != null){
								DoorCreator c = (DoorCreator) creator;
								if(creator.getStep() == 2 && event.getClickedBlock().getType() == Material.IRON_FENCE){
									c.addDoorBlock(event.getClickedBlock().getLocation());
								}
							}
						}else if(creator instanceof BoxCreator){
							if(event.getClickedBlock() != null){
								BoxCreator c = (BoxCreator) creator;
								if(creator.getStep() == 2 && event.getClickedBlock().getType() == Material.CHEST){
									c.addBoxBlock(event.getClickedBlock().getLocation());
								}else if(creator.getStep() == 3){
									c.addLightBlock(event.getClickedBlock().getLocation());
								}
							}
						}else if(creator instanceof WindowCreator){
							if(event.getClickedBlock() != null){
								WindowCreator c = (WindowCreator) creator;
								if(creator.getStep() == 2 && event.getClickedBlock().getType() == Material.IRON_FENCE){
									c.setWindowBlock(event.getClickedBlock().getLocation());
								}else if(creator.getStep() == 3 && event.getClickedBlock().getType() == Material.WALL_SIGN){
									c.setWindowSign(event.getClickedBlock().getLocation());
								}
							}
						}else if(creator instanceof UpgradeCreator){
							if(event.getClickedBlock() != null){
								UpgradeCreator c = (UpgradeCreator) creator;
								if(creator.getStep() == 1 && event.getClickedBlock().getType() == Material.ENDER_CHEST){
									c.setUpgradeBlock(event.getClickedBlock().getLocation());
								}
							}
						}else if(creator instanceof PerkCreator){
							if(event.getClickedBlock() != null){
								PerkCreator c = (PerkCreator) creator;
								if(creator.getStep() == 2 && event.getClickedBlock().getType() == Material.WOOL){
									c.setPerkBlock(event.getClickedBlock().getLocation());
								}
							}
						}else if(creator instanceof ZombieSpawnCreator){
							ZombieSpawnCreator c = (ZombieSpawnCreator) creator;
							if(creator.getStep() == 2){
								c.setSpawnLocation(player.getLocation());
							}
						}else if(creator instanceof DogSpawnCreator){
							DogSpawnCreator c = (DogSpawnCreator) creator;
							if(creator.getStep() == 2){
								c.setSpawnLocation(player.getLocation());
							}
						}
					}
				}
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
							CustomSound.DOOR_OPEN.play(block.getLocation());
							
							if(main.game.isUnlocked(door.getAreaID()) == false){
								main.game.addUnlockedArea(main.game.getArea(door.getAreaID()));
							}
							zplayer.removeScore(door.getPrice());
							zplayer.sendMessage("You have removed this door for " + red + door.getPrice() + gray + " points.");
							
							CustomSound.DOOR_OPEN.play(block.getLocation());
						}else{
							zplayer.sendError("Insufficient points.");
						}
					}
				}else if(block.getType() == Material.WALL_SIGN){
					Sign sign = (Sign) block.getState();
					if(sign.getLine(1).contains("Repair") == true && sign.getLine(2).contains("Window") == true){
						if(main.game.repair.containsKey(player.getName()) == false){
							Window window = null;
							for(Window w : main.game.getAllWindows()){
								if(w.getSignLocation().getBlockX() == block.getLocation().getBlockX() && 
										w.getSignLocation().getBlockY() == block.getLocation().getBlockY() && 
										w.getSignLocation().getBlockZ() == block.getLocation().getBlockZ()){
									window = w;
									break;
								}
							}
							if(window != null){
								if(main.game.windowhealth.get(window.getID()) < 6){
									WindowRepairTask task = new WindowRepairTask(player, window);
									task.runTaskTimer(main, 0, 10);
									main.game.repair.put(player.getName(), task);
								}
							}
						}else{
							main.game.repair.get(player.getName()).clicking = true;
						}
					}
				}else if(block.getType() == Material.CHEST){
					event.setCancelled(true);
					boolean box = false;
					for(Location l : main.game.getCurrentMysteryBox().getBlocks()){
						if((l.getBlockX() == block.getLocation().getBlockX() && 
								l.getBlockY() == block.getLocation().getBlockY() && 
								l.getBlockZ() == block.getLocation().getBlockZ()) || l.distance(block.getLocation()) <= 1){
							box = true;
							break;
						}
					}
					if(box == true){
						if(main.game.boxtask == null){
							if(zplayer.getScore() >= 950){
								zplayer.removeScore(950);
								
								main.game.boxtask = new MysteryBoxTask(player, main.game.getCurrentMysteryBox());
								main.game.boxtask.runTaskTimer(main, 0, 3);
								zplayer.sendMessage("You have purchased this box for " + red + 950 + gray + " points.");
								
								CustomSound.MYSTERY_BOX_OPEN.play(block.getLocation());
							}else{
								zplayer.sendError("Insufficient points.");
							}
						}else if(main.game.boxweapon.containsKey(player.getName()) == true){
							if(slot <= 1){
								zplayer.giveWeapon(main.game.boxweapon.get(player.getName()), false);
								zplayer.sendMessage("You have received the " + red + main.game.boxweapon.get(player.getName()).getName() + gray + ".");
								main.game.boxtask.cancelTask(true);
							}else{
								zplayer.sendError("You must be in the first or second slot.");
							}
						}
					}
				}else if(block.getType() == Material.ENDER_CHEST){
					event.setCancelled(true);
					if(Utils.areDifferent(block.getLocation(), main.game.getMap().getUpgrade()) == false){
						if(main.game.upgradetask == null){
							if((player.getInventory().getHeldItemSlot() == 0 || player.getInventory().getHeldItemSlot() == 1) && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
								if(zplayer.getScore() >= 5000){
									if(zplayer.isWeaponUpgraded() == false){
										zplayer.removeScore(5000);
										
										main.game.upgradetask = new UpgradeTask(player, zplayer.getWeaponInHand());
										main.game.upgradetask.runTaskTimer(main, 0, 20);
										player.setItemInHand(null);
										zplayer.sendMessage("You have purchased pack-a-punch for " + red + 5000 + gray + " points.");
										
										CustomSound.PACK_A_PUNCH_USE.play(block.getLocation());
									}else{
										zplayer.sendError("You have already upgraded this weapon.");
									}
								}else{
									zplayer.sendError("Insufficient points.");
								}
							}else{
								zplayer.sendError("You must be holding a weapon.");
							}
						}else if(main.game.upgradetask.getPlayer().getName().equalsIgnoreCase(player.getName()) == true){
							if(main.game.upgradetask.isWaiting() == true){
								zplayer.giveWeapon(main.game.upgradetask.getWeapon(), true);
								main.game.upgradetask.cancelTask();
							}
						}
					}
				}else if(block.getType() == Material.WOOL){
					Perk perk = null;
					for(Perk p : Perk.values()){
						if(main.game.getMap().getPerkLocation(p).distance(block.getLocation()) < 2){
							perk = p;
							break;
						}
					}
					if(perk != null){
						if(zplayer.getScore() >= perk.getPrice()){
							if(main.game.perktask.containsKey(player.getName()) == false){
								if(zplayer.hasPerk(perk) == false){
									PerkTask task = new PerkTask(player, perk, player.getInventory().getHeldItemSlot());
									task.runTaskTimer(main, 0, 10);
									main.game.perktask.put(player.getName(), task);
									player.getInventory().setHeldItemSlot(8);
									player.getInventory().setItem(8, Utils.renameItem(new ItemStack(Material.POTION), ChatColor.AQUA + perk.getName()));
									
									perk.getPurchaseSound().play(player);
								}
							}
						}else{
							zplayer.sendError("Insufficient points.");
						}
					}
				}else if(block.getType() == Material.LEVER){
					if(!Utils.areDifferent(main.game.getMap().getPowerLocation(), block.getLocation())){
						event.setCancelled(true);
						if(!main.game.isPowerOn()){
							main.game.setPowerOn();
							
							Utils.broadcastMessage("The power has been turned on.");
							CustomSound.POWER_ENABLE.playGlobally(8F);
						}
					}
				}else{
					zplayer.shootWeapon(slot);
				}
			}
		}
	}
	
	@EventHandler
	public void playerTargetBlock(PlayerTargetBlockEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		Block block = event.getNewBlock();
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
					Window window = null;
					for(Window w : main.game.getAllWindows()){
						if(w.getSignLocation().getBlockX() == block.getLocation().getBlockX() && 
								w.getSignLocation().getBlockY() == block.getLocation().getBlockY() && 
								w.getSignLocation().getBlockZ() == block.getLocation().getBlockZ()){
							window = w;
							break;
						}
					}
					if(window != null){
						if(main.game.windowhealth.get(window.getID()) < 6){
							zplayer.sendActionBar(gray + "Hold right-click to repair.");
						}
					}
				}
			}else if(block.getType() == Material.CHEST){
				boolean box = false;
				for(Location l : main.game.getCurrentMysteryBox().getBlocks()){
					if(Utils.areDifferent(l, block.getLocation()) == false || l.distance(block.getLocation()) <= 1){
						box = true;
						break;
					}
				}
				if(box == true){
					if(main.game.boxtask == null){
						zplayer.sendActionBar(gray + "Press right-click for a random weapon. [" + red + "950 points" + gray + "]");
					}else if(main.game.boxweapon.containsKey(player.getName()) == true){
						zplayer.sendActionBar(ChatColor.GRAY + "Press right-click to trade weapons. [" + ChatColor.RED + main.game.boxweapon.get(player.getName()).getName() + ChatColor.GRAY + "]");
					}
				}
			}else if(block.getType() == Material.ENDER_CHEST){
				if(Utils.areDifferent(block.getLocation(), main.game.getMap().getUpgrade()) == false){
					if(main.game.upgradetask == null){
						if(zplayer.isWeaponUpgraded() == false){
							zplayer.sendActionBar(gray + "Press right-click to upgrade weapon. [" + red + "5000 points" + gray + "]");
						}
					}else if(main.game.upgradetask.getPlayer().getName().equalsIgnoreCase(player.getName()) == true){
						if(main.game.upgradetask.isWaiting() == true){
							zplayer.sendActionBar(ChatColor.GRAY + "Press right-click to accept weapon. [" + ChatColor.RED + main.game.upgradetask.getWeapon().getUpgradedName() + ChatColor.GRAY + "]");
						}
					}
				}
			}else if(block.getType() == Material.WOOL){
				Perk perk = null;
				for(Perk p : Perk.values()){
					if(main.game.getMap().getPerkLocation(p).distance(block.getLocation()) < 2){
						perk = p;
						break;
					}
				}
				if(perk != null){
					if(main.game.perktask.containsKey(player.getName()) == false){
						if(zplayer.hasPerk(perk) == false){
							zplayer.sendActionBar(ChatColor.GRAY + "Press right-click to purchase " + perk.getName() + ". [" + ChatColor.RED + perk.getPrice() + " points" + ChatColor.GRAY + "]");
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Zombie || event.getEntity() instanceof Wolf){
			LivingEntity entity = (LivingEntity) event.getEntity();
			entity.setNoDamageTicks(0);
			Player player = null;
			Egg egg = null;
			if(event.getDamager() instanceof Egg){
				egg = (Egg) event.getDamager();
				if(egg.getShooter() != null && egg.getShooter() instanceof Player){
					player = (Player) egg.getShooter();
				}
			}else if(event.getDamager() instanceof Player){
				player = (Player) event.getDamager();
			}else{
				event.setCancelled(true);
			}
			
			if(player != null){
				final ZPlayer zplayer = new ZPlayer(player);
				Weapon weapon = zplayer.getWeaponInHand();
				if(weapon != null && ((egg != null && weapon != Weapon.KNIFE) || (egg == null && weapon == Weapon.KNIFE)) && weapon != Weapon.RAY_GUN){
					boolean upgraded = zplayer.isWeaponUpgraded();
					double damage = weapon.getDamage(upgraded);
					
					boolean headshot = false;
					int hitScore = 10;
					int killScore = 60;
					if(egg != null && egg.getLocation().getY() - entity.getLocation().getY() > 1.875){
						damage = weapon.getHeadshotDamage(upgraded);
						killScore = 100;
						headshot = true;
					}
					
					if(weapon == Weapon.KNIFE){
						killScore = 130;
						player.getInventory().setItem(2, null);
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
							public void run(){
								zplayer.setSlot(2, Weapon.KNIFE, 1);
							}
						}, 20);
					}
					
					if(main.game.instakilltask != null){
						damage = main.game.getZombieHealth() + 50;
					}
					if(main.game.doublepointstask != null){
						killScore *= 2;
						hitScore *= 2;
					}
					
					EffectUtils.playBloodEffect(entity, headshot);
					if(entity.getType() == EntityType.ZOMBIE){
						for(Player p : Bukkit.getOnlinePlayers()){
							p.playSound(entity.getLocation(), Sound.ZOMBIE_HURT, 1F, 1F);
						}
					}
					
					event.setCancelled(true);
					
					double newhealth = entity.getHealth() - damage;
					if(newhealth > 1){
						entity.setHealth(newhealth);
						Utils.setNavigation(entity, player.getLocation());
						entity.playEffect(EntityEffect.HURT);
						zplayer.addScore(hitScore);
					}else{
						main.game.killEntity(entity);
						zplayer.giveBrains(1);
						zplayer.setKills(zplayer.getKills() + 1);
						zplayer.addScore(killScore);
						
						if(entity.getType() == EntityType.WOLF){
							for(Player p : Bukkit.getOnlinePlayers()){
								p.playSound(entity.getLocation(), Sound.WOLF_HURT, 1F, 1F);
							}
						}
					}
				}else{
					event.setCancelled(true);
				}
			}
		}else if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			ZPlayer zplayer = new ZPlayer(player);
			if(event.getDamager().getType() == EntityType.ZOMBIE || event.getDamager().getType() == EntityType.WOLF){
				if(main.game.ending == false){
					final LivingEntity damager = (LivingEntity) event.getDamager();
					if(main.game.nodamage.contains(damager.getEntityId()) == false){
						if(!main.game.death.containsKey(player.getName())){
							if(event.getDamager().getType() == EntityType.ZOMBIE){
								Zombie z = (Zombie) event.getDamager();
								Utils.swingArms(z);
							}
							double damage = 12;
							int delay = 40;
							if(event.getDamager().getType() == EntityType.WOLF){
								damage = 3.5;
								delay = 15;
							}
							if(zplayer.hasPerk(Perk.JUGGERNOG) == true){
								damage = damage / 2.5;
							}
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
							main.game.nodamage.add(damager.getEntityId());
							main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
								public void run(){
									main.game.nodamage.remove(new Integer(damager.getEntityId()));
								}
							}, delay);
						}else{
							event.setCancelled(true);
							Utils.navigateToNearest(damager);
						}
					}else{
						event.setCancelled(true);
					}
				}else{
					event.setCancelled(true);
				}
			}else{
				event.setCancelled(true);
			}
		}else if(event.getEntity() instanceof EnderCrystal){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerInteractEntity(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		if(event.getRightClicked() instanceof Player){
			Player target = (Player) event.getRightClicked();
			if(main.game.death.containsKey(target.getName())){
				PlayerDeathTask task = main.game.death.get(target.getName());
				task.reviver = player;
				task.zreviver = zplayer;
				task.reviving = true;
				task.setHologramText(false);
				task.clicks++;
			}
		}
	}
	
	@EventHandler
	public void entityDamage(EntityDamageEvent event){
		if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK){
			event.setCancelled(true);
			if(event.getEntity().getType() == EntityType.ZOMBIE){
				event.getEntity().setFireTicks(0);
			}
		}
	}
	
	@EventHandler
	public void playerItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		int slot = event.getNewSlot();
		
		if(main.game.perktask.containsKey(player.getName()) == true){
			event.setCancelled(true);
		}else{
			if(main.game.reload.containsKey(player.getName()) == true){
				main.game.reload.get(player.getName()).cancelTask(false);
				main.game.reload.remove(player.getName());
			}
			
			Weapon weapon = zplayer.getWeaponInSlot(slot);
			if(weapon != null && weapon.isGun()){
				zplayer.sendAmmo(zplayer.getAmmo(slot, weapon));
			}else{
				zplayer.sendActionBar("");
			}
		}
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(main.game.death.containsKey(player.getName())){
			if(event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ()){
				player.teleport(event.getFrom());
			}
		}else{
			if(main.game.poweruptask != null){
				PowerUp powerup = main.game.poweruptask.getPowerUp();
				if(player.getLocation().distance(powerup.getLocation()) < 2.5){
					main.game.applyPowerUp(powerup.getType());
					main.game.poweruptask.cancelTask();
				}
			}
		}
	}
	
	@EventHandler
	public void asyncPlayerChatEvent(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		ZPlayer zplayer = new ZPlayer(player);
		
		event.setFormat(ChatColor.GOLD + "[Lvl. " + 1 + "] " + ChatColor.DARK_GREEN + "%s " + ChatColor.GOLD + TextUtils.getArrow() + ChatColor.GRAY + " %s");
		
		Creator creator = zplayer.getCreator();
		if(creator != null){
			if(creator instanceof DoorCreator){
				DoorCreator c = (DoorCreator) creator;
				if(creator.getStep() == 1){
					if(Utils.isInteger(event.getMessage())){
						c.setAreaID(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}else if(creator.getStep() == 3){
					if(Utils.isInteger(event.getMessage())){
						c.setDoorPrice(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}
			}else if(creator instanceof BoxCreator){
				BoxCreator c = (BoxCreator) creator;
				if(creator.getStep() == 1){
					if(Utils.isInteger(event.getMessage())){
						c.setAreaID(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}
			}else if(creator instanceof WindowCreator){
				WindowCreator c = (WindowCreator) creator;
				if(creator.getStep() == 1){
					if(Utils.isInteger(event.getMessage())){
						c.setAreaID(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}
			}else if(creator instanceof PerkCreator){
				PerkCreator c = (PerkCreator) creator;
				if(creator.getStep() == 1){
					String msg = event.getMessage();
					Perk perk = null;
					for(Perk p : Perk.values()){
						if(p.toString().equalsIgnoreCase(msg)){
							perk = p;
							break;
						}
					}
					if(perk != null){
						c.setPerk(perk);
					}else{
						zplayer.sendError("You have entered an invalid perk.");
					}
					event.setCancelled(true);
				}
			}else if(creator instanceof ZombieSpawnCreator){
				ZombieSpawnCreator c = (ZombieSpawnCreator) creator;
				if(creator.getStep() == 1){
					if(Utils.isInteger(event.getMessage())){
						c.setAreaID(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}
			}else if(creator instanceof DogSpawnCreator){
				DogSpawnCreator c = (DogSpawnCreator) creator;
				if(creator.getStep() == 1){
					if(Utils.isInteger(event.getMessage())){
						c.setAreaID(Integer.parseInt(event.getMessage()));
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void projectileHit(ProjectileHitEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Egg){
			Egg egg = (Egg) entity;
			
			if(egg.getShooter() instanceof Player){
				Player player = (Player) egg.getShooter();
				ZPlayer zplayer = new ZPlayer(player);
				if(zplayer.getWeaponInHand() == Weapon.RAY_GUN){
					EffectUtils.playRayGunEffect(egg.getLocation());
					for(Entity e : egg.getNearbyEntities(1.5, 1.5, 1.5)){
						if(e instanceof Zombie){
							Zombie z = (Zombie) e;
							
							int hitScore = 10;
							int killScore = 50;
							double damage = Weapon.RAY_GUN.getDamage(zplayer.isWeaponUpgraded());
							if(main.game.instakilltask != null){
								damage = main.game.getZombieHealth() + 50;
							}
							if(main.game.doublepointstask != null){
								killScore *= 2;
								hitScore *= 2;
							}
							
							EffectUtils.playBloodEffect(z, false);
							double newhealth = z.getHealth() - damage;
							if(newhealth > 1){
								z.setHealth(newhealth);
								z.playEffect(EntityEffect.HURT);
								zplayer.addScore(hitScore);
							}else{
								main.game.killEntity(z);
								zplayer.giveBrains(1);
								zplayer.setKills(zplayer.getKills() + 1);
								zplayer.addScore(killScore);
							}
						}
					}
				}
			}
			
			BlockIterator iterator = new BlockIterator(entity.getWorld(), entity.getLocation().toVector(), entity.getVelocity().normalize(), 0, 4);
			Block hitBlock = null;
			while(iterator.hasNext()){
				hitBlock = iterator.next();
				if(hitBlock.getType() != Material.AIR){
					break;
				}
			}
			if(hitBlock != null){
				for(Entity e : entity.getNearbyEntities(20, 20, 20)){
					if(e instanceof Player){
						Player p = (Player) e;
						p.playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, hitBlock.getTypeId());
					}
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
			if(main.game.death.containsKey(player.getName()) == true){
				event.setCancelled(true);
				Player nearest = Utils.navigateToNearest((LivingEntity) event.getEntity());
				if(nearest == null){
					event.setTarget(null);
				}else{
					event.setTarget(nearest);
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
	
	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent event){
		event.setCancelled(true);
		event.getEntity().remove();
	}

}
