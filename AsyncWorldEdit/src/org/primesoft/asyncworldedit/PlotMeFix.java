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
package org.primesoft.asyncworldedit;

import com.worldcretornica.plotme.*;
import org.bukkit.*;
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
public class PlotMeFix implements Listener {
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

            fetchAliases(plugin);

            plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

    private Set<String> clearAliases = new HashSet<>();
    private Set<String> resetAliases = new HashSet<>();

    public void fetchAliases(JavaPlugin plugin) {
        YamlConfiguration yaml;
        File plugins = plugin.getDataFolder().getParentFile();
        File plotme = new File(plugins, "PlotMe");
        File config = new File(plotme, "config.yml");
        if (config.exists()) {
            System.out.println(4);

            yaml = YamlConfiguration.loadConfiguration(config);
            String language = yaml.getString("Language");
            File captions = new File(plugins, "PlotMe" + File.separator + "caption-" + language + ".yml");
            if (captions.exists()) {
                System.out.println(1);
                yaml = YamlConfiguration.loadConfiguration(captions);
                String clear = yaml.getString("CommandClear");
                String reset = yaml.getString("CommandReset");
                File jar = new File(plugins, "PlotMe.jar");
                if (jar.exists()) {
                    System.out.println(2);
                    try {
                        ZipFile zip = new ZipFile(jar);
                        ZipEntry in = zip.getEntry("plugin.yml");
                        InputStream stream = zip.getInputStream(in);
                        YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(stream);
                        List<String> aliases = pluginYml.getStringList("commands.plotme.aliases");
                        aliases.add("plotme");
                        String base = "/%s %s";
                        for (String alias : aliases) {
                            System.out.println(3);

                            clearAliases.add(String.format(base, alias, clear).toLowerCase());
                            resetAliases.add(String.format(base, alias, reset).toLowerCase());
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private boolean isCommand(Set<String> aliases, String command) {
        return aliases.contains(command.toLowerCase());
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        System.out.println(event.getMessage());
        for (String alias : clearAliases) {
            System.out.println("+ + " + alias);
        }
        if (isCommand(clearAliases, event.getMessage())) {
            event.setCancelled(true);
            handlePlotClear(event.getPlayer());
        } else if (isCommand(resetAliases, event.getMessage())) {
            event.setCancelled(true);
            handlePlotReset(event.getPlayer());
            System.out.println("t");
        }
    }

    private boolean handlePlotClear(Player p) {
        if (PlotMe.cPerms(p, "PlotMe.admin.clear") || PlotMe.cPerms(p, "PlotMe.use.clear")) {
            if (!PlotManager.isPlotWorld(p)) {
                Send(p, RED + C("MsgNotPlotWorld"));
            } else {
                String id = PlotManager.getPlotId(p.getLocation());
                if (id.equals("")) {
                    Send(p, RED + C("MsgNoPlotFound"));
                } else {
                    if (!PlotManager.isPlotAvailable(id, p)) {
                        Plot plot = PlotManager.getPlotById(p, id);

                        if (plot.protect) {
                            Send(p, RED + C("MsgPlotProtectedCannotClear"));
                        } else {
                            String playername = p.getName();

                            if (plot.owner.equalsIgnoreCase(playername) || PlotMe.cPerms(p, "PlotMe.admin.clear")) {

                                clear(p, plot);
                                Send(p, C("MsgPlotCleared"));

                                if (isAdv)
                                    PlotMe.logger.info(LOG + playername + " " + C("MsgClearedPlot") + " " + id);
                            } else {
                                Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgNotYoursNotAllowedClear"));
                            }
                        }
                    } else {
                        Send(p, RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
                    }
                }
            }
        } else {
            Send(p, RED + C("MsgPermissionDenied"));
        }
        return true;
    }

    private boolean handlePlotReset(Player p) {
        if (PlotMe.cPerms(p, "PlotMe.admin.reset")) {
            if (!PlotManager.isPlotWorld(p)) {
                Send(p, RED + C("MsgNotPlotWorld"));
            } else {
                Plot plot = PlotManager.getPlotById(p.getLocation());

                if (plot == null) {
                    Send(p, RED + C("MsgNoPlotFound"));
                } else {
                    if (plot.protect) {
                        Send(p, RED + C("MsgPlotProtectedCannotReset"));
                    } else {
                        String id = plot.id;
                        World w = p.getWorld();

                        PlotManager.setBiome(w, id, plot, Biome.PLAINS);
                        clear(p, plot);

                        if (!PlotManager.isPlotAvailable(id, p)) {
                            PlotManager.getPlots(p).remove(id);
                        }

                        String name = p.getName();

                        PlotManager.removeOwnerSign(w, id);
                        PlotManager.removeSellSign(w, id);

                        SqlManager.deletePlot(PlotManager.getIdX(id), PlotManager.getIdZ(id), w.getName().toLowerCase());

                        Send(p, C("MsgPlotReset"));

                        if (isAdv)
                            PlotMe.logger.info(LOG + name + " " + C("MsgResetPlot") + " " + id);
                    }
                }
            }
        } else {
            Send(p, RED + C("MsgPermissionDenied"));
        }
        return true;
    }

    public static void clear(Player player, Plot plot) {
        Location bottom = PlotManager.getPlotBottomLoc(player.getWorld(), plot.id);
        Location top = PlotManager.getPlotTopLoc(player.getWorld(), plot.id);

        PlotMapInfo pmi = PlotManager.getMap(bottom);

        int bottomX = bottom.getBlockX();
        int topX = top.getBlockX();
        int bottomZ = bottom.getBlockZ();
        int topZ = top.getBlockZ();

        int minChunkX = (int) Math.floor((double) bottomX / 16);
        int maxChunkX = (int) Math.floor((double) topX / 16);
        int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ = (int) Math.floor((double) topZ / 16);

        World w = bottom.getWorld();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                Chunk chunk = w.getChunkAt(cx, cz);

                for (Entity e : chunk.getEntities()) {
                    Location eloc = e.getLocation();

                    if (!(e instanceof Player) && eloc.getBlockX() >= bottom.getBlockX() && eloc.getBlockX() <= top.getBlockX() &&
                            eloc.getBlockZ() >= bottom.getBlockZ() && eloc.getBlockZ() <= top.getBlockZ()) {
                        e.remove();
                    }
                }
            }
        }

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block block = new Location(w, x, 0, z).getBlock();

                block.setBiome(Biome.PLAINS);

                for (int y = w.getMaxHeight(); y >= 0; y--) {
                    block = new Location(w, x, y, z).getBlock();

                    BlockState state = block.getState();

                    if (state instanceof InventoryHolder) {
                        InventoryHolder holder = (InventoryHolder) state;
                        holder.getInventory().clear();
                    }


                    if (state instanceof Jukebox) {
                        Jukebox jukebox = (Jukebox) state;
                        //Remove once they fix the NullPointerException
                        try {
                            jukebox.setPlaying(Material.AIR);
                        } catch (Exception e) {
                        }
                    }


                    if (y == 0)
                        block.setTypeId(pmi.BottomBlockId);
                    else if (y < pmi.RoadHeight)
                        block.setTypeId(pmi.PlotFillingBlockId);
                    else if (y == pmi.RoadHeight)
                        block.setTypeId(pmi.PlotFloorBlockId);
                    else {
                        if (y == (pmi.RoadHeight + 1) &&
                                (x == bottomX - 1 ||
                                        x == topX + 1 ||
                                        z == bottomZ - 1 ||
                                        z == topZ + 1)) {
                            //block.setTypeId(pmi.WallBlockId);
                        } else {
                            block.setTypeIdAndData(0, (byte) 0, false); //.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        PlotManager.adjustWall(bottom);
    }

    private final ChatColor BLUE = ChatColor.BLUE;
    private final ChatColor RED = ChatColor.RED;
    private final ChatColor RESET = ChatColor.RESET;
    private final ChatColor AQUA = ChatColor.AQUA;
    private final ChatColor GREEN = ChatColor.GREEN;
    private final ChatColor ITALIC = ChatColor.ITALIC;
    private final String PREFIX = PlotMe.PREFIX;
    private final String LOG = "[" + PlotMe.NAME + " Event] ";
    private final boolean isAdv = PlotMe.advancedlogging;

    private void Send(CommandSender cs, String text) {
        cs.sendMessage(PlotMe.PREFIX + text);
    }

    private String C(String caption) {
        return PlotMe.caption(caption);
    }

}
