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

import java.util.Collection;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author SBPrime
 */
public class BlockWrapper implements Block {

    private final Block m_paretn;

    private final World m_world;

    public BlockWrapper(Block paretn, World world) {
        m_paretn = paretn;
        m_world = world;
    }

    @Override
    public byte getData() {
        return m_paretn.getData();
    }

    @Override
    public Block getRelative(int i, int i1, int i2) {
        return new BlockWrapper(m_paretn.getRelative(i, i1, i2), m_world);
    }

    @Override
    public Block getRelative(BlockFace bf) {
        return new BlockWrapper(m_paretn.getRelative(bf), m_world);
    }

    @Override
    public Block getRelative(BlockFace bf, int i) {
        return new BlockWrapper(m_paretn.getRelative(bf, i), m_world);
    }

    @Override
    public Material getType() {
        return m_paretn.getType();
    }

    @Override
    public int getTypeId() {
        return m_paretn.getTypeId();
    }

    @Override
    public byte getLightLevel() {
        return m_paretn.getLightLevel();
    }

    @Override
    public byte getLightFromSky() {
        return m_paretn.getLightFromSky();
    }

    @Override
    public byte getLightFromBlocks() {
        return m_paretn.getLightFromBlocks();
    }

    @Override
    public World getWorld() {
        //return m_paretn.getWorld();
        return m_world;
    }

    @Override
    public int getX() {
        return m_paretn.getX();
    }

    @Override
    public int getY() {
        return m_paretn.getY();
    }

    @Override
    public int getZ() {
        return m_paretn.getZ();
    }

    @Override
    public Location getLocation() {
        Location l = m_paretn.getLocation();
        
        return new Location(m_world, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    @Override
    public Location getLocation(Location lctn) {
        Location l = m_paretn.getLocation(lctn);
        return new Location(m_world, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    @Override
    public Chunk getChunk() {
        return m_paretn.getChunk();
    }

    @Override
    public void setData(byte b) {
        m_paretn.setData(b);
    }

    @Override
    public void setData(byte b, boolean bln) {
        m_paretn.setData(b, bln);
    }

    @Override
    public void setType(Material mtrl) {
        m_paretn.setType(mtrl);
    }

    @Override
    public boolean setTypeId(int i) {
        return m_paretn.setTypeId(i);
    }

    @Override
    public boolean setTypeId(int i, boolean bln) {
        return m_paretn.setTypeId(i, bln);
    }

    @Override
    public boolean setTypeIdAndData(int i, byte b, boolean bln) {
        return m_paretn.setTypeIdAndData(i, b, bln);
    }

    @Override
    public BlockFace getFace(Block block) {
        return m_paretn.getFace(block);
    }

    @Override
    public BlockState getState() {
        return m_paretn.getState();
    }

    @Override
    public Biome getBiome() {
        return m_paretn.getBiome();
    }

    @Override
    public void setBiome(Biome biome) {
        m_paretn.setBiome(biome);
    }

    @Override
    public boolean isBlockPowered() {
        return m_paretn.isBlockPowered();
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return m_paretn.isBlockIndirectlyPowered();
    }

    @Override
    public boolean isBlockFacePowered(BlockFace bf) {
        return m_paretn.isBlockFacePowered(bf);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace bf) {
        return m_paretn.isBlockFaceIndirectlyPowered(bf);
    }

    @Override
    public int getBlockPower(BlockFace bf) {
        return m_paretn.getBlockPower(bf);
    }

    @Override
    public int getBlockPower() {
        return m_paretn.getBlockPower();
    }

    @Override
    public boolean isEmpty() {
        return m_paretn.isEmpty();
    }

    @Override
    public boolean isLiquid() {
        return m_paretn.isLiquid();
    }

    @Override
    public double getTemperature() {
        return m_paretn.getTemperature();
    }

    @Override
    public double getHumidity() {
        return m_paretn.getHumidity();
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return m_paretn.getPistonMoveReaction();
    }

    @Override
    public boolean breakNaturally() {
        return m_paretn.breakNaturally();
    }

    @Override
    public boolean breakNaturally(ItemStack is) {
        return m_paretn.breakNaturally(is);
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return m_paretn.getDrops();
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack is) {
        return m_paretn.getDrops(is);
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        m_paretn.setMetadata(string, mv);
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return m_paretn.getMetadata(string);
    }

    @Override
    public boolean hasMetadata(String string) {
        return m_paretn.hasMetadata(string);
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        m_paretn.removeMetadata(string, plugin);
    }
}
