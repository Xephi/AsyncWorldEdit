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

import org.primesoft.asyncworldedit.bukkit.BlockWrapper;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 *
 * @author SBPrime
 */
public class WorldWrapper implements World {

    private final World m_parent;

    public WorldWrapper(World parent) {
        m_parent = parent;
    }

    @Override
    public Block getBlockAt(int i, int i1, int i2) {
        return new BlockWrapper(m_parent.getBlockAt(i, i1, i2), this);
    }

    @Override
    public Block getBlockAt(Location lctn) {
        return new BlockWrapper(m_parent.getBlockAt(lctn), this);
    }

    @Override
    public int getBlockTypeIdAt(int i, int i1, int i2) {
        return m_parent.getBlockTypeIdAt(i, i1, i2);
    }

    @Override
    public int getBlockTypeIdAt(Location lctn) {
        return m_parent.getBlockTypeIdAt(lctn);
    }

    @Override
    public int getHighestBlockYAt(int i, int i1) {
        return m_parent.getHighestBlockYAt(i, i1);
    }

    @Override
    public int getHighestBlockYAt(Location lctn) {
        return m_parent.getHighestBlockYAt(lctn);
    }

    @Override
    public Block getHighestBlockAt(int i, int i1) {
        return new BlockWrapper(m_parent.getHighestBlockAt(i, i1), this);
    }

    @Override
    public Block getHighestBlockAt(Location lctn) {
        return new BlockWrapper(m_parent.getHighestBlockAt(lctn), this);
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        return m_parent.getChunkAt(i, i1);
    }

    @Override
    public Chunk getChunkAt(Location lctn) {
        return m_parent.getChunkAt(lctn);
    }

    @Override
    public Chunk getChunkAt(Block block) {
        return m_parent.getChunkAt(block);
    }

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        return m_parent.isChunkLoaded(chunk);
    }

    @Override
    public Chunk[] getLoadedChunks() {
        return m_parent.getLoadedChunks();
    }

    @Override
    public void loadChunk(Chunk chunk) {
        m_parent.loadChunk(chunk);
    }

    @Override
    public boolean isChunkLoaded(int i, int i1) {
        return m_parent.isChunkLoaded(i, i1);
    }

    @Override
    public boolean isChunkInUse(int i, int i1) {
        return m_parent.isChunkInUse(i, i1);
    }

    @Override
    public void loadChunk(int i, int i1) {
        m_parent.loadChunk(i, i1);
    }

    @Override
    public boolean loadChunk(int i, int i1, boolean bln) {
        return m_parent.loadChunk(i, i1, bln);
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return m_parent.unloadChunk(chunk);
    }

    @Override
    public boolean unloadChunk(int i, int i1) {
        return m_parent.unloadChunk(i, i1);
    }

    @Override
    public boolean unloadChunk(int i, int i1, boolean bln) {
        return m_parent.unloadChunk(i, i1, bln);
    }

    @Override
    public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1) {
        return m_parent.unloadChunk(i, i1, bln, bln1);
    }

    @Override
    public boolean unloadChunkRequest(int i, int i1) {
        return m_parent.unloadChunkRequest(i, i1);
    }

    @Override
    public boolean unloadChunkRequest(int i, int i1, boolean bln) {
        return m_parent.unloadChunkRequest(i, i1, bln);
    }

    @Override
    public boolean regenerateChunk(int i, int i1) {
        return m_parent.regenerateChunk(i, i1);
    }

    @Override
    public boolean refreshChunk(int i, int i1) {
        return m_parent.refreshChunk(i, i1);
    }

    @Override
    public Item dropItem(Location lctn, ItemStack is) {
        return m_parent.dropItem(lctn, is);
    }

    @Override
    public Item dropItemNaturally(Location lctn, ItemStack is) {
        return m_parent.dropItemNaturally(lctn, is);
    }

    @Override
    public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1) {
        return m_parent.spawnArrow(lctn, vector, f, f1);
    }

    @Override
    public boolean generateTree(Location lctn, TreeType tt) {
        return m_parent.generateTree(lctn, tt);
    }

    @Override
    public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd) {
        return m_parent.generateTree(lctn, tt, bcd);
    }

    @Override
    public Entity spawnEntity(Location lctn, EntityType et) {
        return m_parent.spawnEntity(lctn, et);
    }

    @Override
    public LivingEntity spawnCreature(Location lctn, EntityType et) {
        return m_parent.spawnCreature(lctn, et);
    }

    @Override
    public LivingEntity spawnCreature(Location lctn, CreatureType ct) {
        return m_parent.spawnCreature(lctn, ct);
    }

    @Override
    public LightningStrike strikeLightning(Location lctn) {
        return m_parent.strikeLightning(lctn);
    }

    @Override
    public LightningStrike strikeLightningEffect(Location lctn) {
        return m_parent.strikeLightningEffect(lctn);
    }

    @Override
    public List<Entity> getEntities() {
        return m_parent.getEntities();
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        return m_parent.getLivingEntities();
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types) {
        return m_parent.getEntitiesByClass(types);
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type) {
        return m_parent.getEntitiesByClass(type);
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... types) {
        return m_parent.getEntitiesByClasses(types);
    }

    @Override
    public List<Player> getPlayers() {
        return m_parent.getPlayers();
    }

    @Override
    public String getName() {
        return m_parent.getName();
    }

    @Override
    public UUID getUID() {
        return m_parent.getUID();
    }

    @Override
    public Location getSpawnLocation() {
        return m_parent.getSpawnLocation();
    }

    @Override
    public boolean setSpawnLocation(int i, int i1, int i2) {
        return m_parent.setSpawnLocation(i, i1, i2);
    }

    @Override
    public long getTime() {
        return m_parent.getTime();
    }

    @Override
    public void setTime(long l) {
        m_parent.setTime(l);
    }

    @Override
    public long getFullTime() {
        return m_parent.getFullTime();
    }

    @Override
    public void setFullTime(long l) {
        m_parent.setFullTime(l);
    }

    @Override
    public boolean hasStorm() {
        return m_parent.hasStorm();
    }

    @Override
    public void setStorm(boolean bln) {
        m_parent.setStorm(bln);
    }

    @Override
    public int getWeatherDuration() {
        return m_parent.getWeatherDuration();
    }

    @Override
    public void setWeatherDuration(int i) {
        m_parent.setWeatherDuration(i);
    }

    @Override
    public boolean isThundering() {
        return m_parent.isThundering();
    }

    @Override
    public void setThundering(boolean bln) {
        m_parent.setThundering(bln);
    }

    @Override
    public int getThunderDuration() {
        return m_parent.getThunderDuration();
    }

    @Override
    public void setThunderDuration(int i) {
        m_parent.setThunderDuration(i);
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f) {
        return m_parent.createExplosion(d, d1, d2, f);
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f, boolean bln) {
        return m_parent.createExplosion(d, d1, d2, f, bln);
    }

    @Override
    public boolean createExplosion(double d, double d1, double d2, float f, boolean bln, boolean bln1) {
        return m_parent.createExplosion(d, d1, d2, f, bln, bln1);
    }

    @Override
    public boolean createExplosion(Location lctn, float f) {
        return m_parent.createExplosion(lctn, f);
    }

    @Override
    public boolean createExplosion(Location lctn, float f, boolean bln) {
        return m_parent.createExplosion(lctn, f, bln);
    }

    @Override
    public Environment getEnvironment() {
        return m_parent.getEnvironment();
    }

    @Override
    public long getSeed() {
        return m_parent.getSeed();
    }

    @Override
    public boolean getPVP() {
        return m_parent.getPVP();
    }

    @Override
    public void setPVP(boolean bln) {
        m_parent.setPVP(bln);
    }

    @Override
    public ChunkGenerator getGenerator() {
        return m_parent.getGenerator();
    }

    @Override
    public void save() {
        m_parent.save();
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        return m_parent.getPopulators();
    }

    @Override
    public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException {
        return m_parent.spawn(lctn, type);
    }

    @Override
    public FallingBlock spawnFallingBlock(Location lctn, Material mtrl, byte b) throws IllegalArgumentException {
        return m_parent.spawnFallingBlock(lctn, mtrl, b);
    }

    @Override
    public FallingBlock spawnFallingBlock(Location lctn, int i, byte b) throws IllegalArgumentException {
        return m_parent.spawnFallingBlock(lctn, i, b);
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i) {
        m_parent.playEffect(lctn, effect, i);
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i, int i1) {
        m_parent.playEffect(lctn, effect, i, i1);
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
        m_parent.playEffect(lctn, effect, t);
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t, int i) {
        m_parent.playEffect(lctn, effect, t, i);
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1) {
        return m_parent.getEmptyChunkSnapshot(i, i1, bln, bln1);
    }

    @Override
    public void setSpawnFlags(boolean bln, boolean bln1) {
        m_parent.setSpawnFlags(bln, bln1);
    }

    @Override
    public boolean getAllowAnimals() {
        return m_parent.getAllowAnimals();
    }

    @Override
    public boolean getAllowMonsters() {
        return m_parent.getAllowMonsters();
    }

    @Override
    public Biome getBiome(int i, int i1) {
        return m_parent.getBiome(i, i1);
    }

    @Override
    public void setBiome(int i, int i1, Biome biome) {
        m_parent.setBiome(i, i1, biome);
    }

    @Override
    public double getTemperature(int i, int i1) {
        return m_parent.getTemperature(i, i1);
    }

    @Override
    public double getHumidity(int i, int i1) {
        return m_parent.getHumidity(i, i1);
    }

    @Override
    public int getMaxHeight() {
        return m_parent.getMaxHeight();
    }

    @Override
    public int getSeaLevel() {
        return m_parent.getSeaLevel();
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return m_parent.getKeepSpawnInMemory();
    }

    @Override
    public void setKeepSpawnInMemory(boolean bln) {
        m_parent.setKeepSpawnInMemory(bln);
    }

    @Override
    public boolean isAutoSave() {
        return m_parent.isAutoSave();
    }

    @Override
    public void setAutoSave(boolean bln) {
        m_parent.setAutoSave(bln);
    }

    @Override
    public void setDifficulty(Difficulty dfclt) {
        m_parent.setDifficulty(dfclt);
    }

    @Override
    public Difficulty getDifficulty() {
        return m_parent.getDifficulty();
    }

    @Override
    public File getWorldFolder() {
        return m_parent.getWorldFolder();
    }

    @Override
    public WorldType getWorldType() {
        return m_parent.getWorldType();
    }

    @Override
    public boolean canGenerateStructures() {
        return m_parent.canGenerateStructures();
    }

    @Override
    public long getTicksPerAnimalSpawns() {
        return m_parent.getTicksPerAnimalSpawns();
    }

    @Override
    public void setTicksPerAnimalSpawns(int i) {
        m_parent.setTicksPerAnimalSpawns(i);
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        return m_parent.getTicksPerMonsterSpawns();
    }

    @Override
    public void setTicksPerMonsterSpawns(int i) {
        m_parent.setTicksPerMonsterSpawns(i);
    }

    @Override
    public int getMonsterSpawnLimit() {
        return m_parent.getMonsterSpawnLimit();
    }

    @Override
    public void setMonsterSpawnLimit(int i) {
        m_parent.setMonsterSpawnLimit(i);
    }

    @Override
    public int getAnimalSpawnLimit() {
        return m_parent.getAnimalSpawnLimit();
    }

    @Override
    public void setAnimalSpawnLimit(int i) {
        m_parent.setAnimalSpawnLimit(i);
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return m_parent.getWaterAnimalSpawnLimit();
    }

    @Override
    public void setWaterAnimalSpawnLimit(int i) {
        m_parent.setWaterAnimalSpawnLimit(i);
    }

    @Override
    public int getAmbientSpawnLimit() {
        return m_parent.getAmbientSpawnLimit();
    }

    @Override
    public void setAmbientSpawnLimit(int i) {
        m_parent.setAmbientSpawnLimit(i);
    }

    @Override
    public void playSound(Location lctn, Sound sound, float f, float f1) {
        m_parent.playSound(lctn, sound, f, f1);
    }

    @Override
    public String[] getGameRules() {
        return m_parent.getGameRules();
    }

    @Override
    public String getGameRuleValue(String string) {
        return m_parent.getGameRuleValue(string);
    }

    @Override
    public boolean setGameRuleValue(String string, String string1) {
        return m_parent.setGameRuleValue(string, string1);
    }

    @Override
    public boolean isGameRule(String string) {
        return m_parent.isGameRule(string);
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
}
