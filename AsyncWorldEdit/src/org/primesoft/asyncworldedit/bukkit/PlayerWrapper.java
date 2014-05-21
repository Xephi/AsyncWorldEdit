/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primesoft.asyncworldedit.bukkit;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

/**
 * Proxy class used to provide asynced worlds
 *
 * @author SBPrime
 */
public class PlayerWrapper implements Player {

    private final Player m_parent;

    public PlayerWrapper(Player parent) {
        m_parent = parent;
    }

    @Override
    public World getWorld() {
        return new WorldWrapper(m_parent.getWorld());
    }

    @Override
    public Location getLocation() {
        Location location = m_parent.getLocation();
        
        return new Location(getWorld(), 
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    @Override
    public String getDisplayName() {
        return m_parent.getDisplayName();
    }

    @Override
    public void setDisplayName(String string) {
        m_parent.setDisplayName(string);
    }

    @Override
    public String getPlayerListName() {
        return m_parent.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String string) {
        m_parent.setPlayerListName(string);
    }

    @Override
    public void setCompassTarget(Location lctn) {
        m_parent.setCompassTarget(lctn);
    }

    @Override
    public Location getCompassTarget() {
        return m_parent.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return m_parent.getAddress();
    }

    @Override
    public void sendRawMessage(String string) {
        m_parent.sendRawMessage(string);
    }

    @Override
    public void kickPlayer(String string) {
        m_parent.kickPlayer(string);
    }

    @Override
    public void chat(String string) {
        m_parent.chat(string);
    }

    @Override
    public boolean performCommand(String string) {
        return m_parent.performCommand(string);
    }

    @Override
    public boolean isSneaking() {
        return m_parent.isSneaking();
    }

    @Override
    public void setSneaking(boolean bln) {
        m_parent.setSneaking(bln);
    }

    @Override
    public boolean isSprinting() {
        return m_parent.isSprinting();
    }

    @Override
    public void setSprinting(boolean bln) {
        m_parent.setSprinting(bln);
    }

    @Override
    public void saveData() {
        m_parent.saveData();
    }

    @Override
    public void loadData() {
        m_parent.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean bln) {
        m_parent.setSleepingIgnored(bln);
    }

    @Override
    public boolean isSleepingIgnored() {
        return m_parent.isSleepingIgnored();
    }

    @Override
    public void playNote(Location lctn, byte b, byte b1) {
        m_parent.playNote(lctn, b, b1);
    }

    @Override
    public void playNote(Location lctn, Instrument i, Note note) {
        m_parent.playNote(lctn, i, note);
    }

    @Override
    public void playSound(Location lctn, Sound sound, float f, float f1) {
        m_parent.playSound(lctn, sound, f, f1);
    }

    @Override
    public void playSound(Location lctn, String string, float f, float f1) {
        m_parent.playSound(lctn, string, f, f1);
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i) {
        m_parent.playEffect(lctn, effect, i);
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
        m_parent.playEffect(lctn, effect, t);
    }

    @Override
    public void sendBlockChange(Location lctn, Material mtrl, byte b) {
        m_parent.sendBlockChange(lctn, mtrl, b);
    }

    @Override
    public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes) {
        return m_parent.sendChunkChange(lctn, i, i1, i2, bytes);
    }

    @Override
    public void sendBlockChange(Location lctn, int i, byte b) {
        m_parent.sendBlockChange(lctn, i, b);
    }

    @Override
    public void sendSignChange(Location lctn, String[] strings) throws IllegalArgumentException {
        m_parent.sendSignChange(lctn, strings);
    }

    @Override
    public void sendMap(MapView mv) {
        m_parent.sendMap(mv);
    }

    @Override
    public void updateInventory() {
        m_parent.updateInventory();
    }

    @Override
    public void awardAchievement(Achievement a) {
        m_parent.awardAchievement(a);
    }

    @Override
    public void removeAchievement(Achievement a) {
        m_parent.removeAchievement(a);
    }

    @Override
    public boolean hasAchievement(Achievement a) {
        return m_parent.hasAchievement(a);
    }

    @Override
    public void incrementStatistic(Statistic ststc) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc);
    }

    @Override
    public void decrementStatistic(Statistic ststc) throws IllegalArgumentException {
        m_parent.decrementStatistic(ststc);
    }

    @Override
    public void incrementStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc, i);
    }

    @Override
    public void decrementStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        m_parent.decrementStatistic(ststc, i);
    }

    @Override
    public void setStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        m_parent.setStatistic(ststc, i);
    }

    @Override
    public int getStatistic(Statistic ststc) throws IllegalArgumentException {
        return m_parent.getStatistic(ststc);
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc, mtrl);
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        m_parent.decrementStatistic(ststc, mtrl);
    }

    @Override
    public int getStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        return m_parent.getStatistic(ststc, mtrl);
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc, mtrl, i);
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        m_parent.decrementStatistic(ststc, mtrl, i);
    }

    @Override
    public void setStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        m_parent.setStatistic(ststc, mtrl, i);
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc, et);
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        m_parent.decrementStatistic(ststc, et);
    }

    @Override
    public int getStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        return m_parent.getStatistic(ststc, et);
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et, int i) throws IllegalArgumentException {
        m_parent.incrementStatistic(ststc, et, i);
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et, int i) {
        m_parent.decrementStatistic(ststc, et, i);
    }

    @Override
    public void setStatistic(Statistic ststc, EntityType et, int i) {
        m_parent.setStatistic(ststc, et, i);
    }

    @Override
    public void setPlayerTime(long l, boolean bln) {
        m_parent.setPlayerTime(l, bln);
    }

    @Override
    public long getPlayerTime() {
        return m_parent.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return m_parent.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return m_parent.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        m_parent.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType wt) {
        m_parent.setPlayerWeather(wt);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return m_parent.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        m_parent.resetPlayerWeather();
    }

    @Override
    public void giveExp(int i) {
        m_parent.giveExp(i);
    }

    @Override
    public void giveExpLevels(int i) {
        m_parent.giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return m_parent.getExp();
    }

    @Override
    public void setExp(float f) {
        m_parent.setExp(f);
    }

    @Override
    public int getLevel() {
        return m_parent.getLevel();
    }

    @Override
    public void setLevel(int i) {
        m_parent.setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return m_parent.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        m_parent.setTotalExperience(i);
    }

    @Override
    public float getExhaustion() {
        return m_parent.getExhaustion();
    }

    @Override
    public void setExhaustion(float f) {
        m_parent.setExhaustion(f);
    }

    @Override
    public float getSaturation() {
        return m_parent.getSaturation();
    }

    @Override
    public void setSaturation(float f) {
        m_parent.setSaturation(f);
    }

    @Override
    public int getFoodLevel() {
        return m_parent.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        m_parent.setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation() {
        return m_parent.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location lctn) {
        m_parent.setBedSpawnLocation(lctn);
    }

    @Override
    public void setBedSpawnLocation(Location lctn, boolean bln) {
        m_parent.setBedSpawnLocation(lctn, bln);
    }

    @Override
    public boolean getAllowFlight() {
        return m_parent.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean bln) {
        m_parent.setAllowFlight(bln);
    }

    @Override
    public void hidePlayer(Player player) {
        m_parent.hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        m_parent.showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return m_parent.canSee(player);
    }

    @Override
    public boolean isOnGround() {
        return m_parent.isOnGround();
    }

    @Override
    public boolean isFlying() {
        return m_parent.isFlying();
    }

    @Override
    public void setFlying(boolean bln) {
        m_parent.setFlying(bln);
    }

    @Override
    public void setFlySpeed(float f) throws IllegalArgumentException {
        m_parent.setFlySpeed(f);
    }

    @Override
    public void setWalkSpeed(float f) throws IllegalArgumentException {
        m_parent.setWalkSpeed(f);
    }

    @Override
    public float getFlySpeed() {
        return m_parent.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return m_parent.getWalkSpeed();
    }

    @Override
    public void setTexturePack(String string) {
        m_parent.setTexturePack(string);
    }

    @Override
    public void setResourcePack(String string) {
        m_parent.setResourcePack(string);
    }

    @Override
    public Scoreboard getScoreboard() {
        return m_parent.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scrbrd) throws IllegalArgumentException, IllegalStateException {
        m_parent.setScoreboard(scrbrd);
    }

    @Override
    public boolean isHealthScaled() {
        return m_parent.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean bln) {
        m_parent.setHealthScaled(bln);
    }

    @Override
    public void setHealthScale(double d) throws IllegalArgumentException {
        m_parent.setHealthScale(d);
    }

    @Override
    public double getHealthScale() {
        return m_parent.getHealthScale();
    }

    @Override
    public String getName() {
        return m_parent.getName();
    }

    @Override
    public PlayerInventory getInventory() {
        return m_parent.getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return m_parent.getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prprt, int i) {
        return m_parent.setWindowProperty(prprt, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return m_parent.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory invntr) {
        return m_parent.openInventory(invntr);
    }

    @Override
    public InventoryView openWorkbench(Location lctn, boolean bln) {
        return m_parent.openWorkbench(lctn, bln);
    }

    @Override
    public InventoryView openEnchanting(Location lctn, boolean bln) {
        return m_parent.openEnchanting(lctn, bln);
    }

    @Override
    public void openInventory(InventoryView iv) {
        m_parent.openInventory(iv);
    }

    @Override
    public void closeInventory() {
        m_parent.closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return m_parent.getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack is) {
        m_parent.setItemInHand(is);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return m_parent.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack is) {
        m_parent.setItemOnCursor(is);
    }

    @Override
    public boolean isSleeping() {
        return m_parent.isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return m_parent.getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return m_parent.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gm) {
        m_parent.setGameMode(gm);
    }

    @Override
    public boolean isBlocking() {
        return m_parent.isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return m_parent.getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return m_parent.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean bln) {
        return m_parent.getEyeHeight(bln);
    }

    @Override
    public Location getEyeLocation() {
        return m_parent.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hs, int i) {
        return m_parent.getLineOfSight(hs, i);
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> hs, int i) {
        return m_parent.getTargetBlock(hs, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i) {
        return m_parent.getLastTwoTargetBlocks(hs, i);
    }

    @Override
    public Egg throwEgg() {
        return m_parent.throwEgg();
    }

    @Override
    public Snowball throwSnowball() {
        return m_parent.throwSnowball();
    }

    @Override
    public Arrow shootArrow() {
        return m_parent.shootArrow();
    }

    @Override
    public int getRemainingAir() {
        return m_parent.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        m_parent.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return m_parent.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        m_parent.setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return m_parent.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        m_parent.setMaximumNoDamageTicks(i);
    }

    public double getLastDamage() {
        return m_parent.getLastDamage();
    }

    @Override
    public void setLastDamage(double d) {
        m_parent.setLastDamage(d);
    }

    @Override
    public int getNoDamageTicks() {
        return m_parent.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        m_parent.setNoDamageTicks(i);
    }

    @Override
    public Player getKiller() {
        return m_parent.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe) {
        return m_parent.addPotionEffect(pe);
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe, boolean bln) {
        return m_parent.addPotionEffect(pe, bln);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> clctn) {
        return m_parent.addPotionEffects(clctn);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType pet) {
        return m_parent.hasPotionEffect(pet);
    }

    @Override
    public void removePotionEffect(PotionEffectType pet) {
        m_parent.removePotionEffect(pet);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return m_parent.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return m_parent.hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return m_parent.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean bln) {
        m_parent.setRemoveWhenFarAway(bln);
    }

    @Override
    public EntityEquipment getEquipment() {
        return m_parent.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean bln) {
        m_parent.setCanPickupItems(bln);
    }

    @Override
    public boolean getCanPickupItems() {
        return m_parent.getCanPickupItems();
    }

    @Override
    public void setCustomName(String string) {
        m_parent.setCustomName(string);
    }

    @Override
    public String getCustomName() {
        return m_parent.getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean bln) {
        m_parent.setCustomNameVisible(bln);
    }

    @Override
    public boolean isCustomNameVisible() {
        return m_parent.isCustomNameVisible();
    }

    @Override
    public boolean isLeashed() {
        return m_parent.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return m_parent.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return m_parent.setLeashHolder(entity);
    }

    @Override
    public Location getLocation(Location lctn) {
        return m_parent.getLocation(lctn);
    }

    @Override
    public void setVelocity(Vector vector) {
        m_parent.setVelocity(vector);
    }

    @Override
    public Vector getVelocity() {
        return m_parent.getVelocity();
    }

    @Override
    public boolean teleport(Location lctn) {
        return m_parent.teleport(lctn);
    }

    @Override
    public boolean teleport(Location lctn, PlayerTeleportEvent.TeleportCause tc) {
        return m_parent.teleport(lctn, tc);
    }

    @Override
    public boolean teleport(Entity entity) {
        return m_parent.teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause tc) {
        return m_parent.teleport(entity, tc);
    }

    @Override
    public List<Entity> getNearbyEntities(double d, double d1, double d2) {
        return m_parent.getNearbyEntities(d, d1, d2);
    }

    @Override
    public int getEntityId() {
        return m_parent.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return m_parent.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return m_parent.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        m_parent.setFireTicks(i);
    }

    @Override
    public void remove() {
        m_parent.remove();
    }

    @Override
    public boolean isDead() {
        return m_parent.isDead();
    }

    @Override
    public boolean isValid() {
        return m_parent.isValid();
    }

    @Override
    public Server getServer() {
        return m_parent.getServer();
    }

    @Override
    public Entity getPassenger() {
        return m_parent.getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return m_parent.setPassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return m_parent.isEmpty();
    }

    @Override
    public boolean eject() {
        return m_parent.eject();
    }

    @Override
    public float getFallDistance() {
        return m_parent.getFallDistance();
    }

    @Override
    public void setFallDistance(float f) {
        m_parent.setFallDistance(f);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent ede) {
        m_parent.setLastDamageCause(ede);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return m_parent.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId() {
        return m_parent.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return m_parent.getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        m_parent.setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect ee) {
        m_parent.playEffect(ee);
    }

    @Override
    public EntityType getType() {
        return m_parent.getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return m_parent.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return m_parent.leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return m_parent.getVehicle();
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        m_parent.setMetadata(string, mv);
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return m_parent.getMetadata(string);
    }

    @Override
    public boolean hasMetadata(String string) {
        return m_parent.hasMetadata(string);
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        m_parent.removeMetadata(string, plugin);
    }

    @Override
    public void damage(double d) {
        m_parent.damage(d);
    }

    @Override
    public void damage(double d, Entity entity) {
        m_parent.damage(d, entity);
    }

    @Override
    public double getHealth() {
        return m_parent.getHealth();
    }

    @Override
    public void setHealth(double d) {
        m_parent.setHealth(d);
    }

    @Override
    public double getMaxHealth() {
        return m_parent.getMaxHealth();
    }

    @Override
    public void setMaxHealth(double d) {
        m_parent.setMaxHealth(d);
    }

    @Override
    public void resetMaxHealth() {
        m_parent.resetMaxHealth();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type) {
        return m_parent.launchProjectile(type);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type, Vector vector) {
        return m_parent.launchProjectile(type, vector);
    }

    @Override
    public boolean isPermissionSet(String string) {
        return m_parent.isPermissionSet(string);
    }

    @Override
    public boolean isPermissionSet(Permission prmsn) {
        return m_parent.isPermissionSet(prmsn);
    }

    @Override
    public boolean hasPermission(String string) {
        return m_parent.hasPermission(string);
    }

    @Override
    public boolean hasPermission(Permission prmsn) {
        return m_parent.hasPermission(prmsn);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
        return m_parent.addAttachment(plugin, string, bln);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return m_parent.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
        return m_parent.addAttachment(plugin, string, bln, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return m_parent.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment pa) {
        m_parent.removeAttachment(pa);
    }

    @Override
    public void recalculatePermissions() {
        m_parent.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return m_parent.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return m_parent.isOp();
    }

    @Override
    public void setOp(boolean bln) {
        m_parent.setOp(bln);
    }

    @Override
    public boolean isConversing() {
        return m_parent.isConversing();
    }

    @Override
    public void acceptConversationInput(String string) {
        m_parent.acceptConversationInput(string);
    }

    @Override
    public boolean beginConversation(Conversation c) {
        return m_parent.beginConversation(c);
    }

    @Override
    public void abandonConversation(Conversation c) {
        m_parent.abandonConversation(c);
    }

    @Override
    public void abandonConversation(Conversation c, ConversationAbandonedEvent cae) {
        m_parent.abandonConversation(c, cae);
    }

    @Override
    public void sendMessage(String string) {
        m_parent.sendMessage(string);
    }

    @Override
    public void sendMessage(String[] strings) {
        m_parent.sendMessage(strings);
    }

    @Override
    public boolean isOnline() {
        return m_parent.isOnline();
    }

    @Override
    public boolean isBanned() {
        return m_parent.isBanned();
    }

    @Override
    public void setBanned(boolean bln) {
        m_parent.setBanned(bln);
    }

    @Override
    public boolean isWhitelisted() {
        return m_parent.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean bln) {
        m_parent.setWhitelisted(bln);
    }

    @Override
    public Player getPlayer() {
        return m_parent.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return m_parent.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return m_parent.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return m_parent.hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize() {
        return m_parent.serialize();
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
        m_parent.sendPluginMessage(plugin, string, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return m_parent.getListeningPluginChannels();
    }

    @Override
    public int _INVALID_getLastDamage() {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public void _INVALID_setLastDamage(int i) {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public void _INVALID_damage(int i) {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public void _INVALID_damage(int i, Entity entity) {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public int _INVALID_getHealth() {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public void _INVALID_setHealth(int i) {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public int _INVALID_getMaxHealth() {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }

    @Override
    public void _INVALID_setMaxHealth(int i) {
        throw new UnsupportedOperationException("This method is not supported use the double variantt.");
    }
}
