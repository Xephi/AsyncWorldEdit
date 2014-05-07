/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
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
package org.primesoft.asyncworldedit.plotme;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.worldcretornica.plotme.*;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.worldedit.WorldeditIntegrator;
import org.yaml.snakeyaml.Yaml;
import sun.org.mozilla.javascript.internal.ContextFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class is used to fix PlotMe Mask seting errors
 *
 * @author SBPrime
 */
public class PlotMeFix {
    /**
     * Is PlotMe fix enabled
     */
    private boolean m_isEnabled;

    /**
     * New instance of PlotMe plugin
     *
     * @param plugin
     */
    public PlotMeFix(JavaPlugin plugin) {
        m_isEnabled = false;
        if (ConfigProvider.isPlotMeFixEnabled()) {
            try {
                Plugin cPlugin = plugin.getServer().getPluginManager().getPlugin("PlotMe");

                if ((cPlugin != null) && (cPlugin instanceof PlotMe)) {
                    m_isEnabled = true;
                }
            } catch (NoClassDefFoundError ex) {
            }

            if (m_isEnabled) {
                PluginMain.log("PlotMe fix enabled.");
            }

        }
    }


    public void setMask(Player p) {
        if (!m_isEnabled || p == null) {
            return;
        }

        if (PlotManager.isPlotWorld(p)) {
            if (!PlotMe.isIgnoringWELimit(p)) {
                PlotWorldEdit.setMask(p);
            } else {
                PlotWorldEdit.removeMask(p);
            }
        }
    }

}
